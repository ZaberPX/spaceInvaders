#version 430

in vec3 position;
in vec2 texcoord;

out vec2 texcoord2;

void main() {
    texcoord2 = texcoord;
	gl_Position = vec4( position.x, position.y, -position.z, 1.0);
}