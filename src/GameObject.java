

/**
 *
 * @author karma
 */
public class GameObject {
	
	boolean visable;	//visibility of object
	float vX;		//the velocity vector of the game object
	float vY;		//the velocity vector of the game object
	Sprite2D sprite;	//the sprite representing the game object
	
	public GameObject() {
		
		visable = true;
		vX = 0.0f;
		vY = 0.0f;
	}
}
