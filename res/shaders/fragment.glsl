#version 430

in vec2 texcoord2;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    outColor = texture(tex, texcoord2);
}