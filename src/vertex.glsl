#version 330 core

layout (location = 0) in vec4 vertex;
layout (location = 1) in vec2 tex;

uniform mat2 rotate;
uniform mat4 ortho;
uniform vec2 scale;
uniform vec2 pos;
uniform vec2 res;
uniform vec2 origin;

out vec2 uv;

void main() {
	
	//changes the point at which the verts scaled or rotated from
	vec4 o_point = vertex + vec4(origin.xy, 0.0, 0.0);

	//scale verts
	vec4 size = o_point * vec4(scale.x, -scale.y, 1.0, 1.0);

	//rotate verts
	vec4 rot = size * mat4(rotate);

	//translate verts to screen space
	vec4 screen = rot + vec4(scale.xy, 0.0, 0.0);

	//translate to screen position
	vec2 center = pos + (res / 2);
	vec4 position = screen + vec4(center.xy, 0.0, 0.0);
	
	//final vert position
	vec4 clipPos = ortho * position;

	//uv = vec2(0.4) + tex;
	uv = tex;

	//plot final vertex position
	gl_Position = clipPos;
}
