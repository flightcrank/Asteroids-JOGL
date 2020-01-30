#version 330 core

layout (location = 0) in vec4 vertex;

uniform mat4 ortho;
uniform mat2 rotate;
uniform vec2 shipPos;
uniform vec2 vel;
uniform int thrust;

out vec3 vertCol;

void main() {

    vec4 scaleVert = vertex * vec4(15, 15, 1, 1);
    vec4 rotateVert = scaleVert * mat4(rotate);

    if (gl_VertexID == 0) {
     
        vertCol = vec3(1.0, 1.0, 1.0);

    } else {
       
        if (thrust == 1) {
            
            vertCol = vec3(1.0, 1.0, 0.0);
        
        } else {
            
            vertCol = vec3(1.0, 1.0, 1.0);
        }
    }
    
    vec4 transVert = rotateVert + vec4(shipPos, 0, 1);
    vec4 clipSpace = transVert * ortho;

    //plot final vertex position
    gl_Position = clipSpace;
}
