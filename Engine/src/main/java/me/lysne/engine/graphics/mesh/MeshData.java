package me.lysne.engine.graphics.mesh;

import me.lysne.engine.graphics.Vertex;

public class MeshData {

    public Vertex[] vertices;
    public int[] indices;

    public MeshData(Vertex[] vertices, int[] indices) {

        this.vertices = vertices;
        this.indices = indices;
    }
}
