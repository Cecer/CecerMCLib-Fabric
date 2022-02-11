package com.cecer1.projects.mc.cecermclib.fabric.modules.text;

import com.cecer1.projects.mc.cecermclib.common.modules.text.ITextAdapter;
import com.cecer1.projects.mc.cecermclib.common.modules.text.WrappedComponent;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.Click;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.Hover;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.components.AbstractXmlTextComponent;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.components.TextXmlTextComponent;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class FabricTextAdapter implements ITextAdapter<MutableText> {

    private TextRenderer getFontRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public int getStringWidth(String s) {
        return this.getFontRenderer().getWidth(s);
    }
    @Override
    public WrappedComponent<MutableText> newRootComponent() {
        return new WrappedFabricComponent(new LiteralText(""));
    }

    @Override
    public WrappedComponent<MutableText> adapt(Collection<AbstractXmlTextComponent> parts) {
        WrappedComponent<MutableText> out = ITextAdapter.super.adapt(parts);
        if (out == null) {
            out = new WrappedFabricComponent(new LiteralText("Error: Failed to compile XML message!"));
        }
        return out;
    }
    @Override
    public void appendPart(WrappedComponent<MutableText> fullComponent, TextXmlTextComponent part) {
        MutableText partComponent = new LiteralText(part.getText());
        
        Style style = partComponent.getStyle()
                .withBold(part.getState().isBold())
                .withItalic(part.getState().isItalic())
                .withUnderline(part.getState().isUnderlined())
                //.withStrikethrough(part.getState().isStrikethrough()) // TODO: This is not implemented! Decide what to do
                //.withObfuscated(part.getState().isObfuscated()) // TODO: This is not implemented! Decide what to do
                .withColor(TextColor.parse(part.getState().getColor().getValue()))
                .withInsertion(part.getState().getInsertion())
                .withFont(new Identifier(part.getState().getFont()));
        
        if (part.getState().getClick() != Click.NONE) {
            style = style.withClickEvent(this.adaptClick(part.getState().getClick()));
        }
        if (part.getState().getHover() != Hover.NONE) {
            style = style.withHoverEvent(this.adaptHover(part.getState().getHover()));
        }
        fullComponent.appendChild(partComponent.setStyle(style));
    }
    private ClickEvent adaptClick(Click click) {
        if (click instanceof Click.OpenFile) {
            return new ClickEvent(ClickEvent.Action.OPEN_FILE, ((Click.OpenFile) click).getPath());
        }
        if (click instanceof Click.RunCommand) {
            return new ClickEvent(ClickEvent.Action.RUN_COMMAND, ((Click.RunCommand) click).getCommand());
        }
        if (click instanceof Click.Suggest) {
            return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ((Click.Suggest) click).getSuggest());
        }
        if (click instanceof Click.ChangePage) {
            return new ClickEvent(ClickEvent.Action.CHANGE_PAGE, ((Click.ChangePage) click).getPage());
        }
        if (click instanceof Click.Url) {
            return new ClickEvent(ClickEvent.Action.OPEN_URL, ((Click.Url) click).getUrl());
        }
        throw new IllegalArgumentException("Unsupported click type: " + click.getClass().getName());
    }
    private HoverEvent adaptHover(Hover hover) {
        if (hover instanceof Hover.Text) {
            return HoverEvent.Action.SHOW_TEXT.buildHoverEvent(JsonParser.parseString(((Hover.Text) hover).getJson()));
        }
        if (hover instanceof Hover.Item) {
            return HoverEvent.Action.SHOW_ITEM.buildHoverEvent(JsonParser.parseString(((Hover.Item) hover).getJson()));
        }
        if (hover instanceof Hover.Entity) {
            return HoverEvent.Action.SHOW_ENTITY.buildHoverEvent(JsonParser.parseString(((Hover.Entity) hover).getJson()));
        }
        if (hover instanceof Hover.Segment) {
            WrappedComponent<MutableText> segment = this.adapt(((Hover.Segment) hover).getComponentList());
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, segment.getComponent());
        }
        throw new IllegalArgumentException("Unsupported hover type: " + hover.getClass().getName());
    }

    @Override
    public int getFontHeight() {
        return this.getFontRenderer().fontHeight;
    }
}