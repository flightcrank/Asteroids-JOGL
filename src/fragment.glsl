#version 330 core

out vec4 FragColor;

uniform vec2 panelResolution;
uniform float time;
uniform int flag;
uniform sampler2D ourTexture;

in vec2 uv;

void main() {
	
	vec4 texCol = texture(ourTexture, uv);

	float f = smoothstep(0.4, 0.5, texCol.r);
	vec4 col = vec4(f);
	
	if (flag == 1) {

		FragColor = vec4(col);

	} else {
		
		FragColor = vec4(texCol);
	}
} 