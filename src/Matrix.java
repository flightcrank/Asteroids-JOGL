
class Matrix {
		
	static final int X = 0, Y = 1, Z = 2;
	
	public static float[] transpose3D(float[] m) {
		
		
		return m;
	}
	
	public static float[] rot2D(float angle) {
		
		float[] m = {
			
			1, 0,
			0, 1
		};

		m[0] =  (float) Math.cos(angle); 
		m[1] = -(float) Math.sin(angle);
		m[2] =  (float) Math.sin(angle);
		m[3] =  (float) Math.cos(angle);

		return  m;
	}
	
	public static float[] rot3D(float angle, int axis) {
		
		float[] m = {
			
			1, 0, 0,
			0, 1, 0,
			0, 0, 1
		};
		
		switch (axis) {
			
			case X:
				
				m[4] =  (float) Math.cos(angle); 
				m[5] = -(float) Math.sin(angle);
				m[7] =  (float) Math.sin(angle);
				m[8] =  (float) Math.cos(angle);
				break;
			
			case Y:
			
				m[0] =  (float) Math.cos(angle); 
				m[2] =  (float) Math.sin(angle);
				m[6] = -(float) Math.sin(angle);
				m[8] =  (float) Math.cos(angle);
				break;
		
			case Z:
			
				m[0] =  (float) Math.cos(angle); 
				m[1] = -(float) Math.sin(angle);
				m[3] =  (float) Math.sin(angle);
				m[4] =  (float) Math.cos(angle);
				break;
		}

		return  m;
	}
	
	public static float[] rot4D(float angle, int axis) {
		
		float[] m = {
			
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1,
		};
		
		switch (axis) {
			
			case X:
				
				m[5] =  (float) Math.cos(angle); 
				m[6] = -(float) Math.sin(angle);
				m[9] =  (float) Math.sin(angle);
				m[10] =  (float) Math.cos(angle);
				break;
			
			case Y:
			
				m[0] =  (float) Math.cos(angle); 
				m[2] =  (float) Math.sin(angle);
				m[8] = -(float) Math.sin(angle);
				m[10] =  (float) Math.cos(angle);
				break;
		
			case Z:
			
				m[0] =  (float) Math.cos(angle); 
				m[1] = -(float) Math.sin(angle);
				m[3] =  (float) Math.sin(angle);
				m[4] =  (float) Math.cos(angle);
				break;
		}

		return  m;
	}
	
	
	public static float[] scale3D(float x, float y, float z) {
		
		float[] m = {
			
			1, 0, 0,
			0, 1, 0,
			0, 0, 1
		};
		
		m[0] = x;
		m[4] = y;
		m[8] = z;
		
		return m;		
	}
	
	public static float[] perspective(float scale, float zNear, float zFar) {
		
		float[] m = {
			
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		};
		
		
		m[0] = scale;
		m[5] = scale;
		m[10] = -zFar  / (zFar - zNear);
		m[11] = -1;
		m[14] = -zFar * zNear / (zFar - zNear);
		
		return m;
	}
	
	public static float[] orthographic(float xLeft, float xRight, float yBottom, float yTop, float zNear, float zFar) {
		
		float[] m = {
			
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		};
		
		
		m[0] = 2 / (xRight - xLeft);
		m[5] = 2 / (yBottom - yTop );
		m[10] = -2 / (zFar - zNear);
		m[12] = (xRight + xLeft) / (xLeft - xRight);
		m[13] = (yTop + yBottom) / (yTop - yBottom);
		m[14] = (zFar + zNear) / (zFar - zNear);
		m[15] = 1;
		
		return m;
	}

}
