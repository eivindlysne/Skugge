package me.lysne.engine.rendering.pass;

public interface RenderPass {

    void preRender();

    void render();

    void postRender();
}
