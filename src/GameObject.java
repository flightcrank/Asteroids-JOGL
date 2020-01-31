

/**
 *
 * @author karma
 */
public abstract class GameObject {
	
	boolean visable;	//visibility of object
	float posX;		//x position in the game world
	float posY;		//y position in the game world
	float vX;		//the velocity vector of the game object
	float vY;		//the velocity vector of the game object
	float rot;		//the amount the game object is rotated in radians
	float scale;		//the scale of the object
	float[] verts;		//the vertices that make up the game object
	
	public GameObject() {
		
		visable = true;
		posX = 0.0f;
		posY = 0.0f;
		rot = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		scale = 1.0f;
	}
	
	//each instance must overide this method
	public abstract void update(int w, int h);
}
