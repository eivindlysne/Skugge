package me.lysne.world;

import me.lysne.graphics.*;
import org.joml.Vector3f;

public class BaseLight {

    //public Vector3f position;
    public Transform transform;
    public Vector3f color;

    private Mesh mesh;

    public BaseLight(Vector3f position, Vector3f color) {

        transform = new Transform();
        transform.position.set(position);
        transform.scale.set(0.5f, 0.5f, 0.5f);
        this.color = color;
        generateMesh();
    }

    public void destroy() {

        mesh.destroy();
    }

    public void draw(ShaderProgram shader) {

        shader.setUniform(transform);
        mesh.draw();
    }

    private void generateMesh() {

        float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

        Vertex[] vertices = new Vertex[12];

        vertices[0] = new Vertex(new Vector3f(-1f,  t,  0f), color);
        vertices[1] = new Vertex(new Vector3f( 1f,  t,  0f), color);
        vertices[2] = new Vertex(new Vector3f(-1f, -t,  0f), color);
        vertices[3] = new Vertex(new Vector3f( 1f, -t,  0f), color);

        vertices[4] = new Vertex(new Vector3f( 0f, -1f,  t), color);
        vertices[5] = new Vertex(new Vector3f( 0f,  1f,  t), color);
        vertices[6] = new Vertex(new Vector3f( 0f, -1f, -t), color);
        vertices[7] = new Vertex(new Vector3f( 0f,  1f, -t), color);

        vertices[8] = new Vertex(new Vector3f( t,  0f, -1f), color);
        vertices[9] = new Vertex(new Vector3f( t,  0f,  1f), color);
        vertices[10] = new Vertex(new Vector3f(-t,  0f, -1f), color);
        vertices[11] = new Vertex(new Vector3f(-t,  0f,  1f), color);

        int[] indices = new int[20 * 3]; // 20 faces of three vertices

        indices[0]  = 0  ; indices[1]  = 11 ; indices[2]  = 5;
        indices[3]  = 0  ; indices[4]  = 5  ; indices[5]  = 1;
        indices[6]  = 0  ; indices[7]  = 1  ; indices[8]  = 7;
        indices[9]  = 0  ; indices[10] = 7  ; indices[11] = 10;
        indices[12] = 0  ; indices[13] = 10 ; indices[14] = 11;

        indices[15] = 1  ; indices[16] = 5  ; indices[17] = 9;
        indices[18] = 5  ; indices[19] = 11 ; indices[20] = 4;
        indices[21] = 11 ; indices[22] = 10 ; indices[23] = 2;
        indices[24] = 10 ; indices[25] = 7  ; indices[26] = 6;
        indices[27] = 7  ; indices[28] = 1  ; indices[29] = 8;

        indices[30] = 3  ; indices[31] = 9  ; indices[32] = 4;
        indices[33] = 3  ; indices[34] = 4  ; indices[35] = 2;
        indices[36] = 3  ; indices[37] = 2  ; indices[38] = 6;
        indices[39] = 3  ; indices[40] = 6  ; indices[41] = 8;
        indices[42] = 3  ; indices[43] = 8  ; indices[44] = 9;

        indices[45] = 4  ; indices[46] = 9  ; indices[47] = 5;
        indices[48] = 2  ; indices[49] = 4  ; indices[50] = 11;
        indices[51] = 6  ; indices[52] = 2  ; indices[53] = 10;
        indices[54] = 8  ; indices[55] = 6  ; indices[56] = 7;
        indices[57] = 9  ; indices[58] = 8  ; indices[59] = 1;

        mesh = new Mesh(vertices, indices, DrawHint.STATIC);
    }
}
