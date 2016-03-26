package me.lysne.engine.graphics.mesh;

import me.lysne.engine.graphics.types.DrawHint;
import me.lysne.engine.graphics.vertex.Vertex;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private int vao;
    private int vbo;
    private int ibo;

    private int drawCount = 0;

    public Mesh(MeshData meshData, DrawHint drawHint) {
        this(meshData.vertices, meshData.indices, drawHint, true);
    }

    public Mesh(MeshData meshData, DrawHint drawHint, boolean calculateNormals) {
        this(meshData.vertices, meshData.indices, drawHint, calculateNormals);
    }

    public Mesh(Vertex[] vertices, int[] indices, DrawHint drawHint) {
        this(vertices, indices, drawHint, true);
    }

    public Mesh(Vertex[] vertices, int[] indices, DrawHint drawHint, boolean calculateNormals) {

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        ibo = GL15.glGenBuffers();

        if (calculateNormals)
            calculateNormals(vertices, indices);

        drawCount = indices.length;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(
                vertices.length * Vertex.VERTEX_SIZE);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);

        for (int i = 0; i < vertices.length; i++) {
            Vertex v = vertices[i];
            vertexBuffer
                    .put(v.position.x).put(v.position.y).put(v.position.z)
                    .put(v.color.x).put(v.color.y).put(v.color.z)
                    .put(v.normal.x).put(v.normal.y).put(v.normal.z);
        }
        for (int i = 0; i < indices.length; i++) {
            indexBuffer.put(indices[i]);
        }

        vertexBuffer.flip();
        indexBuffer.flip();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, drawHint.value);

        GL20.glEnableVertexAttribArray(0); // Position
        GL20.glEnableVertexAttribArray(1); // Color
        GL20.glEnableVertexAttribArray(2); // Normal

        int bytesPerVertex = Vertex.VERTEX_SIZE * Vertex.FLOAT_SIZE;

        GL20.glVertexAttribPointer(
                0,
                3,
                GL11.GL_FLOAT,
                false,
                bytesPerVertex,
                0L);
        GL20.glVertexAttribPointer(
                1,
                3,
                GL11.GL_FLOAT,
                false,
                bytesPerVertex,
                3 * Vertex.FLOAT_SIZE);
        GL20.glVertexAttribPointer(
                2,
                3,
                GL11.GL_FLOAT,
                false,
                bytesPerVertex,
                (3 + 3) * Vertex.FLOAT_SIZE);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, drawHint.value);

        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
        GL15.glDeleteBuffers(ibo);
    }

    public void draw() {
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, drawCount, GL11.GL_UNSIGNED_INT, 0L);
        GL30.glBindVertexArray(0);
    }

    private static void calculateNormals(Vertex[] vertices, int[] indices) {

        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();

        for (int i = 0; i < indices.length; i += 3) {

            Vertex v0 = vertices[indices[i]];
            Vertex v1 = vertices[indices[i + 1]];
            Vertex v2 = vertices[indices[i + 2]];

            a.set(v1.position).sub(v0.position);
            b.set(v2.position).sub(v1.position);

            Vector3f normal = a.cross(b).normalize();

            v0.normal.add(normal);
            v1.normal.add(normal);
            v2.normal.add(normal);
        }

        for (int i = 0; i < vertices.length; i++) {
            vertices[i].normal.normalize();
        }
    }
}
