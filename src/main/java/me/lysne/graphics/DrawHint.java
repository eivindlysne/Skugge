package me.lysne.graphics;

import org.lwjgl.opengl.GL15;

public enum DrawHint {

    STATIC(GL15.GL_STATIC_DRAW),
    DYNAMIC(GL15.GL_DYNAMIC_DRAW),
    STREAM(GL15.GL_STREAM_DRAW);

    public int val;

    DrawHint(int val) {
        this.val = val;
    }
}