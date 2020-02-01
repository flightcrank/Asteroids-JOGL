

/**
 *
 * @author karma
 */

public class Bullet extends GameObject {
	
	public Bullet() {
		
		vX = 0.1f;
		vY = 0.1f;
		scale = 3.0f;
		visable = false;
	}
	
	public static float[] getVerts() {
		
		return new float[] {	 0.0f,  1.0f, 0.0f,
					-1.0f, -1.0f, 0.0f,
					 1.0f, -1.0f, 0.0f};
	}
	
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