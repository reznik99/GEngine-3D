package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import water.WaterTile;

/** Player entity
 * 
 * @author Francesco
 */
public class Player extends Entity{

	private static final float GRAVITY = 9.81f * 2;
	private static final float JUMP_POWER = GRAVITY * 0.7f;
	private static final float WALK_SPEED = 7.3f;
	private static final float SWIM_SPEED = 5f;
	private static final float RUNNING_SPEED = 9.2f;
	private static final float TURN_SPEED = 160;
	private static final float MAX_STAMINA = 250;
	private static final float STAMINA_REGEN = 50;
	private static final float STAMINA_CONSUM_RATE = 50;
	public static float PLAYER_HEIGHT = 4;
	private static final float FALL_MULTIPLIER = 2f;
	private static final float LOW_JUMP_MULTIPLIER = 1f;

	private float terrainHeight = 0;
	private float currentTurnSpeed = 0;
	private Vector3f speed = new Vector3f(0, 0, 0);
	private boolean inAir = false;
	private boolean underWater = false;
	private float stamina = MAX_STAMINA;
	private boolean sprinting;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale); //model stuff
	}

	public boolean move(Terrain terrain, WaterTile water) {
	
		checkAndSetPlayerState(water);
		this.terrainHeight = terrain.getHeightAt(this.getPosition().x, this.getPosition().z);
		checkInputs();

		/*Update player position and rotation*/
		float local_gravity = GRAVITY * (speed.y > 0 && Keyboard.isKeyDown(Keyboard.KEY_SPACE) 
				? LOW_JUMP_MULTIPLIER : FALL_MULTIPLIER); //make jump feel crisp
		if(underWater) local_gravity = GRAVITY/3; //fake buoyancy
		speed.y -= local_gravity * DisplayManager.getFrameTimeSeconds();
		//apply update
		super.increasePosition(speed.x, speed.y * DisplayManager.getFrameTimeSeconds(), speed.z);
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(),  0);
		
		if(!inAir || underWater) {//Friction on ground
			speed.x *=0.85f;
			speed.z *=0.85f;
		}
		collideWithGround();

		return underWater;
	}

	/**
	 * Checks if collide with ground and handles it. 
	 * (reset y speed. set boolean inAir to false)
	 */
	private void collideWithGround() {
		//collision detection (terrain)
		if(super.getPosition().y<this.terrainHeight) {
			super.getPosition().y = this.terrainHeight;
			speed.y = 0;
			inAir = false;
		}
	}
	
	/**
	 * Jump the player
	 */
	private void jump() {
		if(!inAir) {
			speed.y = Player.JUMP_POWER;
			inAir = true;
		}else if(underWater) //swim
			speed.y += GRAVITY/2 * DisplayManager.getFrameTimeSeconds();
	}

	/**
	 * Sets variables such as speed and rotation speed
	 * depending on input, also calls jump method.
	 */
	private void checkInputs() {
		float max_speed = underWater ? SWIM_SPEED : WALK_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && stamina > 0) {
			max_speed += RUNNING_SPEED;
			stamina -= DisplayManager.getFrameTimeSeconds() * STAMINA_CONSUM_RATE;
			sprinting = true;
		}else {
			sprinting = false;
			if(stamina < MAX_STAMINA)stamina += DisplayManager.getFrameTimeSeconds() * STAMINA_REGEN;
		}
		float dx = (float) ((max_speed * DisplayManager.getFrameTimeSeconds())
				* Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) ((max_speed * DisplayManager.getFrameTimeSeconds())
				* Math.cos(Math.toRadians(super.getRotY())));

		//movement
		if(!inAir || underWater) { //can only move if in water OR if on land NOT while jumping
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
				speed.x = dx;
				speed.z = dz;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
				speed.x = -dx;
				speed.z = -dz;
			}
		}

		//jump
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			this.jump();

		//rotation
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if(Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else
			this.currentTurnSpeed = 0;
	}

	/**
	 * check/set new player state
	 * @param water
	 */
	private void checkAndSetPlayerState(WaterTile water) {
		if(this.getPosition().y + PLAYER_HEIGHT <= water.getHeight())
			underWater = true;
		else 
			underWater = false;
				
		if(this.getPosition().y > terrainHeight)
			inAir = true;
		else
			inAir = false;
	}
	
	
	public boolean getUnderwater() {
		return underWater;
	}
	public boolean getSprinting() {
		return sprinting;
	}
	public float getStamina() {
		return stamina;
	}
}
