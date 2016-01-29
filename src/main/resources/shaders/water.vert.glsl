#version 330 core

struct Transform {
    vec3 position;
    vec4 orientation;
    vec3 scale;
};

vec3 quaternion_rotate(vec4 q, vec3 v) {
    return v + 2.0 * cross(cross(v, q.xyz) + q.w * v, q.xyz);
}

uniform mat4 view;
uniform mat4 projection;

uniform Transform transform;

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aColor;
//layout (location = 2) in vec2 vTexCoord;
layout (location = 2) in vec3 aNormal;

//out vec3 vPosition;
out vec3 vColor;
//out vec2 vTexCoord;
//out vec3 vNormal;

void main() {

    vec3 position = transform.position + quaternion_rotate(transform.orientation, transform.scale * aPosition);
    vec4 positionRelativeToCam = view * vec4(position, 1.0);

    gl_Position = projection * positionRelativeToCam;

//    vPosition = aPosition;
    vColor = aColor;
//    vNormal = quaternion_rotate(transform.orientation, transform.scale * aNormal);
}
