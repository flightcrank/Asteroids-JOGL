

/**
 *
 * @author karma
 */

public class Bullet extends GameObject {
		
	long spawnTime;
	
	public Bullet() {
		
		spawnTime = 0;
		vX = 0.1f;
		vY = 0.1f;
		visable = false;
		sprite = new Sprite2D(512, 512);
		sprite.setIndex(5);
		sprite.setScale(45.0f);
		sprite.setSize(64, 64);
	}
			
	public void update(int w, int h) {
	
		sprite.position[0] += -vX;
		sprite.position[1] += -vY;
		
		checkBounds(w, h);
	}
	
	private void checkBounds(int w, int h) {
		
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		long duration = System.currentTimeMillis() - spawnTime;
		
		if (duration > 700) {
			
			this.visable = false;
		}
	
		sprite.position[0]= (sprite.position[0] >  halfWidth)  ? sprite.position[0] =   -halfWidth  : sprite.position[0];
		sprite.position[0] = (sprite.position[0] < -halfWidth)  ? sprite.position[0] =   halfWidth  : sprite.position[0];
		sprite.position[1] = (sprite.position[1] >  halfHeight) ? sprite.position[1] =  -halfHeight : sprite.position[1];
		sprite.position[1] = (sprite.position[1] < -halfHeight) ? sprite.position[1] =   halfHeight : sprite.position[1];
	}
}