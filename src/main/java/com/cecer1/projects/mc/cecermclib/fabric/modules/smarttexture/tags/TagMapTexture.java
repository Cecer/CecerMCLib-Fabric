package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TagMapTexture {
    private final BufferedImage bufferedImage;

    public TagMapTexture(Identifier textureResourceLocation, ResourceManager resourceManager) {
        BufferedImage image;
        try(InputStream inputStream = resourceManager.getResource(textureResourceLocation).getInputStream()) {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            image = null;
        }
        this.bufferedImage = image;
    }

    public int getPixelColor(int x, int y) {
        if (this.bufferedImage == null) {
            return 0;
        }
        return this.bufferedImage.getRGB(x, y);
    }
}
