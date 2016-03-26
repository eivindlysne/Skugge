package me.lysne.game.world;

import me.lysne.engine.graphics.ShaderProgram;
import me.lysne.engine.graphics.Transform;
import me.lysne.engine.graphics.mesh.Mesh;
import me.lysne.engine.graphics.types.DrawHint;
import me.lysne.engine.pg.TerrainGenerator;

public class Region {

    public static final int REGION_SIZE_X = 32;
    public static final int REGION_SIZE_Z = 32;

    private World world;
    public Coord coord;

    private Mesh mesh;
    private Mesh waterMesh;
    private Transform transform;

    public Region(Coord coord, World world) {

        this.coord = coord;
        this.world = world;

        this.transform = new Transform();
        transform.position.set((coord.x * REGION_SIZE_X), 0, (coord.z * REGION_SIZE_Z));

        mesh = new Mesh(
                TerrainGenerator.generateRegionMesh(this.transform, REGION_SIZE_X, REGION_SIZE_Z, 8f /* Feature size */),
                DrawHint.STATIC
        );

        waterMesh = new Mesh(
                TerrainGenerator.generateWaterMesh(REGION_SIZE_X, REGION_SIZE_Z),
                DrawHint.STATIC,
                false
        );
    }

    public void destroy() {

        mesh.destroy();
    }

    public void drawTerrain(ShaderProgram shader) {

        shader.setUniform(Transform.mul(world.transform, transform));
        mesh.draw();
    }

    public void drawWater(ShaderProgram shader) {

        shader.setUniform(Transform.mul(world.transform, transform));
        waterMesh.draw();
    }
}
