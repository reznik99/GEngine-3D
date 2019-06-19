package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Light {

	private Vector3f position;
	private Vector3f colour;
	private float speed = .1f;
	
	public Light(Vector3f position, Vector3f colour) {
		super();
		this.position = position;
		this.colour = colour;
	}
	
	public void update() {
		if(this.position.y <= -100) {
			speed = -speed;
			this.position.y = -100;
		}
		if(this.position.y >= 300) {
			speed = -speed;
			this.position.y = 300;
		}
		float distance = speed;
		this.position.y-= distance;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColor(Vector3f color) {
		this.colour = color;
	}
}
