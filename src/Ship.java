
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
	Sprite2D sprite;

	public Ship() {
		
		dX = 0.0f;
		dY = 0.0f;
		rotSpeed = 0.0f;
		scale = 45;
		thrust = false;
		lives = 3;
		bullets = new Bullet[3];
		sprite = new Sprite2D(512, 512);
		sprite.setIndex(0);
		sprite.setScale(scale);
		sprite.setSize(128, 128);

		//set up the bullet propertys
		for (int i = 0; i < bullets.length; i++) {
			
			bullets[i] = new Bullet();	
		}		
	}
	
	public void reset() {
		
		dX = 0.0f;
		dY = 0.0f;
		posX = 0.0f;
		posY = 0.0f;
		rot = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		rotSpeed = 0.0f;
	}

	public void setDirection() {
		
		//calculate ships normalised direction vector for the x and y axis
		dX = (float) Math.sin(rot);
		dY = (float) Math.cos(rot);
	}
	
	public void checkBounds(int w, int h) {

		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		posX = (posX >  halfWidth)  ? posX =  -halfWidth  : posX;
		posX = (posX < -halfWidth)  ? posX =   halfWidth  : posX;
		posY = (posY >  halfHeight) ? posY =  -halfHeight : posY;
		posY = (posY < -halfHeight) ? posY =   halfHeight : posY;
	}
	
	@Override //update player position and rotation
	public void update(int w, int h) {
		
		rot += rotSpeed;
		posX += vX;
		posY += vY;
		//sprite.setPosition(posX, posY);
		
		//check if the ship as reached the bounds of the screen
		checkBounds(w, h);
		
		//set the ships normalised direction vector
		setDirection();
		
		if (thrust) {

			//set a limit on the ships maximum velocity
			float limit = 2.5f;
			
			//if true set velocity to limit
			//if false add .02 to velocity based on the ships normalised direction vector
			vX = (vX >  limit) ?  limit : vX + (-dX * .03f);
			vX = (vX < -limit) ? -limit : vX + (-dX * .03f);
			vY = (vY >  limit) ?  limit : vY + (-dY * .03f);
			vY = (vY < -limit) ? -limit : vY + (-dY * .03f);
		}
	}
}
