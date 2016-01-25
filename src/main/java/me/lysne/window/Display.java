package me.lysne.window;

import me.lysne.Config;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Display {

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;

    private int width;
    private int height;
    private String title;

    private long handle = 0;

    public Display(int width, int height, String title) {

        this.width = width;
        this.height = height;
        this.title = title;

        init();
        initGL();
    }

    private void init() {

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if (glfwInit() != GL11.GL_TRUE)
            System.err.println("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL11.GL_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL11.GL_TRUE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL)
            System.err.println("Failed to create window");

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(handle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);
    }

    private void initGL() {

        GL.createCapabilities();

        GL11.glClearColor(
                Config.SKY_COLOR.x,
                Config.SKY_COLOR.y,
                Config.SKY_COLOR.z,
                Config.SKY_COLOR.w);

        GL11.glViewport(0, 0, width, height);

        GL11.glEnable(GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS); // Not sure if necessary

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void destroy() {

        errorCallback.release();
        keyCallback.release();
        mouseButtonCallback.release();
        cursorPosCallback.release();
        glfwDestroyWindow(handle);
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void poll() {
        glfwPollEvents();
    }

    public void swap() {
        glfwSwapBuffers(handle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle) == GL11.GL_TRUE;
    }

    public void setShouldClose() {
        glfwSetWindowShouldClose(handle, GL11.GL_TRUE);
    }

    public void registerKeyCallBack(GLFWKeyCallback callback) {
        glfwSetKeyCallback(handle, keyCallback = callback);
    }

    public void registerMouseCallback(GLFWMouseButtonCallback callback) {
        glfwSetMouseButtonCallback(handle, mouseButtonCallback = callback);
    }

    public void registerCursorCallback(GLFWCursorPosCallback callback) {
        glfwSetCursorPosCallback(handle, cursorPosCallback = callback);
    }

    public long getHandle() {
        return handle;
    }
}
