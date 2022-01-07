package com.cecer1.projects.mc.cecermclib.fabric.modules.input.keyboard;

import com.cecer1.projects.mc.cecermclib.common._misc.collections.Tree;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class KeyboardInputManager {

    private final TabCycleGroup rootHandler = new RootKeyboardInputHandler();

    private final Tree<IKeyboardInputHandler> handlers = new Tree<>(rootHandler);
    private Tree<IKeyboardInputHandler> leafHandlerTree;

    public Tree<IKeyboardInputHandler> getHandlers() {
        return this.handlers;
    }
    public Tree<IKeyboardInputHandler> getLeafHandlerTree() {
        return this.leafHandlerTree;
    }
    public void setLeafHandlerTree(Tree<IKeyboardInputHandler> leafHandlerTree) {
        this.leafHandlerTree = leafHandlerTree;
    }
    
    public void registerEvents() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.allowKeyPress(screen).register(this::onAllowKeyPress);
            ScreenKeyboardEvents.allowKeyRelease(screen).register(this::onAllowKeyRelease);
        });
    }
    
    @FunctionalInterface
    private interface AllowKeyEventConsumer {
        IKeyboardInputHandler.KeyboardInputResult handle(int key, int scancode, int modifiers);
    }
    private boolean onAllowKeyPress(Screen screen, int key, int scancode, int modifiers) {
        return this.onAllowKeyEvent(key, scancode, modifiers, handler -> handler::onKeyboardKeyDown);
    }
    private boolean onAllowKeyRelease(Screen screen, int key, int scancode, int modifiers) {
        return this.onAllowKeyEvent(key, scancode, modifiers, handler -> handler::onKeyboardKeyUp);
    }

    private boolean onAllowKeyEvent(int key, int scancode, int modifiers, Function<IKeyboardInputHandler, AllowKeyEventConsumer> handlerFunc) {
        Tree<IKeyboardInputHandler> handlerTree = this.getLeafHandlerTree();
        while (handlerTree != null) {
            IKeyboardInputHandler handler = handlerTree.getValue();
            IKeyboardInputHandler.KeyboardInputResult result;

            if (handler == null) {
                return true;
            } else {
                result = handlerFunc.apply(handler).handle(key, scancode, modifiers);
            }

            switch (result) {
                case BUBBLE_UP: {
                    handlerTree = handlerTree.getParent();
                    break;
                }
                case CONSUME: {
                    return false;
                }
                case PASSIVE: {
                    return true;
                }
            }
        }
        return true;
    }

    public Tree<IKeyboardInputHandler> tabCycle(boolean backwards) {
        Tree<IKeyboardInputHandler> handler = this.getLeafHandlerTree();
        while (!(handler.getValue() instanceof TabCycleGroup)) {
            handler = handler.getParent();
        }
        if (backwards) {
            return ((TabCycleGroup) handler.getValue()).tabCycleBack();
        } else {
            return ((TabCycleGroup) handler.getValue()).tabCycleNext();
        }
    }
}
