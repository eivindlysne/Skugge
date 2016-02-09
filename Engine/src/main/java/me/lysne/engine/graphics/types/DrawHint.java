package me.lysne.engine.graphics.types;

import org.lwjgl.opengl.GL15;

public enum DrawHint {

    STATIC(GL15.GL_STATIC_DRAW),
    DYNAMIC(GL15.GL_DYNAMIC_DRAW),
    STREAM(GL15.GL_STREAM_DRAW);

    public final int value;

    DrawHint(int value) {
        this.value = value;
    }
}
