package com.cecer1.projects.mc.cecermclib.fabric.modules.input.mouse;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.*;

public class MouseInputManager {

    private int lastMouseX = -1;
    private int lastMouseY = -1;

    private final List<MouseRegionHandler> mouseRegionHandlers;
    private MouseRegionHandler handlerUnderCursor;
    private final Set<Integer> halfPressedButtons = new HashSet<>();

    public MouseInputManager() {
        this.mouseRegionHandlers = new ArrayList<>();
        this.handlerUnderCursor = null;
    }
    
    public void registerEvents() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            this.onCursorActivated();
            this.registerEvents(screen);
        });
        
        // Track mouse movement every frame
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (this.lastMouseX != -1) {
                MinecraftClient mc = MinecraftClient.getInstance();
                int trueMouseX = (int) mc.mouse.getX();
                int trueMouseY = (int) mc.mouse.getY();
                
                if (this.lastMouseX != trueMouseX || this.lastMouseY != trueMouseY) {
                    this.onMouseMove(trueMouseX, trueMouseY);
                }
            }
        });
    }
    private void registerEvents(Screen screen) {
        ScreenEvents.remove(screen).register(ignore -> this.onCursorDeactivated());
        ScreenMouseEvents.allowMouseClick(screen).register((ignore, ignore2, ignore3, button) -> this.onMouseDown(button));
        ScreenMouseEvents.allowMouseRelease(screen).register((ignore, ignore2, ignore3, button) -> this.onMouseUp(button));
        ScreenMouseEvents.allowMouseScroll(screen).register((ignore, ignore2, ignore3, horizontalAmount, verticalAmount) -> this.onScroll(horizontalAmount, verticalAmount));
    }

    public void registerHandler(MouseRegionHandler handler) {
        this.mouseRegionHandlers.add(handler);
    }

    public void clearHandlers() {
        this.mouseRegionHandlers.clear();
    }
    
    
    public void onCursorActivated() {
        MinecraftClient mc = MinecraftClient.getInstance();
        int trueMouseX = (int) mc.mouse.getX();
        int trueMouseY = (int) (mc.getWindow().getHeight() - mc.mouse.getY());
        
        MouseRegionHandler handler = this.getTopHandler(trueMouseX, trueMouseY);
        if (handler != null) {
            this.handleCursorEnter(handler, trueMouseX, trueMouseY);
        }

        this.lastMouseX = trueMouseX;
        this.lastMouseY = trueMouseY;
    }
    public void onCursorDeactivated() {
        if (this.handlerUnderCursor != null) {
            this.handleCursorExit(this.handlerUnderCursor, this.lastMouseX, this.lastMouseY);
        }
        this.lastMouseX = -1;
        this.lastMouseY = -1;
    }

    public boolean onMouseDown(int button) {
        if (this.handlerUnderCursor != null) {
            return this.handleMouseDown(this.handlerUnderCursor, button);
        }
        return true;
    }
    public boolean onMouseUp(int button) {
        if (this.handlerUnderCursor != null) {
            return this.handleMouseUp(this.handlerUnderCursor, button);
        }
        return true;
    }

    public void onMouseMove(int trueMouseX, int trueMouseY) {
        MouseRegionHandler newHandlerUnderCursor = this.getTopHandler(trueMouseX, trueMouseY);
        
        if (this.handlerUnderCursor != newHandlerUnderCursor) {
            if (this.handlerUnderCursor != null) {
                this.handleCursorExit(this.handlerUnderCursor, trueMouseX, trueMouseY); // TODO: Would this maybe want the old position? Maybe both?
            }
            if (newHandlerUnderCursor != null) {
                this.handleCursorEnter(newHandlerUnderCursor, trueMouseX, trueMouseY);
            }
        } else if (this.handlerUnderCursor != null) {
            this.handleMouseMove(this.handlerUnderCursor, this.lastMouseX, this.lastMouseY, trueMouseX, trueMouseY);
        }

        this.lastMouseX = trueMouseX;
        this.lastMouseY = trueMouseY;
    }

    public boolean onScroll(double horizontalAmount, double verticalAmount) {
        if (this.handlerUnderCursor != null) {
            return this.handleScroll(this.handlerUnderCursor, horizontalAmount, verticalAmount);
        }
        return true;
    }


    private void handleCursorEnter(MouseRegionHandler handler, int trueMouseX, int trueMouseY) {
        this.handlerUnderCursor = handler;

        if (handler != null) {
            handler.onMouseEnter(trueMouseX, trueMouseY);
        }
    }
    private void handleCursorExit(MouseRegionHandler handler, int trueMouseX, int trueMouseY) {
        this.handlerUnderCursor = null;
        this.halfPressedButtons.clear();

        if (handler != null) {
            handler.onMouseExit(trueMouseX, trueMouseY);
        }
    }
    private boolean handleMouseDown(MouseRegionHandler handler, int button) {
        boolean allow = handler.onMouseDown(button);
        this.halfPressedButtons.add(button);
        return allow;
    }
    private boolean handleMouseUp(MouseRegionHandler handler, int button) {
        boolean allow = handler.onMouseUp(button);
        if (this.halfPressedButtons.contains(button)) {
            allow |= handler.onClick(button);
        }
        this.halfPressedButtons.remove(button);
        return allow;
    }
    private void handleMouseMove(MouseRegionHandler handler, int lastMouseX, int lastMouseY, int trueMouseX, int trueMouseY) {
        handler.onMouseMove(lastMouseX, lastMouseY, trueMouseX, trueMouseY);
    }
    private boolean handleScroll(MouseRegionHandler handler, double horizontalAmount, double verticalAmount) {
        return handler.onScroll(horizontalAmount, verticalAmount);
    }


    private boolean isMouseOver(MouseRegionHandler handler, int x, int y) {
        if (handler == null) {
            return false;
        }

        if (x < handler.getMinX()) {
            return false;
        }
        if (x > handler.getMaxX()) {
            return false;
        }
        if (y < handler.getMinY()) {
            return false;
        }
        if (y > handler.getMaxY()) {
            return false;
        }
        return true;
    }

    private MouseRegionHandler getTopHandler(int x, int y) {
        if (x == -1 && y == -1) {
            return null;
        }

        ListIterator<MouseRegionHandler> iterator = this.mouseRegionHandlers.listIterator(this.mouseRegionHandlers.size());
        while (iterator.hasPrevious()) {
            MouseRegionHandler handler = iterator.previous();
            if (this.isMouseOver(handler, x, y)) {
                return handler;
            }
        }
        return null;
    }

    public Iterable<MouseRegionHandler> getHandlersIterator() {
        return this.mouseRegionHandlers;
    }
}
