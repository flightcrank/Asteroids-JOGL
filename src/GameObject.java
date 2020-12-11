

/**
 *
 * @author karma
 */
public abstract class GameObject {
	
	boolean visable;	//visibility of object
	float vX;		//the velocity vector of the game object
	float vY;		//the velocity vector of the game object
	float rot;		//the amount the game object is rotated in radians
	Sprite2D sprite;	//the sprite representing the game object
	
	public GameObject() {
		
		visable = true;
		rot = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
	}
	
	//each instance must overide this method
	public abstract void update(int w, int h);
}
