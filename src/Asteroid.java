
/**
 *
 * @author karma
 */
enum Size {BIG, MEDIUM, SMALL};

public class Asteroid extends GameObject {

	Size size;
	float rotSpeed;
	Sprite2D sprite;
	
	Asteroid(Size s) {
		
		double r = Math.ceil(Math.random() * 3);
		sprite = new Sprite2D(512, 512);
		sprite.setIndex((int) r + 5); 
		sprite.setSize(128, 128);
		posX = (float) ((Math.random() - .5) * 2) * 1000f;
		posY = (float) ((Math.random() - .5) * 2) * 1000f;
		init(s);
	}
	
	Asteroid(Size s, float x, float y) {
		
		double r = Math.ceil(Math.random() * 3);
		posX = x;
		posY = y;
		sprite = new Sprite2D(512, 512);
		sprite.setIndex((int) r + 5);
		sprite.setSize(128, 128);
		init(s);
	}
	
	private void init(Size s) {
		
		switch (s) {
			
			case BIG:
				scale = 40.0f;
				sprite.setScale(80.0f);
				break;
				
			case MEDIUM:
				scale = 20.0f;
				sprite.setScale(40.0f);
				break;
				
			case SMALL:
				scale = 10.0f;
				sprite.setScale(20.0f);
				break;	
		}
		
		size = s;
		
		vX = (float) ((Math.random() - .5) * 2) * .5f;
		vY = (float) ((Math.random() - .5) * 2) * .5f;
		rotSpeed = (float) ((Math.random() - .5) * 2) * .03f;
	}
	
	@Override
	public void update(int w, int h) {
		
		posX += vX;
		posY += vY;
		rot += rotSpeed;
		checkBounds(w, h);
	}
	
	public void checkBounds(int w, int h) {
		
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		posX = (posX >  halfWidth)  ? posX =  -halfWidth  : posX;
		posX = (posX < -halfWidth)  ? posX =   halfWidth  : posX;
		posY = (posY >  halfHeight) ? posY =  -halfHeight : posY;
		posY = (posY < -halfHeight) ? posY =   halfHeight : posY;
	}
	
	public int checkCollision(GameObject obj) {
		
		double a = posX - obj.posX;
		double b = posY - obj.posY;
		double dist = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));	//distance betweek asteroid and obj
		
		if (dist < sprite.scale[0]) {
						
			return 1;
		}
		
		return 0;
	}
}
