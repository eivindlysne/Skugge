#version 130

uniform sampler2D font;

in vec2 vTexCoord;
in vec3 vColor;

out vec4 fragColor;

void main() {
    vec4 f = texture2D(font, vTexCoord);
    fragColor = vec4(f.a * f.rgb, 1);
    //fragColor = texture2D(font, vTexCoord) * vec4(vColor, 1);
}
