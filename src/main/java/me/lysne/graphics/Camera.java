package me.lysne.graphics;

import me.lysne.Config;
import me.lysne.window.Input;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {

    public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);
    public static final float MAX_PITCH = (float) Math.toRadians(60);

    private float currentPitch = 0;

    public Matrix4f ortho = new Matrix4f().ortho(
            0,
            Config.WINDOW_WIDTH,
            0,
            Config.WINDOW_HEIGHT,
            1,
            -1);

    public Matrix4f projection = new Matrix4f();
    public Matrix4f view = new Matrix4f();
    public Matrix4f combined = new Matrix4f();

    private Transform transform = new Transform();

    // TODO: Move to config?
    private final float fov = (float) Math.toRadians(70);
    private final float zNear = 0.01f;
    private final float zFar = 1000;
    private final float aspect = Config.WINDOW_WIDTH / Config.WINDOW_HEIGHT;

    public Camera(Vector3f position) {

        transform = new Transform();
        transform.position.set(position);

        projection.perspective(fov, aspect, zNear, zFar);
    }

    // Internal objects to reduce allocation every frame
    private Quaternionf tempQ1 = new Quaternionf();
    private Quaternionf tempQ2 = new Quaternionf();
    private Vector3f tempV1 = new Vector3f();
    private Vector3f tempV2 = new Vector3f();
    private Matrix4f tempM1 = new Matrix4f();
    private Matrix4f tempM2 = new Matrix4f();

    private void offsetOrientation(double yawDegrees, double pitchDegrees) {

        float yaw = (float) Math.toRadians(yawDegrees);
        float pitch = (float) Math.toRadians(pitchDegrees);

        if (currentPitch + pitch > MAX_PITCH)
            pitch = MAX_PITCH - currentPitch;
        else if (currentPitch + pitch < -MAX_PITCH) {
            pitch = -MAX_PITCH - currentPitch;
        }
        currentPitch += pitch;

        Quaternionf yawRotation = tempQ1.set(0, 0, 0, 1).rotateAxis(yaw, WORLD_UP);

        Vector3f right = tempV1.set(1, 0, 0).rotate(transform.orientation);
        Quaternionf pitchRotation = tempQ2.set(0, 0, 0, 1).rotateAxis(pitch, right);

        yawRotation.mul(pitchRotation).mul(transform.orientation, transform.orientation);
    }

    public void update(Input input) {

        if (input.isMouseCaptured()) {
            offsetOrientation(
                    -input.getMousedx() * Config.MOUSE_SENSITIVITY,
                    -input.getMousedy() * Config.MOUSE_SENSITIVITY);
            input.centerCursor();
        }

        Vector3f direction = new Vector3f(0, 0, 0);

        if (input.keyDown(GLFW.GLFW_KEY_W)) direction.z -= 1;
        if (input.keyDown(GLFW.GLFW_KEY_S)) direction.z += 1;
        if (input.keyDown(GLFW.GLFW_KEY_A)) direction.x -= 1;
        if (input.keyDown(GLFW.GLFW_KEY_D)) direction.x += 1;
        if (direction.length() > 0) direction.normalize();

        direction.rotate(transform.orientation);
        if (!Config.FLYING) direction.y = 0;
        direction.mul(Config.VELOCITY);
        transform.position.add(direction);

        view.identity();
        Quaternionf orientationConj = tempQ1.set(transform.orientation).conjugate();
        Vector3f positionNegated = tempV1.set(transform.position).negate();

        view.scale(tempV2.set(1, 1, 1).div(transform.scale))
                .mul(tempM1.identity().rotation(orientationConj))
                .mul(tempM2.identity().translate(positionNegated));

        projection.mul(view, combined);
    }

    public Vector3f getPosition() {
        return transform.position;
    }

    public Quaternionf getOrientation() {
        return transform.orientation;
    }
}
