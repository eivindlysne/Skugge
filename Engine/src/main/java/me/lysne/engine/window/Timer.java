package me.lysne.engine.window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {

    public static double getTime() {
        return glfwGetTime();
    }
}
