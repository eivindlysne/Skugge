package me.lysne.engine.graphics.material;

import org.joml.Vector3f;

public class Material {

    private final Vector3f ambient;

    private final Vector3f diffuse;

    private final Vector3f specular;

    private float shininess;

    private Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess) {

        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public Material ambient(float r, float g, float b) {
        ambient.set(r, g, b);
        return this;
    }

    public Material diffuse(float r, float g, float b) {
        diffuse.set(r, g, b);
        return this;
    }

    public Material specular(float r, float g, float b) {
        specular.set(r, g, b);
        return this;
    }

    public Material shininess(float shininess) {
        this.shininess = shininess;
        return this;
    }

    public Vector3f ambient() {
        return ambient;
    }

    public Vector3f diffuse() {
        return diffuse;
    }

    public Vector3f specular() {
        return specular;
    }

    public float shininess() {
        return shininess;
    }

    public static final Material GOLD = new Material(
            new Vector3f(0.24725f, 0.1995f, 0.0745f),
            new Vector3f(0.75164f, 0.60648f, 0.22648f),
            new Vector3f(0.628281f, 0.555802f, 0.366065f),
            0.4f
    );
}
