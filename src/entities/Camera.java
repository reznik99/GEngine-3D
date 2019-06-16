package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private Vector3f position = new Vector3f(0,5,0);
	private float pitch;
	private float yaw = 180;
	private float roll;
	private float moveSpeed = 4f;
	private float lookSpeed = 0.6f;
	
	public void move() {
//		yaw =  - (Display.getWidth() - Mouse.getX() / 2);
//		pitch =  (Display.getHeight() / 2) - Mouse.getY();
//		
//		if (pitch >= 90)
//			pitch = 90;
//		else if (pitch <= -90)
//			pitch = -90;
		
		float x1 = (float) (Math.sin(Math.toRadians(yaw)) / moveSpeed);
		float y1 = (float) (Math.cos(Math.toRadians(yaw)) / moveSpeed);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.x += x1;position.z -= y1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += y1;position.z += x1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= y1;position.z -= x1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.x -= x1;position.z += y1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_Q))
			yaw-=lookSpeed;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_E))
			yaw+=lookSpeed;
		
	}
	
	
	
	public Vector3f getPosition() {
		return position;
	}
	public float getPitch() {
		return pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public float getRoll() {
		return roll;
	}

}
