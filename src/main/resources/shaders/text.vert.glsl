#version 330 core

uniform mat4 viewProjection;
uniform mat4 model;

in vec2 aPosition;
in vec2 aTexCoord;
in vec3 aColor;

out vec2 vTexCoord;
out vec3 vColor;

void main() {
    vTexCoord = aTexCoord;
    vColor = aColor;
    gl_Position = viewProjection * model * vec4(aPosition, 0.0, 1.0);
}
