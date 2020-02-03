
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

		//set up the bullet propertys
		for (int i = 0; i < bullets.length; i++) {
			
			bullets[i] = new Bullet();
			
		}
		
		
	}
	
	public static float[] getVerts() {
		
		return new float[] {	0.0f, -0.5f, 0.0f,
					1.0f,  -1.0f,  0.0f,
					0.0f,   1.0f,  0.0f,
					0.0f,   1.0f,  0.0f,
				       -1.0f,  -1.0f,  0.0f,
					0.0f,  -0.5f,  0.0f,
		
					0.500000f, -1.000000f, 0.000000f,
0.200000f, -1.100000f, 0.000000f,
0.400000f, -0.800000f, 0.000000f,
0.000000f, -1.500000f, 0.000000f,
0.000000f, -0.600000f, 0.000000f,
0.200000f, -1.100000f, 0.000000f,
0.400000f, -0.800000f, 0.000000f,
0.200000f, -1.100000f, 0.000000f,
0.000000f, -0.600000f, 0.000000f,
-0.500000f, -1.000000f, 0.000000f,
-0.400000f, -0.800000f, 0.000000f,
-0.200000f, -1.100000f, 0.000000f,
0.000000f, -1.500000f, 0.000000f,
-0.200000f, -1.100000f, 0.000000f,
0.000000f, -0.600000f, 0.000000f,
-0.400000f, -0.800000f, 0.000000f,
0.000000f, -0.600000f, 0.000000f,
-0.200000f, -1.100000f, 0.000000f};
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
		
		//check if the ship as reached the bounds of the screen
		checkBounds(w, h);
		
		//set the ships normalised direction vector
		setDirection();
		
		if (thrust) {

			//set a limit on the ships maximum velocity
			float limit = 2.5f;
			
			//if true set velocity to limit
			//if false add .02 to velocity based on the ships normalised direction vector
			vX = (vX >  limit) ?  limit : vX + (dX * .02f);
			vX = (vX < -limit) ? -limit : vX + (dX * .02f);
			vY = (vY >  limit) ?  limit : vY + (dY * .02f);
			vY = (vY < -limit) ? -limit : vY + (dY * .02f);
		}
	}
}
