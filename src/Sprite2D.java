

/**
 * Class to represent a 2D sprite rendered in an orthographic projection
 * @author karma
 */
class Sprite2D {
	
	float[] scale;		//the scale the sprite will be renderd at in px
	float[] verts;		//the verts that make up the quad
	float[] uvs;		//the uv co-ords for each vert
	float[] position;	//the position the sprite will be drawn on screen
	int[] size;		//the size in px of the sprite in the sprite sheet
	int index;		//the index of the sprite to be drawn from the sprite sheet

	public Sprite2D(int x, int y) {
		
		float s[] = {1f, 1f};
		scale = s;
		
		float p[] = {0f, 0f};
		position = p;
		
		int d[] = {x, y};
		size = d;
		
		index = 0;
		
		setVerts();
	}
	
	/**
	 *
	 * @param i the index of the sprite in the sprite sheet
	 * 
	 * This sets index of the sprite in the sprite sheet. Index is 0 based
	 * It must also be set before the setSize function is called as that function
	 * uses the index to calculate the UVs
	*/	
	public void setIndex(int i) {
	
		this.index = i;
	}
	
	/**
	 *
	 * @param x the size in pixels of the sprites length
	 * @param y the size in pixels of the sprites height
	 * 
	 * This sets the size of the sprites width and height if part of a 
	 * sprite sheet. it would be the size of one sprite within the sprite 
	 * sheet
	 */
	public void setSize(int width, int height) {
		
		int cellsWide =  size[0] / width;
		
		int col = index % cellsWide;
		int row = index / cellsWide;
		
		float topLeftX = col * width;
		float topLeftY = size[1] - (row * height);
		
		float topLeftXUV = topLeftX / size[0];
		float topLeftYUV = topLeftY / size[1];
		float topRightXUV = (topLeftX + width) / size[0];
		float bottomLeftYUV = (topLeftY - height) / size[1];
		
		float[] uvs = {topLeftXUV,  topLeftYUV,		//top left
			       topLeftXUV,  bottomLeftYUV,	//bottom left
			       topRightXUV, bottomLeftYUV,	//bottom right
			       
			       topLeftXUV,  topLeftYUV,		//top left
			       topRightXUV, topLeftYUV,		//top right
			       topRightXUV, bottomLeftYUV,	//bottom right  
		};
		
		this.uvs = uvs;
	}
	
	/**
	 *
	 * @param x the amount in pixels to scale horizontally
	 * @param y the amount in pixels to scale vertically
	 * 
	 * scale amount is divided by 2 because the sprite is in normalised local 
	 * space of -1.0 to 1.0. So if the sprite is intended to be 100px, scale 
	 * will only need to be set to half as it is scaled on either side of 0.0;
	 */
	public void setScale(float x, float y) {
		
		this.scale[0] = x / 2;
		this.scale[1] = y / 2;
	}
	
	/**
	 *
	 * @param s the amount to scale in pixels
	 * 
	 * scale amount is divided by 2 because the sprite is in normalised local 
	 * space of -1.0 to 1.0. So if the sprite is intended to be 100px, scale 
	 * will only need to be set to half as it is scaled on either side of 0.0;
	 */
	public void setScale(float s) {
		
		this.scale[0] = s / 2;
		this.scale[1] = s / 2;
	}
	
	/**
	 *
	 * @param x position of sprite in screen space on the x axis
	 * @param y position of sprite in screen space on the y axis
	 */
	public void setPosition(float x, float y) {
		
		this.position[0] = x;
		this.position[1] = y;
	}
		
	private void setVerts() {
			
		float[] verts = {-1.0f,  1.0f, 0.0f,	//top left
				 -1.0f, -1.0f, 0.0f,	//bottom left
				  1.0f, -1.0f, 0.0f,	//bottom right
				
				 -1.0f,  1.0f, 0.0f,	//top left
				  1.0f,  1.0f, 0.0f,	//top right
				  1.0f, -1.0f, 0.0f};	//bottom right
		
		float[] uvs = {0.0f, 1.0f,	//top left
			       0.0f, 0.0f,	//bottom left
			       1.0f, 0.0f,	//bottom right
			       
			       0.0f, 1.0f,	//top left
			       1.0f, 1.0f,	//top right
			       1.0f, 0.0f,	//bottom right  
		};
		
		this.verts = verts;
		this.uvs = uvs;
	}
}
