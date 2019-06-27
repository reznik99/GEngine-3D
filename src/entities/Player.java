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

	private static final float GRAVITY = 98.1f;
	private static final float JUMP_POWER = GRAVITY/2;
	private static final float RUN_SPEED = 40;
	private static final float SWIM_SPEED = 20;
	private static final float SHIFT_BOOST = 20;
	private static final float TURN_SPEED = 160;
	private static final float MAX_STAMINA = 15;
	private static final float STAMINA_REGEN = 2;
	static float PLAYER_HEIGHT = 7;

	private float terrainHeight = 0;
	private float currentTurnSpeed = 0;
	private Vector3f speed = new Vector3f(0, 0, 0);
	private boolean inAir = false;
	private boolean underWater = false;
	private float stamina = 10f;
	private boolean sprinting;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale); //model stuff
	}

	public boolean move(Terrain terrain, WaterTile water) {

		underWater = false;
		PLAYER_HEIGHT=7;
		//if head under water
		if(this.getPosition().y + PLAYER_HEIGHT <= water.getHeight()) {
			underWater = true;
		}

		//sets speeds and angles
		this.terrainHeight = terrain.getHeightAt(this.getPosition().x, this.getPosition().z);
		checkInputs();

		//rotation
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(),  0);
		//movement
		super.increasePosition(speed.x, 0, speed.z);

		//fall
		float local_gravity = underWater ? GRAVITY/3 : GRAVITY;
		speed.y -= local_gravity * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, speed.y * DisplayManager.getFrameTimeSeconds(), 0);
		if(!inAir || underWater)speed.scale(0.95f);//Friction

		//collision detection (terrain)
		if(this.getPosition().y<this.terrainHeight) {
			super.getPosition().y = this.terrainHeight;
			speed.y = 0;
			inAir = false;
		}

		return underWater;
	}

	private void jump() {
		if(!inAir) {
			speed.y = Player.JUMP_POWER;
			inAir = true;
		}else if(underWater) {
			speed.y += GRAVITY/2 / 60;
		}
	}

	private void checkInputs() {
		float max_speed = underWater ? SWIM_SPEED : RUN_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && stamina > 0) {
			max_speed += SHIFT_BOOST;
			stamina -= DisplayManager.getFrameTimeSeconds() * 10;
			sprinting = true;
		}else {
			if(stamina < MAX_STAMINA)stamina += DisplayManager.getFrameTimeSeconds() * STAMINA_REGEN;
			sprinting = false;
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

	public boolean getUnderwater() {
		return underWater;
	}
	
	public boolean getSprinting() {
		return sprinting;
	}

}
