#version 430

in vec2 texcoord2;

out vec4 outColor;

uniform sampler2D tex;
uniform vec2 offset;
uniform vec4 color;

void main() {
    outColor = color * texture(tex, texcoord2 + offset);
}