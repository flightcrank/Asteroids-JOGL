
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;

class Renderer implements GLEventListener {

	int renderingProgram;
	int vao[] = new int[1]; //vertex attribute object
	int vbo[] = new int[2]; //vertex buffer object
	int texo; 		//texture id
	int numVerts = 0;
	long startTime = 0;
	int width;
	int height;
	FloatBuffer vBuf;
	FloatBuffer tBuf;
	GLAutoDrawable glAutoDrawable;
	Ship player;
	ArrayList<Asteroid> asteroids;
	Scene scene;
	
	public Renderer(Ship player, ArrayList asteroids, Scene scene) {
		
		this.player = player;
		this.asteroids = asteroids;
		this.scene = scene;
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
	
		this.glAutoDrawable = glAutoDrawable;	
		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		this.startTime = System.currentTimeMillis();
		
		//load texture
		loadTexture("ship.png");

		gl.glPointSize(15.0f);
		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL3.GL_BLEND);
		//gl.glEnable(GL3.GL_DEPTH_TEST);  
		//gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
		
		//set background colour
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		
		//generate OPENGL buffers
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glGenBuffers(vbo.length, vbo, 0);	//generate vertex AND normal buffer AND texture buffer
		
		//VAO
		gl.glBindVertexArray(vao[0]);	//make vertex attribute object 0 active 	
		
//		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make normal buffer active 
//		gl.glBufferData(GL3.GL_ARRAY_BUFFER, nBuf.limit() * Buffers.SIZEOF_FLOAT, nBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 
		
//		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0]);								//make faces buffer active 
//		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, fBuf.limit() * Buffers.SIZEOF_INT, fBuf, GL3.GL_STATIC_DRAW);	//copy fance indexs to EBO[0]
		
