package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.fbo;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class FBOTexture extends AbstractTexture {

    private final Identifier identifier;
    private final int width;
    private final int height;

    public FBOTexture(int framebufferId, int width, int height) {
        this.identifier = new Identifier("cecermclib", "fbotexture/" + framebufferId);
        this.width = width;
        this.height = height;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public void load(ResourceManager manager) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(this::prepare);
        } else {
            this.prepare();
        }
    }

    private void prepare() {
    }
}
