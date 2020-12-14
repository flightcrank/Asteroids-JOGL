
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
	int score;
	Sprite2D title;
	Sprite2D blast;
	
	public Renderer(Ship player, ArrayList asteroids, Scene scene) {
		
		this.player = player;
		this.asteroids = asteroids;
		this.scene = scene;
		this.score = 0;
		
		this.title = new Sprite2D(512, 512);
		title.setIndex(3);
		title.setScale(512, 128);
		title.setSize(512, 128);
		title.setPosition(0, -300);
		
		blast = new Sprite2D(512, 512);
		blast.setIndex(6);
		blast.setScale(45, 25);
		blast.setSize(64, 64);
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

				drawAsteroids(gl);
				drawTitle(gl);
				break;
				
			case GAME:
				
				drawShip(gl);
				drawBlast(gl);
				drawBullets(gl);
				drawAsteroids(gl);
				drawLives(gl);
				drawScore(gl);
				break;
				
			case GAME_OVER:
				
				drawAsteroids(gl);
				drawString("GAME OVER", 0, 0, gl);
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
	
	public void drawScore(GL3 gl) {
		
		
		String str = String.format("%06d", score);
		int yPos = (height / 2) - 15;
		
		drawString(str, 0, -yPos, gl);
		
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
				gl.glUniform2f(pos, bullet.sprite.position[0] - (bullet.sprite.scale[0]), bullet.sprite.position[1] - (bullet.sprite.scale[1]));

				int res = gl.glGetUniformLocation(renderingProgram, "res");
				gl.glUniform2f(res, width, height);
				
				int origin = gl.glGetUniformLocation(renderingProgram, "origin");
				gl.glUniform2f(origin, 0, 0);

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
				gl.glUniform2f(pos, a.sprite.position[0] - (a.sprite.scale[0]), a.sprite.position[1] - (a.sprite.scale[0]));

				int res = gl.glGetUniformLocation(renderingProgram, "res");
				gl.glUniform2f(res, width, height);
				
				int origin = gl.glGetUniformLocation(renderingProgram, "origin");
				gl.glUniform2f(origin, 0, 0);

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
						
					player.lives--;
					player.reset();
					
					System.out.println(player.lives);
					if (player.lives == 0) {
						
						scene = Scene.GAME_OVER;
					}
				}
				
				for (int j = 0; j < player.bullets.length; j++) {
					
					Bullet bullet = player.bullets[j];
					
					int b = a.checkCollision(bullet);
					
					if (b == 1 && bullet.visable == true) {
						
						bullet.visable = false;
						
						switch(a.size) {
							
							case BIG:
								
								score += 10;
								asteroids.add(new Asteroid(Size.MEDIUM, a.sprite.position[0], a.sprite.position[1]));
								asteroids.add(new Asteroid(Size.MEDIUM, a.sprite.position[0], a.sprite.position[1]));
								asteroids.add(new Asteroid(Size.MEDIUM, a.sprite.position[0], a.sprite.position[1]));
								break;
								
							case MEDIUM:
								
								score += 20;
								asteroids.add(new Asteroid(Size.SMALL, a.sprite.position[0], a.sprite.position[1]));
								asteroids.add(new Asteroid(Size.SMALL, a.sprite.position[0], a.sprite.position[1]));
								asteroids.add(new Asteroid(Size.SMALL, a.sprite.position[0], a.sprite.position[1]));
								break;
								
							case SMALL:
								
								score += 30;
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
		
		if (player.sprite.scale[0] < 22) {
			
			float ease = 0.2f;
			float targetX = 22.5f;
			float targetY = 22.5f;
			float dX = targetX - player.sprite.scale[0];
			float dY = targetY - player.sprite.scale[1];
			float vX = dX * ease;
			float vY = dY * ease;
			player.sprite.scale[0] += vX;
			player.sprite.scale[1] += vY;
		}
		
		if (player.lives > 0) {

			player.update(width, height);
			blast.setPosition(player.sprite.position[0], player.sprite.position[1]);
			
			//System.out.println(player.posX + ", " + player.posY);
			//shader uniform variables
			int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
			gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(player.rot)));

			int scale = gl.glGetUniformLocation(renderingProgram, "scale");
			gl.glUniform2f(scale, player.sprite.scale[0], player.sprite.scale[1]);

			int pos = gl.glGetUniformLocation(renderingProgram, "pos");
			gl.glUniform2f(pos, player.sprite.position[0] - (player.sprite.scale[0]), player.sprite.position[1] - (player.sprite.scale[0]));

			int res = gl.glGetUniformLocation(renderingProgram, "res");
			gl.glUniform2f(res, width, height);
			
			int origin = gl.glGetUniformLocation(renderingProgram, "origin");
			gl.glUniform2f(origin, 0, 0);

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
		
		if (Math.abs(title.position[1]) > 101) {
			
			float ease = 0.06f;
			float targetX = 0f;
			float targetY = -100f;
			float dX = targetX - title.position[0];
			float dY = targetY - title.position[1];
			float vX = dX * ease;
			float vY = dY * ease;
			title.position[0] += vX;
			title.position[1] += vY;
			
		} else {
			
			drawString("PRESS SPACE TO START", 0, 150, gl);
		}
		
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
		lives.setSize(64, 64);
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
			
			int origin = gl.glGetUniformLocation(renderingProgram, "origin");
			gl.glUniform2f(origin, 0, 0);

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
	
	public void drawBlast(GL3 gl) {
		
		if (player.thrust) {
		
			if (blast.scale[1] < 25) {
			
				float ease = 0.01f;
				float targetY = 25f;
				float dY = targetY - blast.scale[1];
				float vY = dY * ease;
				blast.scale[1] += vY;
			}			

			//shader uniform variables
			int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
			gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(player.rot)));

			int scale = gl.glGetUniformLocation(renderingProgram, "scale");
			gl.glUniform2f(scale, blast.scale[0], blast.scale[1]);

			int pos = gl.glGetUniformLocation(renderingProgram, "pos");
			gl.glUniform2f(pos, blast.position[0] - blast.scale[0], blast.position[1] - blast.scale[1]);

			int res = gl.glGetUniformLocation(renderingProgram, "res");
			gl.glUniform2f(res, width, height);
			
			int origin = gl.glGetUniformLocation(renderingProgram, "origin");
			gl.glUniform2f(origin, 0f, -1.5f);

			this.vBuf = Buffers.newDirectFloatBuffer(blast.verts);
			this.tBuf = Buffers.newDirectFloatBuffer(blast.uvs);
			this.numVerts = blast.verts.length / 3;

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

			gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
			//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
		
		} else {
			
			blast.setScale(45, 25);
		}
	}
	
	public void drawString(String str, int x, int y, GL3 gl) {
		
		String fontSprites = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
		Sprite2D sprite = new Sprite2D(512, 512);
		sprite.setScale(20);
		int pxLen = str.length() * (int) sprite.scale[0] * 2;
		x = -pxLen / 2 + (int) sprite.scale[0];

		for (int i = 0; i < str.length(); i++) {
			
			int charIndex = fontSprites.indexOf(str.charAt(i));
			int offset = 128 + 4;	//index offset of where the first char starts in the sprite sheet.
			int index = charIndex + offset + (charIndex / 12) * 4;
			sprite.setIndex(index);
			sprite.setSize(32, 32);
			sprite.setPosition(x + i * sprite.scale[1] * 2, y);
			drawChar(gl, sprite);
		}
	}
	
	public void drawChar(GL3 gl, Sprite2D sprite) {
		
		//shader uniform variables
		int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
		gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(0)));
		
		int scale = gl.glGetUniformLocation(renderingProgram, "scale");
		gl.glUniform2f(scale, sprite.scale[0], sprite.scale[1]);
		
		int pos = gl.glGetUniformLocation(renderingProgram, "pos");
		gl.glUniform2f(pos, sprite.position[0] - (sprite.scale[0]), sprite.position[1] - (sprite.scale[1]));
		
		int res = gl.glGetUniformLocation(renderingProgram, "res");
		gl.glUniform2f(res, width, height);
		
		int origin = gl.glGetUniformLocation(renderingProgram, "origin");
		gl.glUniform2f(origin, 0, 0);
		
		this.vBuf = Buffers.newDirectFloatBuffer(sprite.verts);
		this.tBuf = Buffers.newDirectFloatBuffer(sprite.uvs);
		this.numVerts = sprite.verts.length / 3;
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
		//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
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
