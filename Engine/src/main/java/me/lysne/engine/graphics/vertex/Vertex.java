package me.lysne.engine.graphics.vertex;

import org.joml.Vector3f;

public class Vertex {

    public static final int VERTEX_SIZE = 9;
    public static final int FLOAT_SIZE = 4;

    public Vector3f position;
    public Vector3f color;
    public Vector3f normal;

    public Vertex() {
        this(new Vector3f(), new Vector3f(), new Vector3f(0, 0, 0));
    }

    public Vertex(Vector3f position) {
        this(position, new Vector3f(1, 1, 1), new Vector3f(0, 0, 0));
    }

    public Vertex(Vector3f position, Vector3f color) {
        this(position, color, new Vector3f(0, 0, 0));
    }

    public Vertex(Vector3f position, Vector3f color, Vector3f normal) {
        this.position = position;
        this.color = color;
        this.normal = normal;
    }
}
