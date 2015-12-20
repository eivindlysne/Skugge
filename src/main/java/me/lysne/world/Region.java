package me.lysne.world;

import me.lysne.graphics.DrawHint;
import me.lysne.graphics.Mesh;
import me.lysne.graphics.ShaderProgram;
import me.lysne.graphics.Transform;
import me.lysne.pg.TerrainGenerator;

public class Region {

    public static final int REGION_SIZE_X = 32;
    public static final int REGION_SIZE_Z = 32;

    private World world;
    public Coord coord;

    private Mesh mesh;
    private Transform transform;

    public Region(Coord coord, World world) {

        this.coord = coord;
        this.world = world;

        this.transform = new Transform();
        transform.position.set((coord.x * REGION_SIZE_X), 0, (coord.z * REGION_SIZE_Z));

        mesh = new Mesh(
                TerrainGenerator.generateRegionMesh(
                        this.transform,
                        REGION_SIZE_X, REGION_SIZE_Z,
                        4f /* Feature size */),
                DrawHint.STATIC);
    }

    public void destroy() {

        mesh.destroy();
    }

    public void draw(ShaderProgram shader) {

        shader.setUniform(Transform.mul(world.transform, transform));

        mesh.draw();
    }
}
