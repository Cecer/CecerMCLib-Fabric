package com.cecer1.projects.mc.cecermclib.fabric.modules.text;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.modules.text.ChatInputMutateCallback;
import com.cecer1.projects.mc.cecermclib.common.modules.text.TextColor;
import com.cecer1.projects.mc.cecermclib.common.modules.text.WrappedComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class WrappedFabricComponent extends WrappedComponent<MutableText> {

    public WrappedFabricComponent(MutableText component) {
        super(component);
    }
    
    @Override
    public String getPlainString() {
        return Formatting.strip(this.getComponent().getString());
    }
    @Override
    public String getFormattedString() {
        return this.getComponent().toString();
    }
    @Override
    public String getJson() {
        return Text.Serializer.toJson(this.getComponent());
    }

    @Override
    public TextColor getColor() {
        net.minecraft.text.TextColor color = this.getComponent().getStyle().getColor();
        if (color == null) {
            return null;
        }
        return TextColor.fromName(color.getName().toLowerCase());
    }
    @Override
    public boolean isBold() {
        return this.getComponent().getStyle().isBold();
    }
    @Override
    public boolean isItalic() {
        return this.getComponent().getStyle().isItalic();
    }
    @Override
    public boolean isUnderline() {
        return this.getComponent().getStyle().isUnderlined();
    }
    @Override
    public boolean isStrikethrough() {
        return this.getComponent().getStyle().isStrikethrough();
    }
    @Override
    public boolean isObfuscated() {
        return this.getComponent().getStyle().isObfuscated();
    }

    @Override
    public void appendChild(MutableText child) {
        this.getComponent().append(child);
    }

    @Override
    public WrappedComponent<MutableText>[] getChildren() {
        List<Text> childList = this.getComponent().getSiblings();
        WrappedFabricComponent[] children = new WrappedFabricComponent[childList.size()];
        for (int i = 0; i < childList.size(); i++) {
            Text child = childList.get(i);
            if (child instanceof MutableText) {
                children[i] = new WrappedFabricComponent((MutableText) child);
            } else {
                children[i] = new WrappedFabricComponent(new LiteralText(child.getString()));
            }
        }
        return children;
    }

    @Override
    public WrappedComponent<MutableText> getCopyWithoutChildren() {
        MutableText copy = this.getComponent().shallowCopy();
        copy.getSiblings().clear();
        return new WrappedFabricComponent(copy);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void handleClick() {
        if (Screen.hasShiftDown() && this.getComponent().getStyle().getInsertion() != null) {
            ChatInputMutateCallback.EVENT.invoker().handle(this.getComponent().getStyle().getInsertion(), false);
        } else {
            ClickEvent clickEvent = this.getComponent().getStyle().getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                ChatInputMutateCallback.EVENT.invoker().handle(clickEvent.getValue(), true);
            } else {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                if (screen != null) {
                    screen.handleTextClick(this.getComponent().getStyle());
                }
            }
        }
    }
}