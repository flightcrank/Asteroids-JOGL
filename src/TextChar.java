
public class TextChar extends GameObject {
	
	String fontLayout;
	
	public TextChar() {
		
		this.fontLayout = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
		this.sprite = new Sprite2D(512, 512);
		this.sprite.setScale(22);
	}
}
