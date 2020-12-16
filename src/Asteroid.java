
/**
 *
 * @author karma
 */
enum Size {BIG, MEDIUM, SMALL};

public class Asteroid extends GameObject {

	Size size;
	float rotSpeed;
	
	Asteroid(Size s) {
		
		double r = Math.ceil(Math.random() * 3);
		sprite = new Sprite2D(512, 512);
		sprite.setIndex((int) r + 5); 
		sprite.setSize(128, 128);
		sprite.position[0] = (float) ((Math.random() - .5) * 2) * 1000f;
		sprite.position[1] = (float) ((Math.random() - .5) * 2) * 1000f;
		init(s);
	}
	
	Asteroid(Size s, float x, float y) {
		
		double r = Math.ceil(Math.random() * 3);
		sprite = new Sprite2D(512, 512);
		sprite.position[0] = x;
		sprite.position[1] = y;
		sprite.setIndex((int) r + 5);
		sprite.setSize(128, 128);
		init(s);
	}
	
	private void init(Size s) {
		
		switch (s) {
			
			case BIG:
				sprite.setScale(80.0f);
				break;
				
			case MEDIUM:
				sprite.setScale(40.0f);
				break;
				
			case SMALL:
				sprite.setScale(25.0f);
				break;	
		}
		
		size = s;
		
		vX = (float) ((Math.random() - .5) * 2) * .5f;
		vY = (float) ((Math.random() - .5) * 2) * .5f;
		rotSpeed = (float) ((Math.random() - .5) * 2) * .03f;
	}

	public void update(int w, int h) {
		
		sprite.position[0] += vX;
		sprite.position[1] += vY;
		sprite.rot += rotSpeed;
		checkBounds(w, h);
	}
	
	public void checkBounds(int w, int h) {
		
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		sprite.position[0] = (sprite.position[0] >  halfWidth)  ? sprite.position[0] =  -halfWidth  : sprite.position[0];
		sprite.position[0] = (sprite.position[0] < -halfWidth)  ? sprite.position[0] =   halfWidth  : sprite.position[0];
		sprite.position[1] = (sprite.position[1] >  halfHeight) ? sprite.position[1] =  -halfHeight : sprite.position[1];
		sprite.position[1] = (sprite.position[1] < -halfHeight) ? sprite.position[1] =   halfHeight : sprite.position[1];
	}
	
	public boolean checkCollision(GameObject obj) {
		
		if (obj.visable == true) {
			
			double a = sprite.position[0] - obj.sprite.position[0];
			double b = sprite.position[1] - obj.sprite.position[1];
			double dist = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));	//distance between asteroid and obj
		
			if (dist < sprite.scale[0]) {

				return true;
			}
		}
		return false;
	}
}
