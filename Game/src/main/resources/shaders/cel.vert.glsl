#version 130

struct Transform {
    vec3 position;
    vec4 orientation;
    vec3 scale;
};

vec3 quaternion_rotate(vec4 q, vec3 v) {
    return v + 2.0 * cross(cross(v, q.xyz) + q.w * v, q.xyz);
}

const float fogDensity = 0.0035;
const float fogGradient = 5.0;

uniform mat4 view;
uniform mat4 projection;

uniform Transform transform;
uniform vec3 lightPosition;

in vec3 aPosition;
in vec3 aColor;
in vec3 aNormal;

out vec3 vPosition;
out vec3 vColor;
out vec3 vNormal;
out vec3 vToLight;
out float vVisibility;

void main() {

    vec3 position = transform.position + quaternion_rotate(
        transform.orientation, transform.scale * aPosition);

    vec4 positionRelativeToCam = view * vec4(position, 1.0);
    float distanceFromCam = length(positionRelativeToCam.xyz);

    gl_Position = projection * positionRelativeToCam;


    vPosition = aPosition;
    vColor = aColor;
    vNormal = quaternion_rotate(
        transform.orientation, transform.scale * aNormal);
    vToLight = lightPosition - position;
    vVisibility = clamp(
        exp(-pow((distanceFromCam * fogDensity), fogGradient)), 0, 1);
}
