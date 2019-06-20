package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{

	private static final float GRAVITY = 100;
	private static final float JUMP_POWER = 30;
	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	static final float PLAYER_HEIGHT = 5;
	
	private float terrainHeight = 0;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean inAir = false;
	private boolean strafe = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Terrain terrain) {
		//sets speeds and angles
		this.terrainHeight = terrain.getHeightAt(this.getPosition().x, this.getPosition().z);
		checkInputs();
		//if both mouse pressed, player should move forward
		if(strafe)
			currentSpeed = RUN_SPEED;
		//System.out.println(this.getPosition().x + " " + this.getPosition().z);
		//movement
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(),  0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		
		//jumping
		upwardsSpeed -= GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		if(this.getPosition().y<this.terrainHeight) {
			super.getPosition().y = this.terrainHeight;
			inAir = false;
			this.upwardsSpeed = 0;
		}
		strafe = false;
	}
	
	private void jump() {
		if(!inAir)
			this.upwardsSpeed = Player.JUMP_POWER;
	}
	
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			this.currentSpeed = RUN_SPEED;
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
			this.currentSpeed = -RUN_SPEED;
		else
			this.currentSpeed = 0;
		
		if(!strafe) {
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				this.currentTurnSpeed = -TURN_SPEED;
			else if(Keyboard.isKeyDown(Keyboard.KEY_A))
				this.currentTurnSpeed = TURN_SPEED;
			else
				this.currentTurnSpeed = 0;
		}else {
			//strafe left or right
			this.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			this.jump();
	}
	
	public void setStrafe(boolean val) {
		this.strafe = val;
	}

}
