#version 330 core

out vec4 FragColor;

uniform float time;
uniform int flag;
uniform int sheild;
uniform sampler2D ourTexture;

in vec2 uv;

void main() {
	
	vec4 texCol = texture(ourTexture, uv);

	float base = smoothstep(0.3, 0.5, texCol.r);
	float outline = smoothstep(0.0, 0.3, texCol.r);
	vec4 col1 = vec4(base);
	vec4 col2 = vec4(0.0, 0.8, 0.8, outline);

	vec4 final;

	if (sheild == 1) {
		
		final = mix(col2, col1, base);
	
	} else {
		
		final = col1;
	}
	
	if (flag == 1) {

		FragColor = vec4(final);

	} else {
		
		FragColor = vec4(texCol);
	}
} 