
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

	public Ship() {
		
		dX = 0.0f;
		dY = 0.0f;
		rotSpeed = 0.0f;
		scale = 15;
		thrust = false;
		bullets = new Bullet[3];
		verts = new float[] {0.0f,  1.0f, 0.0f,
				    -1.0f, -1.0f, 0.0f,
				     1.0f, -1.0f, 0.0f};
		
		//set up the bullet verts
		for (int i = 0; i < bullets.length; i++) {
			
			bullets[i] = new Bullet();
			bullets[i] .vX = 0.1f;
			bullets[i] .vY = 0.1f;
			bullets[i] .scale = 3.0f;
			bullets[i] .visable = false;
		}
	}
	
	public void setDirection() {
		
		//calculate ships normalised direction vector for the x and y axis
		dX = (float) Math.sin(rot);
		dY = (float) Math.cos(rot);
	}
	
	public void checkBounds(int w, int h) {
		
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		//screen X wrap around	
		if (posX > halfWidth) {
			
			posX = -halfWidth;
		
		} else if (posX < -halfWidth) {
			
			posX = halfWidth;
		}
		
		//screen Y wrap around
		if (posY > halfHeight) {
			
			posY = -halfHeight;
		
		} else if (posY < -halfHeight) {
			
			posY = halfHeight;
		}
	}
	
	@Override //update player position and rotation
	public void update(int w, int h) {
		
		
		
		rot += rotSpeed;
		posX += vX;
		posY += vY;
		
		//check if the ship as reached the bounds of the screen
		checkBounds(w, h);
		
		//set the ships normalised direction vector
		setDirection();
		
		if (thrust) {
				
			//will add or subtract from the ships velocity based on the ships normaalised direction vector
			vY += dY * .05;
			vX += dX * .05;
			
			//set a limit on the ships velocity
			float limit = 2.5f;
			
			//cap veloicy so it never go's above or below a limit
			vX = (vX >  limit) ?  limit : vX;
			vX = (vX < -limit) ? -limit : vX;
			vY = (vY >  limit) ?  limit : vY;
			vY = (vY < -limit) ? -limit : vY;
		}
	}
}
