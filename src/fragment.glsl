#version 330 core

in vec3 vertCol;
out vec4 FragColor;

void main() {
    
    FragColor = vec4(vertCol, 1.0f);
} 