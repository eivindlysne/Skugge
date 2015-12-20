package me.lysne.graphics;

import org.joml.Vector3f;

public class Face {

    Vertex a;
    Vertex b;
    Vertex c;
    Vector3f normal = new Vector3f(0, 0, 0);

    public Face(Vertex a, Vertex b, Vertex c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}

