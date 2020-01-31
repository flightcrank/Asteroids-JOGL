
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;

class Renderer implements GLEventListener {

	int renderingProgram;
	int vao[] = new int[1]; //vertex attribute object
	int vbo[] = new int[1]; //vertex buffer object
	int w, h;
	float vertices[];
	int numVerts = 0;
	FloatBuffer vBuf;
	Ship player;
		
	public Renderer(Ship ship) {
		
		this.player = ship;
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {	
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		//Load ship verts into buffer
		this.vBuf = Buffers.newDirectFloatBuffer(player.verts);
		this.numVerts = player.verts.length / 3;
		
		gl.glPointSize(5.0f);
		gl.glEnable(GL3.GL_DEPTH_TEST);  
		//gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
		
		//set background colour
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		
		//generate OPENGL buffers
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glGenBuffers(vbo.length, vbo, 0);	//generate vertex buffer 
	
		//VAO
		gl.glBindVertexArray(vao[0]);	//make vertex attribute object 0 active 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 
		
		//load/compile shaders from file
		renderingProgram = createShaders();
	}
		
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {

		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		//use compiled shaders
		gl.glUseProgram(renderingProgram);
		
		//shader uniform variables
		int persp = gl.glGetUniformLocation(renderingProgram, "ortho");
		gl.glUniformMatrix4fv(persp, 1, false, Buffers.newDirectFloatBuffer(Matrix.orthographic(w, h, 1, 10)));
				
		//set background colour and clear the screen
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		//DRAW the game objects
		drawShip(gl);
		drawBullets(gl);
		
		//update ship state
		player.update(w, h);
		
		//update bullet array state
		for(Bullet bullet : player.bullets) {
			
			if (bullet.visable) {
				
				bullet.update(w, h);
			}
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		
		
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		w = width;
		h = height;
	}
	
	public void drawBullets(GL3 gl) {
		
		for (Bullet bullet : player.bullets) {
			
			if (bullet.visable) { 
				
				//uniform variables
				int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
				gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(bullet.rot)));

				int shipPos = gl.glGetUniformLocation(renderingProgram, "objectPos");
				gl.glUniform2f(shipPos, bullet.posX, bullet.posY);

				int vel = gl.glGetUniformLocation(renderingProgram, "vel");
				gl.glUniform2f(vel, bullet.vX, bullet.vY);
				
				int scale = gl.glGetUniformLocation(renderingProgram, "scale");
				gl.glUniform1f(scale, bullet.scale);

				//vert position
				gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);		///make vert buffer active
				gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
				gl.glEnableVertexAttribArray(0);

				//DRAW
				gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
			}
		}
	}
	
	public void drawShip(GL3 gl) {
		
		int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
		gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(player.rot)));
		
		int shipPos = gl.glGetUniformLocation(renderingProgram, "objectPos");
		gl.glUniform2f(shipPos, player.posX, player.posY);
		
		int vel = gl.glGetUniformLocation(renderingProgram, "vel");
		gl.glUniform2f(vel, player.vX, player.vY);
		
		int scale = gl.glGetUniformLocation(renderingProgram, "scale");
		gl.glUniform1f(scale, player.scale);
		
		int i = player.thrust ? 1 : 0;
		int thrust = gl.glGetUniformLocation(renderingProgram, "thrust");
		gl.glUniform1i(thrust, i);
		
		//vert position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);		///make vert buffer active
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(0);
		
		//DRAW
		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
		
	}
	
	private String[] readShaderSource(String path) {
		
		ArrayList<String> vertexList = new  ArrayList<>();

		try (BufferedReader in = new BufferedReader(new FileReader(path))) {
			
			String line = in.readLine();

			while (line != null) {

				vertexList.add(line);
				vertexList.add("\n");
				line = in.readLine();
			}

		} catch (Exception ex) {
			
			System.out.println(ex.toString());
		}

		return vertexList.toArray(new String[0]);
	}

	public int createShaders() {
			
		GL3 gl = (GL3) GLContext.getCurrentGL();

		String vshaderSource[] = readShaderSource("vertex.glsl");
		String fshaderSource[] = readShaderSource("fragment.glsl");
		
		int vShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		
		int fShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		
		return vfprogram;
	}
	
	public void loadMesh(String fileName) {
		
//		PlyParser object = new PlyParser();
//		
//		try {
//			
//			object.parseFile(fileName);
//			this.vertices = object.getVertArray();
//			this.normals = object.getNormArray();
//			this.faces = object.getFacesArray();
//			this.numVerts = object.numElements("vertex");
//			this.numFaceIndex = this.faces.length;
//			
//			this.vBuf = Buffers.newDirectFloatBuffer(vertices);
//			this.nBuf = Buffers.newDirectFloatBuffer(normals);
//			this.fBuf = Buffers.newDirectIntBuffer(faces);
//		
//		} catch (Exception ex) {
//			
//			System.err.println("Could not load PLY file");
//			ex.printStackTrace();
//		}		
	}
	
}
