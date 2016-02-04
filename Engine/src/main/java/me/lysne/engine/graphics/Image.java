package me.lysne.engine.graphics;

import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Image {

    public final ByteBuffer data;
    public final int width;
    public final int height;
    public final int numComponents;

    public Image(ByteBuffer data, int width, int height, int numComponents) {

        this.data = data;
        this.width = width;
        this.height = height;
        this.numComponents = numComponents;
    }

    public void destroy() {

        STBImage.stbi_image_free(data);
    }
}