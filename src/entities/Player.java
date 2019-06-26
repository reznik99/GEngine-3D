package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import water.WaterTile;

public class Player extends Entity{

	private static final float GRAVITY = 50;
	private static final float JUMP_POWER = 30;
	private static final float RUN_SPEED = 40;
	private static final float SWIM_SPEED = 20;
	private static final float TURN_SPEED = 160;
	static final float PLAYER_HEIGHT = 7;

	private float terrainHeight = 0;
	private float currentTurnSpeed = 0;
	private static Vector3f speed = new Vector3f(0, 0, 0);
	private boolean inAir = false;
	private boolean inWater = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public boolean move(Terrain terrain, WaterTile water) {
		
		inWater = false;
		//if head under water
		if(this.getPosition().y + PLAYER_HEIGHT <= water.getHeight()) 
			inWater = true;

		//sets speeds and angles
		this.terrainHeight = terrain.getHeightAt(this.getPosition().x, this.getPosition().z);
		checkInputs();

		//rotation
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(),  0);
		//movement
		super.increasePosition(speed.x, 0, speed.z);

		//fall
		float local_gravity = inWater ? GRAVITY/2 : GRAVITY;
		speed.y -= local_gravity * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, speed.y * DisplayManager.getFrameTimeSeconds(), 0);
		if(inWater) speed.y*=0.95;
		
		//collision detection (terrain)
		if(this.getPosition().y<this.terrainHeight) {
			super.getPosition().y = this.terrainHeight;
			inAir = false;
			speed.y = 0;
		}

		return inWater;
	}

	private void jump() {
		if(!inAir || inWater) {
			this.speed.y = Player.JUMP_POWER;
			inAir = true;
		}
	}

	private void checkInputs() {
		float max_speed = inWater ? SWIM_SPEED : RUN_SPEED;
		float dx = (float) ((max_speed * DisplayManager.getFrameTimeSeconds())
				* Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) ((max_speed * DisplayManager.getFrameTimeSeconds())
				* Math.cos(Math.toRadians(super.getRotY())));
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			speed.x = dx;
			speed.z = dz;
		}
			
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			speed.x = dx;
			speed.z = dz;
		}
		else {
			speed.x = 0;
			speed.z = 0;
		}


		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if(Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else
			this.currentTurnSpeed = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			this.jump();
	}

}
