package me.lysne.engine.graphics;

import me.lysne.engine.Config;
import me.lysne.engine.util.FileUtil;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {

    private int program;
    private int vertexShader;
    private int fragmentShader;

    private Map<String, Integer> uniforms = new HashMap<>();

    public ShaderProgram(String name) {

        program = GL20.glCreateProgram();
        vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        String path = Config.SHADER_DIR + name;
        String vertexShaderSource = FileUtil.readFile(
                ClassLoader.getSystemResourceAsStream(path + ".vert.glsl"));
        String fragmentShaderSource = FileUtil.readFile(
                ClassLoader.getSystemResourceAsStream(path + ".frag.glsl"));

        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        checkShaderLog(vertexShader, GL20.GL_COMPILE_STATUS);

        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        checkShaderLog(fragmentShader, GL20.GL_COMPILE_STATUS);

        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);
        GL20.glValidateProgram(program);
        checkProgramLog(program, GL20.GL_LINK_STATUS);
        checkProgramLog(program, GL20.GL_VALIDATE_STATUS);
    }

    public void destroy() {

        GL20.glDetachShader(program, vertexShader);
        GL20.glDetachShader(program, fragmentShader);

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        GL20.glDeleteProgram(program);
    }

    public void use() {

        // Avoid switching context if unecessary
        // NOTE: NOT threadsafe!
        if (program != programInUse) {
            GL20.glUseProgram(program);
            programInUse = program;
        }
    }

    public void registerUniforms(String... uniformNames) {

        for (String uniformName : uniformNames) {
            int location = GL20.glGetUniformLocation(program, uniformName);
            if (location == -1) {
                System.err.println("Failed to locate uniform: " + uniformName);
                continue;
            }
            this.uniforms.put(uniformName, location);
        }
    }

    public void setUniform(Transform t) {

        setUniform("transform.position", t.position);
        setUniform("transform.orientation", t.orientation);
        setUniform("transform.scale", t.scale);
    }

    public void setUniform(String name, int i) {
        use();
        GL20.glUniform1i(uniforms.get(name), i);
    }

    public void setUniform(String name, float f) {
        use();
        GL20.glUniform1f(uniforms.get(name), f);
    }

    public void setUniform(String name, Vector2f v) {
        use();
        GL20.glUniform2f(uniforms.get(name), v.x, v.y);
    }

    public void setUniform(String name, Vector3f v) {
        use();
        GL20.glUniform3f(uniforms.get(name), v.x, v.y, v.z);
    }

    public void setUniform(String name, Vector4f v) {
        use();
        GL20.glUniform4f(uniforms.get(name), v.x, v.y, v.z, v.w);
    }

    public void setUniform(String name, Quaternionf q) {
        use();
        GL20.glUniform4f(uniforms.get(name), q.x, q.y, q.z, q.w);
    }

    public void setUniform(String name, Matrix4f m) {
        use();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        m.get(buffer);
        GL20.glUniformMatrix4fv(uniforms.get(name), false, buffer);
    }

    private static void checkShaderLog(int handle, int flag) {

        int status = GL20.glGetShaderi(handle, flag);
        if (status != GL11.GL_TRUE) {
            String log = GL20.glGetShaderInfoLog(handle);
            System.err.println("Shader error: " + log);
        }
    }

    private static void checkProgramLog(int handle, int flag) {

        int status = GL20.glGetProgrami(handle, flag);
        if (status != GL11.GL_TRUE) {
            String log = GL20.glGetProgramInfoLog(handle);
            System.err.println("Program error: " + log);
        }
    }

    private static int programInUse = -1;

    public static void unset() {
        programInUse = -1;
        GL20.glUseProgram(0);
    }
}
