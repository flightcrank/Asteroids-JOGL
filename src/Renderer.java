
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;

public class Renderer implements GLEventListener {

	int renderingProgram;
	int vao[] = new int[1]; //vertex attribute object
	int vbo[] = new int[2]; //vertex buffer object
	int texo; 		//texture id
	int numVerts = 0;
	int width;
	int height;
	int score;
	long startTime = 0;
	FloatBuffer vBuf;
	FloatBuffer tBuf;
	GLAutoDrawable glAutoDrawable;
	GameObject title;
	GameObject blast;
	ArrayList<Asteroid> asteroids;
	Ship player;
	Scene scene;
	Music rocketSFX;
	Sound shipDeath;
	Sound astroidHitSFX;
	
	Renderer(Ship player, ArrayList asteroids, Scene scene, Music clip, Sound shipDeath, Sound asteroidHitSFX) {
		
		this.player = player;
		this.asteroids = asteroids;
		this.scene = scene;
		this.score = 0;
		this.rocketSFX = clip;
		this.shipDeath = shipDeath;
		this.astroidHitSFX = asteroidHitSFX;
		
		this.title = new GameObject();
		title.sprite = new Sprite2D(512, 512);
		title.sprite.setIndex(3);
		title.sprite.rot = 0.3f;
		title.sprite.setScale(512, 128);
		title.sprite.setSize(512, 128);
		title.sprite.setPosition(0, -300);
		
		blast = new GameObject();
		blast.sprite = new Sprite2D(512, 512);
		blast.sprite.setIndex(6);
		blast.sprite.setScale(45, 25);
		blast.sprite.setSize(64, 64);
		blast.sprite.setOrigin(0f, -1.5f);
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
		int time = gl.glGetUniformLocation(renderingProgram, "time");
		gl.glUniform1f(time, System.currentTimeMillis() - this.startTime);
		
		int ortho = gl.glGetUniformLocation(renderingProgram, "ortho");
		gl.glUniformMatrix4fv(ortho, 1, false, Buffers.newDirectFloatBuffer(Matrix.orthographic(0f, width, 0f, height, -1f, 1f)));
		
		//flag to toggle SDF rendering on or off
		int flag = gl.glGetUniformLocation(renderingProgram, "flag");
		gl.glUniform1i(flag, 1);
		
		//screen resolution
		int res = gl.glGetUniformLocation(renderingProgram, "res");
		gl.glUniform2f(res, width, height);
		
		int sheild = gl.glGetUniformLocation(renderingProgram, "sheild");
		gl.glUniform1i(sheild, 0);
		
		//DRAW
		switch(scene) {
			
			case TITLE:

				drawAsteroids(gl);
				drawTitle(gl);
				break;
				
			case GAME:
				
				drawLives(gl);
				drawScore(gl);
				drawBullets(gl);
				drawAsteroids(gl);
				drawParts(gl);
				drawBlast(gl);
				drawShip(gl);
				checkPlayerCollision();
				checkBulletCollision();
				break;
				
			case GAME_OVER:
				
				drawAsteroids(gl);
				drawParts(gl);
				drawString("GAME OVER", 0, 0, 30, gl);
				break;
				
			case PLAY_AGAIN_GO:
				
				drawAsteroids(gl);
				drawParts(gl);
				drawString("GAME OVER", 0, 0, 30, gl);
				drawString("PRESS ANY KEY", 0, 100, 22, gl);
				drawString("TO PLAY AGAIN", 0, 120, 22, gl);
				break;
			
			case PLAY_AGAIN_W:
				
				drawBlast(gl);
				drawShip(gl);
				drawBullets(gl);
				drawString("YOU WON!", 0, 0, 30, gl);
				drawString("PRESS ANY KEY", 0, 100, 22, gl);
				drawString("TO PLAY AGAIN", 0, 120, 22, gl);
				break;
			
			case WINNER:
				
				drawBlast(gl);
				drawShip(gl);
				drawBullets(gl);
				drawString("YOU WON!", 0, 0, 30, gl);
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
	
	public void drawGameObject(GL3 gl, GameObject obj) {
			
		//shader uniform variables
		int rotation = gl.glGetUniformLocation(renderingProgram, "rotate");
		gl.glUniformMatrix2fv(rotation, 1, true, Buffers.newDirectFloatBuffer(Matrix.rot2D(obj.sprite.rot)));

		int scale = gl.glGetUniformLocation(renderingProgram, "scale");
		gl.glUniform2f(scale, obj.sprite.scale[0], obj.sprite.scale[1]);

		int pos = gl.glGetUniformLocation(renderingProgram, "pos");
		gl.glUniform2f(pos, obj.sprite.position[0] - obj.sprite.scale[0], obj.sprite.position[1] - obj.sprite.scale[1]);

		int origin = gl.glGetUniformLocation(renderingProgram, "origin");
		gl.glUniform2f(origin, obj.sprite.origin[0], obj.sprite.origin[1]);

		this.vBuf = Buffers.newDirectFloatBuffer(obj.sprite.verts);
		this.tBuf = Buffers.newDirectFloatBuffer(obj.sprite.uvs);
		this.numVerts = obj.sprite.verts.length / 3;

		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 

		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make tex buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 

		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
		//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
	}
	
	public void drawParts(GL3 gl) {
		
		for (int i = 0; i < player.parts.length; i ++) {
			
			if (player.parts[i].visable == true) {

				player.parts[i].sprite.position[0] += player.parts[i].vX;
				player.parts[i].sprite.position[1] += player.parts[i].vY;
				drawGameObject(gl, player.parts[i]);
				
				long currentTime = System.currentTimeMillis();
				long diff = currentTime - player.parts[i].spawnTime;
				
				if (diff >= 2000 && player.lives > 0 ) {
				
					player.parts[i].visable = false;
					player.visable = true;
				}
			}			
		}
	}
	
	public void drawScore(GL3 gl) {	
		
		String str = String.format("%06d", score);
		int yPos = (height / 2) - 15;	
		drawString(str, 0, -yPos, 22, gl);	
	}
	
	public void drawBullets(GL3 gl) {
		
		for (Bullet bullet : player.bullets) {
			
			if (bullet.visable == true) {
		
				bullet.update((int) width, (int) height);
				drawGameObject(gl, bullet);
			}	
		}
	}
	
	public void checkBulletCollision() {
		
		for (int i = 0; i < asteroids.size(); i++) {
		
			Asteroid a = asteroids.get(i);
			
			for (int j = 0; j < player.bullets.length; j++) {

				Bullet bullet = player.bullets[j];

				boolean b = a.checkCollision(bullet);

				if (b == true && bullet.visable == true) {

					asteroids.remove(i);
					bullet.visable = false;
					astroidHitSFX.play(0.5);

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
				}
			}
		}
	}
	
	public void checkPlayerCollision() {
		
		for (Asteroid a : asteroids) {	
		
			boolean p = false;

			//if not sheild check collision
			if (!player.sheild) {

				p = a.checkCollision(player);
			}

			if (p == true) {

				for(int j = 0; j < player.parts.length; j++) {

					player.parts[j].sprite.setPosition(player.sprite.position[0], player.sprite.position[1]);
					player.parts[j].sprite.rot = player.sprite.rot;
					player.parts[j].visable = true;
					player.parts[j].spawnTime = System.currentTimeMillis();
				}

				player.visable = false;
				player.lives--;
				player.reset();
				shipDeath.play(0.5);

				if (player.lives == 0) {

					player.visable = false;
					scene = Scene.GAME_OVER;
				}
			}
		}
	}
	
	public void drawAsteroids(GL3 gl) {
		
		//if all asteroids are distroid
		if (asteroids.isEmpty()) {
			
			scene = Scene.WINNER;
		}
		
		for (int i = 0; i < asteroids.size(); i++) {
			
			Asteroid a = asteroids.get(i);
			
			a.update(width, height);
			drawGameObject(gl, a);	
		}
	}
	
	public void drawShip(GL3 gl) {
		
		if (player.visable == true && player.lives > 0) {
			
			//scale ship tween
			if (player.sprite.scale[0] < 22) {
			
				float ease = 0.1f;
				float targetX = 22.5f;
				float targetY = 22.5f;
				float dX = targetX - player.sprite.scale[0];
				float dY = targetY - player.sprite.scale[1];
				float vX = dX * ease;
				float vY = dY * ease;
				player.sprite.scale[0] += vX;
				player.sprite.scale[1] += vY;
			}
					
			int f = (player.sheild == true) ? 1 : 0;
			int sheild = gl.glGetUniformLocation(renderingProgram, "sheild");
			gl.glUniform1i(sheild, f);
			
			drawGameObject(gl, player);
			player.update(width, height);
			blast.sprite.rot = player.sprite.rot;
			blast.sprite.setPosition(player.sprite.position[0], player.sprite.position[1]);
						
		} else {
			
			player.visable = false;
			rocketSFX.stop();
		}
	}
	
	public void drawTitle(GL3 gl) {
		
		GameObject[] icon = new GameObject[3];
		int scale = 40;
		float x = -(scale * icon.length) / 3;
		
		for (int i = 0; i < icon.length; i++) {
			
			icon[i] = new GameObject();
			icon[i].sprite = new Sprite2D(512, 512);
			icon[i].sprite.setIndex(8 + i);
			icon[i].sprite.setScale(scale);
			icon[i].sprite.setSize(64, 64);
			icon[i].sprite.setPosition(x + i * scale , 225);
		}
		
		score = 0;
		
		for (int i = 0; i < player.parts.length; i++) {
						
			player.parts[i].visable = false;
		}
				
		if (Math.abs(title.sprite.position[1]) > 101) {
			
			float ease = 0.06f;
			float targetX = 0f;
			float targetY = -100f;
			float dX = targetX - title.sprite.position[0];
			float dY = targetY - title.sprite.position[1];
			float vX = dX * ease;
			float vY = dY * ease;
			title.sprite.position[0] += vX;
			title.sprite.position[1] += vY;
			
		} else {
			
			drawString("PRESS SPACE TO START", 0, 50, 22, gl);
			drawString("-2020 : JOSHUA LAMBERT : V1.0-", 0, 190, 14, gl);
		}
		
		drawGameObject(gl, title);
		
		for (int i = 0; i < icon.length; i++) {
		
			drawGameObject(gl, icon[i]);
		}
	}
	
	public void drawLives(GL3 gl) {
			
		GameObject lives = new GameObject();
		lives.sprite = new Sprite2D(512, 512);
		lives.sprite.setIndex(0);
		lives.sprite.setScale(24, 24);
		lives.sprite.setSize(64, 64);
		
		float[] translate = {-width / 2, -height / 2};
		
		for (int i = 0; i < player.lives; i++) {
			
			int offset =  i * (int) (lives.sprite.scale[0] + 10);
			lives.sprite.setPosition(offset + translate[0] + lives.sprite.scale[0], translate[1] + lives.sprite.scale[1]);
			drawGameObject(gl, lives);
		}
	}
	
	public void drawBlast(GL3 gl) {
			
		if (player.thrust && player.visable == true) {
		
			if (blast.sprite.scale[1] < 25) {
			
				float ease = 0.01f;
				float targetY = 25f;
				float dY = targetY - blast.sprite.scale[1];
				float vY = dY * ease;
				blast.sprite.scale[1] += vY;
			}			
			
			drawGameObject(gl, blast);

		} else {
			
			blast.sprite.setScale(45, 25);
		}
	}
	
	//will not center the text correctly if size is a odd number
	public void drawString(String str, int x, int y, int size, GL3 gl) {

		TextChar tc = new TextChar();
		tc.sprite.setScale(size);
		
		int pxLen = str.length() * size;
		x = -pxLen / 2 + (int) tc.sprite.scale[0];

		for (int i = 0; i < str.length(); i++) {
			
			int charIndex = tc.fontLayout.indexOf(str.charAt(i));
			int offset = 128 + 4;	//index offset of where the first char starts in the sprite sheet.
			int index = charIndex + offset + (charIndex / 12) * 4;
			tc.sprite.setIndex(index);
			tc.sprite.setSize(32, 32);
			tc.sprite.setPosition(x + i * size, y);
			drawGameObject(gl, tc);
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