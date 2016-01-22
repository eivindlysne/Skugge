package me.lysne.graphics.text;

import me.lysne.Config;
import me.lysne.graphics.Vertex;
import me.lysne.window.Input;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class TextMesh {

    private String text;
    private Font font;
    private float fontSize;
    private Vector2f position;
    private Vector3f color = new Vector3f(1f, 1f, 1f);
    private Matrix4f modelMatrix = new Matrix4f();
    private float lineLength;
    private boolean centered;

    private int vao;
    private int vbo;
    private int drawCount;

    public TextMesh(
            String text,
            Font font,
            float fontSize,
            Vector2f position,
            float lineLength,
            boolean centered
    ) {

        this.text = text;
        this.font = font;
        this.fontSize = fontSize;
        this.position = position;
        this.lineLength = lineLength;
        this.centered = centered;

        modelMatrix.translate(position.x, position.y, 0f);
        modelMatrix.scale(800 * fontSize, 800 * fontSize, 0);

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        drawCount = text.length() * 6;
    }

    public void destroy() {

        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

    public TextMesh build() {

        FloatBuffer buffer = BufferUtils.createFloatBuffer(drawCount * Glyph.VERTEX_SIZE);
        float cursorX = 0f;
        float cursorY = 0f;

        for (char c : text.toCharArray()) {

            if (c == 32) {
                cursorX += font.spaceWidth();
                continue;
            }

            Glyph glyph = font.getGlyph(c);
            glyph.addVertices(buffer, color, cursorX, cursorY);
            cursorX += glyph.xadvance;
        }

        buffer.flip();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(
                0,
                2,
                GL11.GL_FLOAT,
                false,
                Glyph.VERTEX_SIZE * Vertex.FLOAT_SIZE,
                0L);
        GL20.glVertexAttribPointer(
                1,
                2,
                GL11.GL_FLOAT,
                false,
                Glyph.VERTEX_SIZE * Vertex.FLOAT_SIZE,
                (2 * Vertex.FLOAT_SIZE));
        GL20.glVertexAttribPointer(
                2,
                3,
                GL11.GL_FLOAT,
                false,
                Glyph.VERTEX_SIZE * Vertex.FLOAT_SIZE,
                ((2 + 2) * Vertex.FLOAT_SIZE));

        GL30.glBindVertexArray(0);

        return this;
    }

    public void draw() {

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, drawCount);
        GL30.glBindVertexArray(0);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public TextMesh color(float r, float g, float b) {

        color.set(r, g, b);
        return this;
    }

    public Matrix4f modelMatrix() {
        return modelMatrix;
    }
}
