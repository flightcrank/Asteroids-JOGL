
/**
 *
 * @author karma
 */
enum Size {BIG, MEDIUM, SMALL};

public class Asteroid extends GameObject{

	Size size;
	float rotSpeed;
	
	Asteroid(Size s) {
		
		init(s);
	}
	
	Asteroid(Size s, float x, float y) {
		
		init(s);
		posX = x;
		posY = y;
	}
	
	private void init(Size s) {
		
		switch (s) {
			
			case BIG:
				scale = 40.0f;
				break;
				
			case MEDIUM:
				scale = 20.0f;
				break;
				
			case SMALL:
				scale = 10.0f;
				break;	
		}
		
		size = s;
		posX = (float) ((Math.random() - .5) * 2) * 1000f;
		posX = (float) ((Math.random() - .5) * 2) * 1000f;
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
	
	public static float[] getVerts() {
		
		return new float[] {0.000000f, 0.000000f, 0.000000f,
					0.031671f, 0.792114f, 0.000000f,
					-0.549259f, 0.908211f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.549259f, 0.908211f, 0.000000f,
					-0.762112f, 0.608472f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.549259f, 0.908211f, 0.000000f,
					-0.762112f, 0.608472f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.415905f, 0.829636f, 0.000000f,
					0.031671f, 0.792114f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.671873f, 0.464742f, 0.000000f,
					0.415905f, 0.829636f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.671873f, 0.464742f, 0.000000f,
					0.780286f, -0.080520f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.780286f, -0.080520f, 0.000000f,
					0.541467f, -0.518112f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.541467f, -0.518112f, 0.000000f,
					0.109574f, -0.710462f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					0.109574f, -0.710462f, 0.000000f,
					-0.193098f, -0.412903f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.193098f, -0.412903f, 0.000000f,
					-0.566910f, -0.379114f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.566910f, -0.379114f, 0.000000f,
					-0.711617f, -0.125416f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.711617f, -0.125416f, 0.000000f,
					-0.539769f, 0.120092f, 0.000000f,
					0.000000f, 0.000000f, 0.000000f,
					-0.762112f, 0.608472f, 0.000000f,
					-0.539769f, 0.120092f, 0.000000f};
	}
}
