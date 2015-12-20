package me.lysne.pg;

import me.lysne.Config;
import me.lysne.graphics.MeshData;
import me.lysne.graphics.Transform;
import me.lysne.graphics.Vertex;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TerrainGenerator {

    private static TerrainGenerator instance = new TerrainGenerator();

    private OpenSimplexNoise noise = new OpenSimplexNoise(Config.SEED);

    private TerrainGenerator() {}

    private static float generateHeight(float x, float z, float featureSize) {

        double value = 0;
        value += 1 * instance.noise.eval(x / 16, z / 16);
        value += 0.5 * instance.noise.eval(x / 8, z / 8);
        value += 0.25 * instance.noise.eval(x / 4, z / 4);
        value += 0.125 * instance.noise.eval(x / 2, z / 2);

        return (float) value * featureSize;
    }

    private static Vector3f colorFromHeight(float height) {

//        Vector3f dst = new Vector3f();
//        if (height < -5) dst.set(0.40f, 0.47f, 0.79f);
//        else if (height < -4) dst.set(0.93f, 0.84f, 0.72f);
//        else if (height < 1) dst.set(0.14f, 0.41f, 0.18f);
//        else if (height < 2) dst.set(0.15f, 0.69f, 0.21f);
//        else if (height < 4) dst.set(0.5f, 0.51f, 0.53f);
//        else dst.set(0.9f, 0.9f, 1);
//        return dst;
        return new Vector3f(0.15f, 0.69f, 0.21f);
    }

    public static MeshData generateRegionMeshShared2(
            Transform t,
            int xUnits,
            int zUnits,
            float featureSize) {

        float plane = -10;
        Vertex[] vertices = new Vertex[xUnits * zUnits * 4];
        int[] indices = new int[xUnits * zUnits * 6];
        Vector4f heights = new Vector4f();

        zUnits /= 2;
        xUnits /= 2;

        int vx = 0, ix = 0;
        for (int i = -zUnits; i < zUnits; i++) {
            for (int j = -xUnits; j < xUnits; j++) {

                float x0 = j, x1 = x0 + 1;
                float z0 = -i, z1 = z0 - 1;

                heights.set(
                        generateHeight(x0 + t.position.x, z0 + t.position.z, featureSize),
                        generateHeight(x1 + t.position.x, z0 + t.position.z, featureSize),
                        generateHeight(x1 + t.position.x, z1 + t.position.z, featureSize),
                        generateHeight(x0 + t.position.x, z1 + t.position.z, featureSize));

                vertices[vx  ] = new Vertex(new Vector3f(x0, heights.x + plane, z0), colorFromHeight(heights.x));
                vertices[vx+1] = new Vertex(new Vector3f(x1, heights.y + plane, z0), colorFromHeight(heights.y));
                vertices[vx+2] = new Vertex(new Vector3f(x1, heights.z + plane, z1), colorFromHeight(heights.z));
                vertices[vx+3] = new Vertex(new Vector3f(x0, heights.w + plane, z1), colorFromHeight(heights.w));

                indices[ix++] = vx;
                indices[ix++] = vx + 1;
                indices[ix++] = vx + 2;
                indices[ix++] = vx + 2;
                indices[ix++] = vx + 3;
                indices[ix++] = vx;

                vx += 4;
            }
        }

        return new MeshData(vertices, indices);
    }

    public static MeshData generateRegionMeshShared(
            Transform t,
            int xUnits,
            int zUnits,
            float featureSize) {

        float plane = -10;
        Vertex[] vertices = new Vertex[(xUnits + 1) * (zUnits + 1)];
        int[] indices = new int[xUnits * zUnits * 6];

        int xUnitsHalf = xUnits / 2;
        int zUnitsHalf = zUnits / 2;

        int ix = 0;
        for (int i = -zUnitsHalf; i < zUnitsHalf + 1; i++) {
            for (int j = -xUnitsHalf; j < xUnitsHalf + 1; j++) {

                int x = j;
                int z = i;
                int pos = (i+zUnitsHalf) * (xUnits + 1) + (j+xUnitsHalf);

                float y = generateHeight(x + t.position.x, z + t.position.z, featureSize);

                vertices[pos] = new Vertex(new Vector3f(x, y + plane, z), colorFromHeight(y));

                if (i != -zUnitsHalf && j != -xUnitsHalf) {
                    indices[ix++] = pos - 1;
                    indices[ix++] = pos - xUnits - 1;
                    indices[ix++] = pos - xUnits - 2;
                    indices[ix++] = pos;
                    indices[ix++] = pos - xUnits - 1;
                    indices[ix++] = pos - 1;
                }
            }
        }

        return new MeshData(vertices, indices);
    }

    public static MeshData generateRegionMesh(
            Transform t,
            int xUnits, int zUnits,
            float featureSize) {

        float plane = -10;
        Vertex[] vertices = new Vertex[xUnits * zUnits * 6];
        int[] indices = new int[xUnits * zUnits * 6];

        zUnits /= 2;
        xUnits /= 2;

        int vx = 0, ix = 0;
        for (int i = -zUnits; i < zUnits; i += 1) {
            for (int j = -xUnits; j < xUnits; j += 1) {

                float x0 = j, x1 = x0 + 1f;
                float z0 = -i, z1 = z0 - 1f;

                // TODO: This can be optimized!

                float y0 = generateHeight(x0 + t.position.x, z0 + t.position.z, featureSize);
                float y1 = generateHeight(x1 + t.position.x, z0 + t.position.z, featureSize);
                float y2 = generateHeight(x1 + t.position.x, z1 + t.position.z, featureSize);
                float y3 = generateHeight(x1 + t.position.x, z1 + t.position.z, featureSize);
                float y4 = generateHeight(x0 + t.position.x, z1 + t.position.z, featureSize);
                float y5 = generateHeight(x0 + t.position.x, z0 + t.position.z, featureSize);

                vertices[vx  ] = new Vertex(new Vector3f(x0, y0 + plane, z0), colorFromHeight(y0));
                vertices[vx+1] = new Vertex(new Vector3f(x1, y1 + plane, z0), colorFromHeight(y1));
                vertices[vx+2] = new Vertex(new Vector3f(x1, y2 + plane, z1), colorFromHeight(y2));
                vertices[vx+3] = new Vertex(new Vector3f(x1, y3 + plane, z1), colorFromHeight(y3));
                vertices[vx+4] = new Vertex(new Vector3f(x0, y4 + plane, z1), colorFromHeight(y4));
                vertices[vx+5] = new Vertex(new Vector3f(x0, y5 + plane, z0), colorFromHeight(y5));

                indices[ix++] = vx;
                indices[ix++] = vx + 1;
                indices[ix++] = vx + 2;
                indices[ix++] = vx + 3;
                indices[ix++] = vx + 4;
                indices[ix++] = vx + 5;

                vx += 6;
            }
        }
        return new MeshData(vertices, indices);
    }
}
