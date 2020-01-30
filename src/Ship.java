
/**
 *
 * @author karma
 */

public class Ship {
	
	float posX;	//the x position of the ship in the world
	float posY;	//the y position of the ship in the world
	float vX;	//the velocity vector of the ship
	float vY;	//the velocity vector of the ship
	float rot;	//the rotation amount of the ship in radians
	float rotSpeed;	//the increment in which the ships rotates
	boolean thrust;
	float[] verts;

	public Ship() {
		
		posX = 0.0f;
		posY = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		rot = 0.0f;
		rotSpeed = 0.0f;
		thrust = false;
		verts = new float[] {0.0f,  1.0f, 0.0f,
				    -1.0f, -1.0f, 0.0f,
				     1.0f, -1.0f, 0.0f};
	}
	
	//update players position and rotation
	public void update(int w, int h) {
		
		rot += rotSpeed;
		posX += vX;
		posY += vY;
		
		System.out.println(String.format("%.2f", vY));
		
		//screen X wrap around	
		if (posX > w / 2) {
			
			posX = -256;
		
		} else if (posX < -w / 2) {
			
			posX = 256;
		}
		
		//screen Y wrap around
		if (posY > h / 2) {
			
			posY = -256;
		
		} else if (posY < -h / 2) {
			
			posY = 256;
		}
		
		if (thrust) {
			
			//calculate ships normalised direction vector for the x and y axis
			float dvX = (float) Math.sin(rot);
			float dvY = (float) Math.cos(rot);
			
			//will add or subtract from the ships velocity based on the ships direction vector
			vY += dvY * .05;
			vX += dvX * .05;
			
			//set a limit on the ships velocity
			float limit = 2.5f;
			
			if (vX > limit) {
				
				vX = limit;
				
			} else if (vX < -limit) {
				
				vX = -limit;
			}
			
			if (vY > limit) {
				
				vY = limit;
				
			} else if (vY < -limit) {
				
				vY = -limit;
			}
		}
	}
}
