#version 330 core

uniform float width = 0.45;
uniform float edge = 0.15;

uniform float borderWidth = 0.7;
uniform float borderEdge = 0.23;
uniform vec3 borderColor = vec3(0.2, 0.1, 0.1);

uniform sampler2D font;

in vec2 vTexCoord;
in vec3 vColor;

out vec4 fragColor;

void main() {

    vec4 textureSample = texture2D(font, vTexCoord);
    float distance = 1.0 - textureSample.a;

    float alpha = 1.0 - smoothstep(width, width + edge, distance);
    float borderAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance);

    float finalAlpha = alpha + (1.0 - alpha) * borderAlpha;
    vec3 finalColor = mix(borderColor, vColor, alpha / finalAlpha);

    fragColor =  vec4(finalColor, finalAlpha);
}
