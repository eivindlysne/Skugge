package me.lysne.engine.text;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

public class Glyph {

    public static final int VERTEX_SIZE = 2 + 2 + 3;

    int id = -1;
    float xTexCoord = 0;
    float yTexCoord = 0;
    float xTexSize = 0;
    float yTexSize = 0;
    float quadWidth = 0;
    float quadHeight = 0;
    float xoffset = 0;
    float yoffset = 0;
    float xadvance = 0;

    public void addVertices(
            FloatBuffer buffer,
            Vector3f color,
            float cursorX,
            float cursorY
    ) {

        float x0 = cursorX + (xoffset);
        float y0 = cursorY - (yoffset);
        float x1 = x0 + (quadWidth);
        float y1 = y0 - (quadHeight);

        buffer.put(x0).put(y1).put(xTexCoord).put(yTexCoord - yTexSize).put(color.x).put(color.y).put(color.z);
        buffer.put(x1).put(y1).put(xTexCoord + xTexSize).put(yTexCoord - yTexSize).put(color.x).put(color.y).put(color.z);
        buffer.put(x1).put(y0).put(xTexCoord + xTexSize).put(yTexCoord).put(color.x).put(color.y).put(color.z);
        buffer.put(x1).put(y0).put(xTexCoord + xTexSize).put(yTexCoord).put(color.x).put(color.y).put(color.z);
        buffer.put(x0).put(y0).put(xTexCoord).put(yTexCoord).put(color.x).put(color.y).put(color.z);
        buffer.put(x0).put(y1).put(xTexCoord).put(yTexCoord - yTexSize).put(color.x).put(color.y).put(color.z);

    }
}
