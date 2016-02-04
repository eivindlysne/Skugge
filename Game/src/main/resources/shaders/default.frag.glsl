#version 130

const vec3 ambientColor = vec3(0.8, 0.4, 0.6);
const float ambientStrength = 0.1;

uniform vec3 lightColor;
uniform vec3 skyColor = vec3(0.53, 0.81, 0.98);

in vec3 vPosition;
in vec3 vColor;
in vec3 vNormal;
in vec3 vToLight;
in float vVisibility;

out vec4 fragColor;

void main() {

    vec3 unitNormal = normalize(vNormal);
    vec3 unitToLight = normalize(vToLight);

    float brightness = max(dot(unitNormal, unitToLight), 0.0);
    vec3 diffuse = brightness * lightColor;

    vec3 ambient = ambientStrength * ambientColor;

    vec3 finalColor = (ambient + diffuse) * vColor;
    finalColor = mix(skyColor, finalColor, vVisibility);

    fragColor = vec4(finalColor, 1.0);
}
