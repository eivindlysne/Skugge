package me.lysne.engine.text;

import me.lysne.engine.graphics.Vertex;
import me.lysne.engine.graphics.types.DrawHint;
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

    private StringBuilder text;
    private Font font;
    private float fontSize;
    private Vector2f position;
    private Vector3f color = new Vector3f(1f, 1f, 1f);
    private Matrix4f modelMatrix = new Matrix4f();
    private float lineLength;
    private boolean centered;

    private FloatBuffer buffer;

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

        this.text = new StringBuilder(text);
        this.font = font;
        this.fontSize = fontSize;
        this.position = position;
        this.lineLength = lineLength;
        this.centered = centered;

        modelMatrix.translate(position.x, position.y, 0f);
        modelMatrix.scale(400 * fontSize, 400 * fontSize, 0);

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        drawCount = text.length() * 6;
    }

    public void destroy() {

        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

    private void buildBuffer() {

        drawCount = text.length() * 6;
        int capacity = drawCount * Glyph.VERTEX_SIZE;

        if (buffer == null || capacity > buffer.capacity()) {
            buffer = BufferUtils.createFloatBuffer(capacity);
        } else {
            buffer.clear();
            buffer.limit(capacity);
        }

        float cursorX = 0f;
        float cursorY = 0f;

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);
            if (c == 32) {
                cursorX += font.spaceWidth();
                continue;
            }

            Glyph glyph = font.getGlyph(c);
            glyph.addVertices(buffer, color, cursorX, cursorY);
            cursorX += glyph.xadvance;
        }

        buffer.flip();
    }

    private void updateBuffer() {

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0L, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void replace(int start, String subString, boolean deleteRemaining) {

        int end = start + subString.length();
        text.replace(start, end, subString);

        if (deleteRemaining && end < text.length())
            text.delete(end, text.length());

        buildBuffer();
        updateBuffer();
    }

    public void replace(String newText) {

        text.setLength(0);
        text.append(newText);

        buildBuffer();
        updateBuffer();
    }

    public TextMesh build(DrawHint drawHint) {

        buildBuffer();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, drawHint.value);

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
