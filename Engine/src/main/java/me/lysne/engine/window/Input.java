package me.lysne.engine.window;

import me.lysne.engine.Config;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private Display display;

    private Map<Integer, Boolean> keys = new HashMap<>();
    private Map<Integer, Boolean> mouse = new HashMap<>();

    private boolean mouseCaptured = true;

    private double centerx = Config.WINDOW_WIDTH / 2;
    private double centery = Config.WINDOW_HEIGHT / 2;

    private double mousedx = 0;
    private double mousedy = 0;

    public Input(Display display) {
        this.display = display;
        registerCallbacks();
        centerCursor();
    }

    private void registerCallbacks() {
        display.registerKeyCallBack(GLFWKeyCallback.create(
                (w, key, scancode, action, mods) -> {
                    if (action != GLFW_REPEAT) {
                        boolean pressed = action == GLFW_PRESS;
                        switch (key) {
                            case GLFW_KEY_ESCAPE:
                                display.setShouldClose();
                                break;
                            case GLFW_KEY_W:
                                keys.put(GLFW_KEY_W, pressed);
                                break;
                            case GLFW_KEY_S:
                                keys.put(GLFW_KEY_S, pressed);
                                break;
                            case GLFW_KEY_A:
                                keys.put(GLFW_KEY_A, pressed);
                                break;
                            case GLFW_KEY_D:
                                keys.put(GLFW_KEY_D, pressed);
                                break;
                            case GLFW_KEY_O:
                                keys.put(GLFW_KEY_O, pressed);
                                break;
                            case GLFW_KEY_P:
                                keys.put(GLFW_KEY_P, pressed);
                                break;
                            case GLFW_KEY_SPACE:
                                if (action == GLFW_RELEASE)
                                    mouseCaptured = !mouseCaptured;
                                break;
                        }
                    }
                }
        ));
        display.registerMouseCallback(GLFWMouseButtonCallback.create(
                (w, button, action, mods) -> {

                }
        ));
        display.registerCursorCallback(GLFWCursorPosCallback.create(
                (w, xpos, ypos) -> {
                    mousedx = xpos - centerx;
                    mousedy = ypos - centery;
                }
        ));
    }

    public double getMousedx() {
        return mousedx;
    }

    public double getMousedy() {
        return mousedy;
    }

    public void setCursorPosition(double x, double y) {
        glfwSetCursorPos(display.getHandle(), x, y);
    }

    public void centerCursor() {
        setCursorPosition(centerx, centery);
        mousedx = 0;
        mousedy = 0;
    }

    public boolean keyDown(int key) {
        return keys.getOrDefault(key, false);
    }

    public boolean keyDownWithReset(int key) {

        boolean down = keys.getOrDefault(key, false);
        if (down)
            keys.put(key, false);
        return down;
    }

    public boolean mouseButtonDown(int mouseButton) {
        return mouse.getOrDefault(mouseButton, false);
    }

    public boolean isMouseCaptured() {
        return mouseCaptured;
    }
}
