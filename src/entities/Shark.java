package entities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import water.WaterTile;

public class Shark extends Entity{
	
	public static final float SHALLOW_WATER_LIMIT = 8f;
	public static final float IDLE_SPEED = 10f;
	public static final float CHASE_SPEED = 25f;
	public static final float PATROL_RANGE = 100f;
	
	private Vector3f target;
	
	private Random rand = new Random();

	public Shark(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void update(Terrain terrain, WaterTile water, Player player) {
		if(target==null || target.y>=water.getHeight()) {
			findTarget(terrain, water);
		}else {
			goToTarget(player);
		}
	}
	
	private void goToTarget(Player player) {
		Vector3f diffVec = new Vector3f();
		Vector3f.sub(this.target, super.getPosition(), diffVec);
		
		float localSpeed = IDLE_SPEED * DisplayManager.getFrameTimeSeconds();
		
		//player targeting
		if(player.getUnderwater()) {
			Vector3f distToPlayerVec = new Vector3f();
			Vector3f.sub(player.getPosition(), super.getPosition(), distToPlayerVec);
			
			//if chasing player
			if(distToPlayerVec.length() < PATROL_RANGE) {
				this.target = player.getPosition();
				localSpeed = CHASE_SPEED * DisplayManager.getFrameTimeSeconds();
			}
		}
		
		//patrolling
		if(diffVec.length()<1) {
			this.target = null;
		}else {
			
			diffVec.normalise();
			diffVec.set(diffVec.x * localSpeed, diffVec.y * localSpeed, diffVec.z * localSpeed);
			//rotate
			float val = lerp(super.getRotY(), ((float)(Math.atan2(diffVec.x, diffVec.z)*180/Math.PI)), 0.05f);
			super.setRotY(val);
			//move
			Vector3f.add(super.getPosition(), diffVec, super.getPosition());
			
		}
	}
	
	private void findTarget(Terrain terrain, WaterTile water) {
		float x = this.getPosition().x + (rand.nextFloat() * PATROL_RANGE) - (PATROL_RANGE/2);
		float z = this.getPosition().z + (rand.nextFloat() * PATROL_RANGE) - (PATROL_RANGE/2);
		float waterDepth = water.getHeight()-terrain.getHeightAt(x, z);
		//has to be below water, and within map bounds
		while(waterDepth < Shark.SHALLOW_WATER_LIMIT || x<0 || z<0 || x>=Terrain.SIZE || z>=Terrain.SIZE) {
			x = this.getPosition().x + (rand.nextFloat() * PATROL_RANGE) - (PATROL_RANGE/2);
			z = this.getPosition().z + (rand.nextFloat() * PATROL_RANGE) - (PATROL_RANGE/2);
			waterDepth = water.getHeight()-terrain.getHeightAt(x, z);
		}
		float y = terrain.getHeightAt(x, z) + rand.nextFloat()*(waterDepth-3);
		
		this.target = new Vector3f(x, y, z);
	}
	
	private float lerp(float v0, float v1, float t) {
		  return v0 + t * (v1 - v0);
	}

}
