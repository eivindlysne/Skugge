package me.lysne.world;

import me.lysne.graphics.Camera;
import me.lysne.graphics.ShaderProgram;
import me.lysne.graphics.Transform;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {

    public Transform transform = new Transform();

    private Region currentRegion;
    private Map<Coord, Region> regions = new HashMap<>();
    private ArrayList<BaseLight> lights = new ArrayList<>();

    public World() {

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
    }

    public void destroy() {

        regions.forEach(((coord, region) -> region.destroy()));
        lights.forEach(BaseLight::destroy);
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

    public void draw(ShaderProgram shader) {

        BaseLight l = lights.get(0);
        shader.setUniform("lightPosition", l.transform.position);
        shader.setUniform("lightColor", l.color);

        regions.forEach((coord, region) -> region.draw(shader));
        lights.forEach(light -> light.draw(shader));
    }
}