		//load/compile shaders from file
		renderingProgram = createShaders();
	}
		
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {

		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		//use compiled shaders
		gl.glUseProgram(renderingProgram);
		
		//shader uniform variables
		int panelResolution = gl.glGetUniformLocation(renderingProgram, "panelResolution");
		gl.glUniform2f(panelResolution, glAutoDrawable.getSurfaceWidth(), glAutoDrawable.getSurfaceHeight());

		int time = gl.glGetUniformLocation(renderingProgram, "time");
		gl.glUniform1f(time, System.currentTimeMillis() - this.startTime);
				
		// activate texture unit #0 and bind it to the texture object
		gl.glActiveTexture(GL3.GL_TEXTURE0);
		gl.glBindTexture(GL3.GL_TEXTURE_2D, texo);

		//vert position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);		///make vert buffer active
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(0);
		
		//tex position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);		//make normal buffer active
		gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 2, 0);
		gl.glEnableVertexAttribArray(1);
		
		//set background colour and clear the screen
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
	
		//shader uniform variables
		int ortho = gl.glGetUniformLocation(renderingProgram, "ortho");
		gl.glUniformMatrix4fv(ortho, 1, false, Buffers.newDirectFloatBuffer(Matrix.orthographic(0f, width, 0f, height, -1f, 1f)));
		
		int flag = gl.glGetUniformLocation(renderingProgram, "flag");
		gl.glUniform1i(flag, 1);
		
		//DRAW
		switch(scene) {
			
			case TITLE:
				
				drawTitle(gl);
				break;
				
			case GAME:
				
				drawShip(gl);
				drawBullets(gl);
				drawAsteroids(gl);
				drawLives(gl);
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		
		
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		this.width = width;
		this.height = height;
		System.out.println("W = " + width + " H = " + height);
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
	
	public void drawBullets(GL3 gl) {
		
		for (Bullet bullet : player.bullets) {
			
			if (bullet.visable == true) {
		
				bullet.update((int) width, (int) height);

				//shader uniform variables
				int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
				gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(bullet.rot)));
				
				int scale = gl.glGetUniformLocation(renderingProgram, "scale");
				gl.glUniform2f(scale, player.sprite.scale[0], player.sprite.scale[1]);

				int pos = gl.glGetUniformLocation(renderingProgram, "pos");
				gl.glUniform2f(pos, bullet.posX - (bullet.scale / 2), bullet.posY - (bullet.scale / 2));

				int res = gl.glGetUniformLocation(renderingProgram, "res");
				gl.glUniform2f(res, width, height);

				this.vBuf = Buffers.newDirectFloatBuffer(bullet.sprite.verts);
				this.tBuf = Buffers.newDirectFloatBuffer(bullet.sprite.uvs);
				this.numVerts = bullet.sprite.verts.length / 3;

				gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
				gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

				gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
				gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

				gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
				//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
			}	
		}
	}
	
	public void drawAsteroids(GL3 gl) {
		
		for (int i = 0; i < asteroids.size(); i++) {
			
			Asteroid a = asteroids.get(i);
			
			if (a.visable == true) {
				
				a.update(width, height);
				
				//shader uniform variables
				int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
				gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(a.rot)));
				
				int scale = gl.glGetUniformLocation(renderingProgram, "scale");
				gl.glUniform2f(scale, a.sprite.scale[0], a.sprite.scale[1]);

				int pos = gl.glGetUniformLocation(renderingProgram, "pos");
				gl.glUniform2f(pos, a.posX - (a.sprite.scale[0]), a.posY - (a.sprite.scale[0]));

				int res = gl.glGetUniformLocation(renderingProgram, "res");
				gl.glUniform2f(res, width, height);

				this.vBuf = Buffers.newDirectFloatBuffer(a.sprite.verts);
				this.tBuf = Buffers.newDirectFloatBuffer(a.sprite.uvs);
				this.numVerts = a.sprite.verts.length / 3;

				gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
				gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

				gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
				gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

				gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
				//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
				
				int p = a.checkCollision(player);
				
				if (p == 1) {
					
					System.out.println("Player Hit");
					player.lives--;
					player.reset();
				}
				
				for (int j = 0; j < player.bullets.length; j++) {
					
					Bullet bullet = player.bullets[j];
					
					int b = a.checkCollision(bullet);
					
					if (b == 1 && bullet.visable == true) {
						
						bullet.visable = false;
						
						switch(a.size) {
							
							case BIG:
								
								asteroids.add(new Asteroid(Size.MEDIUM, a.posX, a.posY));
								asteroids.add(new Asteroid(Size.MEDIUM, a.posX, a.posY));
								asteroids.add(new Asteroid(Size.MEDIUM, a.posX, a.posY));
								
								break;
								
							case MEDIUM:
								
								asteroids.add(new Asteroid(Size.SMALL, a.posX, a.posY));
								asteroids.add(new Asteroid(Size.SMALL, a.posX, a.posY));
								asteroids.add(new Asteroid(Size.SMALL, a.posX, a.posY));
								
								break;
						}
						
						System.out.println("Bullet Hit");
						a.visable = false;
					}
				}
			}	
		}
	}
	
	public void drawShip(GL3 gl) {
		
		if (player.lives > 0) {

			player.update(width, height);
			//System.out.println(player.posX + ", " + player.posY);
			//shader uniform variables
			int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
			gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(player.rot)));

			int scale = gl.glGetUniformLocation(renderingProgram, "scale");
			gl.glUniform2f(scale, player.sprite.scale[0], player.sprite.scale[1]);

			int pos = gl.glGetUniformLocation(renderingProgram, "pos");
			gl.glUniform2f(pos, player.posX - (player.scale / 2), player.posY - (player.scale / 2));

			int res = gl.glGetUniformLocation(renderingProgram, "res");
			gl.glUniform2f(res, width, height);

			this.vBuf = Buffers.newDirectFloatBuffer(player.sprite.verts);
			this.tBuf = Buffers.newDirectFloatBuffer(player.sprite.uvs);
			this.numVerts = player.sprite.verts.length / 3;

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

			gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
			//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
		
		} else {
			
			player.visable = false;
		}
	}
	
	public void drawTitle(GL3 gl) {
		
		Sprite2D title = new Sprite2D(512, 512);
		title.setIndex(3);
		title.setScale(512, 128);
		title.setSize(512, 128);
		title.setPosition(0, 0);
		
		//shader uniform variables
		int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
		gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(0.3f)));
		
		int scale = gl.glGetUniformLocation(renderingProgram, "scale");
		gl.glUniform2f(scale, title.scale[0], title.scale[1]);
		
		int pos = gl.glGetUniformLocation(renderingProgram, "pos");
		gl.glUniform2f(pos, title.position[0] - (title.scale[0]), title.position[1] - (title.scale[1]));
		
		int res = gl.glGetUniformLocation(renderingProgram, "res");
		gl.glUniform2f(res, width, height);
		
		this.vBuf = Buffers.newDirectFloatBuffer(title.verts);
		this.tBuf = Buffers.newDirectFloatBuffer(title.uvs);
		this.numVerts = title.verts.length / 3;
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
		//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
	}
	
	public void drawLives(GL3 gl) {
		
		Sprite2D lives = new Sprite2D(512, 512);
		lives.setIndex(0);
		lives.setScale(24, 24);
		lives.setSize(128, 128);
		lives.setPosition(0, 0);
		
		float[] translate = {-width / 2, -height / 2};
		
		for (int i = 0; i < player.lives; i++) {
			
			int offset =  i * (int) (lives.scale[0] + 10);
			
			//shader uniform variables
			int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
			gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(0)));

			int scale = gl.glGetUniformLocation(renderingProgram, "scale");
			gl.glUniform2f(scale, lives.scale[0], lives.scale[1]);

			int pos = gl.glGetUniformLocation(renderingProgram, "pos");
			gl.glUniform2f(pos, 0 + translate[0] + offset, 0 + translate[1]);

			int res = gl.glGetUniformLocation(renderingProgram, "res");
			gl.glUniform2f(res, width, height);

			this.vBuf = Buffers.newDirectFloatBuffer(lives.verts);
			this.tBuf = Buffers.newDirectFloatBuffer(lives.uvs);
			this.numVerts = lives.verts.length / 3;

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

			gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
			//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);	
		}
	}
	
	public void loadTexture(String fileName) {
		
		if (fileName != null) {
			
			try {				
				
				Texture tex = TextureIO.newTexture(new File(fileName), false);
				texo = tex.getTextureObject();

			} catch (Exception e) { 

				e.printStackTrace(); 
			}
		}	
	}
	
}
