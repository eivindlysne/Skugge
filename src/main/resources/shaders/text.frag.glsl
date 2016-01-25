#version 130

uniform sampler2D font;

in vec2 vTexCoord;
in vec3 vColor;

out vec4 fragColor;

// TODO: Uniforms?
const float width = 0.5;
const float edge = 0.15;

void main() {

    vec4 textureSample = texture2D(font, vTexCoord);
    float distance = 1.0 - textureSample.a;
    float alpha = 1.0 - smoothstep(width, width + edge, distance);
    fragColor =  vec4(vColor, alpha);
}
