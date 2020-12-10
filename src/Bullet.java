

/**
 *
 * @author karma
 */

public class Bullet extends GameObject {
	
	Sprite2D sprite;
	
	public Bullet() {
		
		vX = 0.1f;
		vY = 0.1f;
		scale = 45.0f;
		visable = false;
		sprite = new Sprite2D(512, 512);
		sprite.setIndex(5);
		sprite.setScale(scale);
		sprite.setSize(64, 64);
	}
		
	@Override
	public void update(int w, int h) {
	
		posX += -vX;
		posY += -vY;
		
		checkBounds(w, h);
	}
	
	private void checkBounds(int w, int h) {
		
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		this.visable = (posX >  halfWidth) ? false : this.visable;
		this.visable = (posX < -halfWidth) ? false : this.visable;
		this.visable = (posY >  halfHeight) ? false : this.visable;
		this.visable = (posY < -halfHeight) ? false : this.visable;
	}
}