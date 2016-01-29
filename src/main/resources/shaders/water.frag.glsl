#version 330 core


uniform sampler2D reflection;
uniform sampler2D refraction;

in vec3 vPosition;
in vec3 vColor;
in vec2 vTexCoord;
in vec3 vNormal;

out vec4 fragColor;


void main() {

    vec4 reflectionColor = texture(reflection, vTexCoord);
    vec4 refractionColor = texture(refraction, vTexCoord);

    fragColor = mix(reflectionColor, refractionColor, 0.5);
}
