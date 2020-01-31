

/**
 *
 * @author karma
 */

public class Bullet extends GameObject {
	
	@Override
	public void update(int w, int h) {
	
		posX += vX;
		posY += vY;
		
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