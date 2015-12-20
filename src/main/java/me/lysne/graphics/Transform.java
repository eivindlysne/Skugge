package me.lysne.graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public Vector3f position = new Vector3f(0, 0, 0);
    public Quaternionf orientation = new Quaternionf(0, 0, 0, 1);
    public Vector3f scale = new Vector3f(1, 1, 1);

    public Transform() {}

    public Transform(Transform transform) {

        position.set(transform.position);
        orientation.set(transform.orientation);
        scale.set(transform.scale);
    }

    public Transform mul(Transform other) {
        return Transform.mul(other, this);
    }

    public Matrix4f toMatrix() {

        Matrix4f position = new Matrix4f().identity();
        Matrix4f orientation = new Matrix4f().identity();
        Matrix4f scale = new Matrix4f().identity();

        return position.translate(this.position)
                .mul(orientation.rotation(this.orientation))
                .mul(scale.scale(this.scale));
    }

    public static Transform mul(final Transform ps, final Transform ls) {

        Transform ws = new Transform();

        ws.position.set(ls.position)
            .mul(ps.scale)
            .rotate(ps.orientation)
            .add(ps.position);

        ws.orientation.set(ls.orientation)
            .mul(ps.orientation);

        ws.scale.set(ls.scale)
            .rotate(ps.orientation)
            .mul(ps.scale);

        return ws;
    }
}
