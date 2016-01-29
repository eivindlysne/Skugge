package me.lysne.world;

import me.lysne.graphics.Camera;
import me.lysne.graphics.ShaderProgram;
import me.lysne.graphics.Transform;
import me.lysne.graphics.buffer.FramebufferObject;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {

    public static final float WATER_LEVEL = -10f;

    public Transform transform = new Transform();

    private ShaderProgram waterShader;

    private Region currentRegion;
    private Map<Coord, Region> regions = new HashMap<>();
    private ArrayList<BaseLight> lights = new ArrayList<>();

    private FramebufferObject reflectionBuffer;
    private FramebufferObject refractionBuffer;
    private Vector4f clipPlane = new Vector4f();

    public World() {

        waterShader = new ShaderProgram("water");
        waterShader.registerUniforms("reflection", "refraction");
//        waterShader.setUniform("reflection", 0);
//        waterShader.setUniform("refraction", 1);

        createRegion(-1, -1);
        createRegion( 0, -1);
        createRegion( 1, -1);

        createRegion(-1,  0);
        currentRegion = createRegion( 0,  0);
        createRegion( 1,  0);

        createRegion(-1,  1);
        createRegion( 0,  1);
        createRegion( 1,  1);

        lights.add(new BaseLight(new Vector3f(0, 0, -10), new Vector3f(1, 1, 1)));

        reflectionBuffer = new FramebufferObject().withColorAttachment().withDepthRenderBuffer().build();
        refractionBuffer = new FramebufferObject().withColorAttachment().withDepthAttachment().build();
    }

    public void destroy() {

        regions.forEach(((coord, region) -> region.destroy()));
        lights.forEach(BaseLight::destroy);

        reflectionBuffer.destroy();
        refractionBuffer.destroy();

        waterShader.destroy();
    }

    private Region createRegion(int x, int z) {

        Coord coord = new Coord(x, z);
        Region region = new Region(coord, this);
        regions.put(coord, region);
        return region;
    }

    private Region getOrCreateRegion(int x, int z) {

        Region region = regions.getOrDefault(new Coord(x, z), null);
        if (region == null)
            region = createRegion(x, z);
        return region;
    }

    public void update(final Camera camera) {

        Vector3f camPosition = new Vector3f(camera.getPosition())
                .add(Region.REGION_SIZE_X / 2, 0, Region.REGION_SIZE_Z / 2);

        int camWorldX = Math.floorDiv((int) camPosition.x, Region.REGION_SIZE_X);
        int camWorldZ = Math.floorDiv((int) camPosition.z, Region.REGION_SIZE_Z);

        boolean crossedRegionBoundary =
                currentRegion.coord.x != camWorldX ||
                currentRegion.coord.z != camWorldZ;

        if (crossedRegionBoundary) {

            Region newRegion = getOrCreateRegion(camWorldX, camWorldZ);

            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    int nx = newRegion.coord.x + x;
                    int nz = newRegion.coord.z + z;
                    getOrCreateRegion(nx, nz);
                }
            }
            currentRegion = newRegion;
        }
    }

    public void draw(Camera camera, ShaderProgram terrainShader, ShaderProgram screenShader) {

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        // TODO: Invert for reflection
        terrainShader.use();

        // TODO: We only have one light atm
        BaseLight light = lights.get(0);
        terrainShader.setUniform("lightPosition", light.transform.position);
        terrainShader.setUniform("lightColor", light.color);

        terrainShader.setUniform("projection", camera.projection);


        reflectionBuffer.bind();
        reflectionBuffer.clear();
        clipPlane.set(0f, 1f, 0f, -WATER_LEVEL);
        terrainShader.setUniform("view", camera.mirrorHorizontalPosition(WATER_LEVEL)); // TODO: Probably not correct
//        terrainShader.setUniform("view", camera.view);
        terrainShader.setUniform("clipPlane", clipPlane);
        for (Region region : regions.values())
            region.drawTerrain(terrainShader);

        refractionBuffer.bind();
        refractionBuffer.clear();
        clipPlane.set(0f, -1f, 0f, WATER_LEVEL);
        terrainShader.setUniform("view", camera.view);
        terrainShader.setUniform("clipPlane", clipPlane);
        for (Region region : regions.values())
            region.drawTerrain(terrainShader);

        FramebufferObject.unbindCurrent();
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

//        for (Region region : regions.values())
//            region.drawTerrain(terrainShader);

//        waterShader.use();
//        reflectionBuffer.bindColorAttachment(0);
//        refractionBuffer.bindColorAttachment(1);

//        light.draw(terrainShader);


        screenShader.use();
        reflectionBuffer.draw();
//        refractionBuffer.draw();

    }
}
