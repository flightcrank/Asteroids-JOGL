
/**
 *
 * @author karma
 */

public class Ship extends GameObject {
	
	float dX;		//normalised direction the ship is pointing on the x axis
	float dY;		//normalised direction the ship is pointing on the y axis
	float rotSpeed;		//the increment in which the ships rotates
	boolean thrust;		//if thrust is being apllied
	int lives;		//amount of lives left
	Bullet[] bullets;	//bullets the ship is able to shoot
	Parts[] parts;

	public Ship() {
		
		dX = 0.0f;
		dY = 0.0f;
		rotSpeed = 0.0f;
		thrust = false;
		lives = 3;
		bullets = new Bullet[3];
		parts = new Parts[4];
		sprite = new Sprite2D(512, 512);
		sprite.setIndex(0);
		sprite.setScale(1);
		sprite.setSize(64, 64);
		//sprite.setPosition(200, 100);

		//set up the bullet propertys
		for (int i = 0; i < bullets.length; i++) {
			
			bullets[i] = new Bullet();	
		}
		
		//set up the parts sprites
		for (int i = 0; i < parts.length; i++) {
			
			parts[i] = new Parts();
			parts[i].sprite = new Sprite2D(512, 512);
			parts[i].sprite.setIndex(i + 1);
			parts[i].sprite.setScale(45);
			parts[i].sprite.setSize(64, 64);
			parts[i].vX = (float) Math.random() - 0.5f;
			parts[i].vY = (float) Math.random() - 0.5f;
		}
	}
	
	public void reset() {
		
		dX = 0.0f;
		dY = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		rotSpeed = 0.0f;
		sprite.setScale(1);
		sprite.setPosition(0, 0);
	}

	public void setDirection() {
		
		//calculate ships normalised direction vector for the x and y axis
		dX = (float) Math.sin(sprite.rot);
		dY = (float) Math.cos(sprite.rot);
	}
	
	public void checkBounds(int w, int h) {

		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		sprite.position[0]= (sprite.position[0] >  halfWidth)  ? sprite.position[0] =   -halfWidth  : sprite.position[0];
		sprite.position[0] = (sprite.position[0] < -halfWidth)  ? sprite.position[0] =   halfWidth  : sprite.position[0];
		sprite.position[1] = (sprite.position[1] >  halfHeight) ? sprite.position[1] =  -halfHeight : sprite.position[1];
		sprite.position[1] = (sprite.position[1] < -halfHeight) ? sprite.position[1] =   halfHeight : sprite.position[1];
	}
	
	//@Override //update player position and rotation
	public void update(int w, int h) {
		
		sprite.rot += rotSpeed;
		sprite.position[0] += vX;
		sprite.position[1] += vY;
		
		//check if the ship as reached the bounds of the screen
		checkBounds(w, h);
		
		//set the ships normalised direction vector
		setDirection();
		
		if (thrust) {

			//set a limit on the ships maximum velocity
			float limit = 2.5f;
			
			//if true set velocity to limit
			//if false add .02 to velocity based on the ships normalised direction vector
			vX = (vX >  limit) ?  limit : vX + (-dX * .017f);
			vX = (vX < -limit) ? -limit : vX + (-dX * .017f);
			vY = (vY >  limit) ?  limit : vY + (-dY * .017f);
			vY = (vY < -limit) ? -limit : vY + (-dY * .017f);
		}
	}
}
