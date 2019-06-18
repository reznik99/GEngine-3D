package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0,10,0);
	private float pitch;
	private float yaw = 180;
	private float roll;
	private float moveSpeed = 4f;
	private float lookSpeed = 0.6f;
	//player stuff
	private float distanceFromPlayer = 50;
	private float targetDistanceFromPlayer = distanceFromPlayer;
	private float angleAroundPlayer = 0;
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		calculateCamera();
	}
	
	private void calculateCamera() {
		float horizontalDistance = this.distanceFromPlayer * (float) Math.cos(Math.toRadians(this.pitch));
		float verticalDistance = this.distanceFromPlayer * (float) Math.sin(Math.toRadians(this.pitch));
		
		float theta = player.getRotY() + this.angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		this.position.y = player.getPosition().y + verticalDistance + Player.PLAYER_HEIGHT;
		this.position.x = player.getPosition().x - offsetX;
		this.position.z = player.getPosition().z - offsetZ;
	
		this.yaw = 180 - theta;
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel()/10;
		if(zoomLevel!=0)
			this.targetDistanceFromPlayer = this.distanceFromPlayer - zoomLevel;
		this.distanceFromPlayer = lerp(distanceFromPlayer, targetDistanceFromPlayer, 0.05f); //distanceFromPlayer + 0.01f * (targetDistanceFromPlayer - distanceFromPlayer);
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(0)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			this.pitch -= pitchChange;
		}
	}
	
	private float lerp(float v0, float v1, float t) {
		  return v0 + t * (v1 - v0);
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			this.angleAroundPlayer -= angleChange;
		}
	}
	
	
	public float getDistanceFromPlayer() {
		return distanceFromPlayer;
	}
	public float getMoveSpeed() {
		return moveSpeed;
	}
	public float getLookSpeed() {
		return lookSpeed;
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
