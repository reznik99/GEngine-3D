package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import audio.AudioManager;
import audio.Source;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Shark;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class Driver {

	public static void main(String[] args){

		DisplayManager.createDisplay();

		Loader loader = new Loader(); //loads up textures and models in VBOs to VAOs

		// *********TERRAIN TEXTURE STUFF***********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("/terrain/mud"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("/terrain/rock"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("/terrain/grass"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("/terrain/sand"));

		backgroundTexture.setShineDamper(1);
		backgroundTexture.setReflectivity(5);
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("/terrain/blendMap_test"));
		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "/terrain/heightmap2_scaled");
		WaterTile water = new WaterTile(400, 400, 0f);//bigger than terrain to give island look
		
		List<Entity> entities = new ArrayList<Entity>();

		/* LOAD Models and Textures */
		TexturedModel treeModel = new TexturedModel(OBJLoader.loadObjModel("/objects/tree", loader), new ModelTexture(loader.loadTexture("/objects/tree")));
		TexturedModel palmModel = new TexturedModel(OBJLoader.loadObjModel("/objects/Palm2LowPoly", loader), new ModelTexture(loader.loadTexture("/objects/Palm2")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("/objects/grassModel2", loader), new ModelTexture(loader.loadTexture("/objects/grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("/objects/grassModel2", loader), new ModelTexture(loader.loadTexture("/objects/flower")));
		TexturedModel rockModel = new TexturedModel(OBJLoader.loadObjModel("/objects/rock", loader), new ModelTexture(loader.loadTexture("/objects/metal")));
		TexturedModel towerModel = new TexturedModel(OBJLoader.loadObjModel("/objects/tower1", loader), new ModelTexture(loader.loadTexture("/objects/tower1")));
		TexturedModel bonfireModel = new TexturedModel(OBJLoader.loadObjModel("/objects/bonfire", loader), new ModelTexture(loader.loadTexture("/objects/bonfire")));
		
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("/objects/Chogall", loader), new ModelTexture(loader.loadTexture("/objects/Chogall")));
		
		TexturedModel sharkModel = new TexturedModel(OBJLoader.loadObjModel("/objects/shark", loader), new ModelTexture(loader.loadTexture("/objects/shark")));
		/* Randomized Entities */
		Random rand = new Random();
		for(int i=0; i<500; i++) {
			float x = rand.nextFloat()*Terrain.SIZE;
			float z = rand.nextFloat()*Terrain.SIZE;
			float y = terrain.getHeightAt(x, z);
			float scale = rand.nextFloat()*1f + 3f;
			while(y<water.getHeight()) {
				x = rand.nextFloat()*Terrain.SIZE;z = rand.nextFloat()*Terrain.SIZE;
				y = terrain.getHeightAt(x, z);
			}
			Vector3f position = new Vector3f(x , y, z);
			TexturedModel model = y>water.getHeight()+25 ? treeModel : palmModel;
			if(i<250) {
				if(y>water.getHeight()+20) continue;
				model = rand.nextFloat()>0.5 ? grass : rand.nextFloat()>0.5 ? rockModel : flower;
				scale = rand.nextFloat()*1f + 1f;
			}
			if(model == palmModel) scale = rand.nextFloat()*2f + 1f;
			Entity entity = new Entity(model, position, 0, rand.nextFloat()*360, rand.nextFloat()*10 - 5, scale);
			entities.add(entity);
		}
		
		for(int i=0; i<25; i++) {
			float x = rand.nextFloat()*Terrain.SIZE;
			float z = rand.nextFloat()*Terrain.SIZE;
			float waterDepth = water.getHeight()-terrain.getHeightAt(x, z);
			while(waterDepth < Shark.SHALLOW_WATER_LIMIT) {
				x = rand.nextFloat()*Terrain.SIZE;
				z = rand.nextFloat()*Terrain.SIZE;
				waterDepth = water.getHeight()-terrain.getHeightAt(x, z);
			}
			float y = terrain.getHeightAt(x, z) + rand.nextFloat()*(waterDepth-3);
			Shark sharkEntity = new Shark(sharkModel, new Vector3f(x, y, z),0,rand.nextFloat()*360,0,5f);
			entities.add(sharkEntity);
		}
		
		/* HARDCODED ENTITIES */
		Entity towerEntity = new Entity(towerModel, new Vector3f(65, terrain.getHeightAt(65, 75)-10, 75), 0,0,0,8f);
		Entity towerEntity2 = new Entity(towerModel, new Vector3f(650, terrain.getHeightAt(650, 300)-10, 300), 0,0,0,8f);
		Entity bonfireEntity = new Entity(bonfireModel, new Vector3f(223,terrain.getHeightAt(223, 198),198),0,90,0,2.5f);
		entities.add(bonfireEntity);
		entities.add(towerEntity);
		entities.add(towerEntity2);
		
		//player
		Entity playerEntity = new Player(playerModel, new Vector3f(Terrain.SIZE/2,terrain.getHeightAt(Terrain.SIZE/2, Terrain.SIZE/2)+10,Terrain.SIZE/2),0,0,0,3f);
		if(!Camera.FIRST_PERSON)//dont render player if firstPerson
			entities.add(playerEntity);
		
		//camera and light
		//Light light = new Light(new Vector3f(223,terrain.getHeightAt(223, 198)+15,198), new Vector3f(1,0.85f,0.55f));
		Light light = new Light(new Vector3f(300000, 8000000, 7000000), new Vector3f(0.75f, 0.7f, 0.6f));
		Camera camera = new Camera((Player) playerEntity);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		
		//audio 
		AudioManager.init();
		int bufferFire = AudioManager.loadSound("audio/bonfire.wav");
		int bufferAmbient = AudioManager.loadSound("audio/islandAmbient.wav");
		int bufferBreathing = AudioManager.loadSound("audio/breathing.wav");
		int bufferSigh = AudioManager.loadSound("audio/sigh.wav");
		int bufferSwimming = AudioManager.loadSound("audio/swimming.wav");
		Source playerSource = new Source();
		playerSource.setVolume(1);
		playerSource.setPosition(playerEntity.getPosition());
		Source playerSource2 = new Source();
		playerSource2.setVolume(1);
		playerSource2.setPosition(playerEntity.getPosition());
		Source bonFireSource = new Source();
		bonFireSource.setVolume(3);
		bonFireSource.setLooping(true);
		bonFireSource.setPosition(bonfireEntity.getPosition());
		bonFireSource.play(bufferFire);
		Source ambientSource1 = new Source();
		ambientSource1.setVolume(3);
		ambientSource1.setLooping(true);
		ambientSource1.setPosition(new Vector3f(300, 25, 100));
		ambientSource1.play(bufferAmbient);
		Source ambientSource2 = new Source();
		ambientSource2.setVolume(3);
		ambientSource2.setLooping(true);
		ambientSource2.setPosition(new Vector3f(600, 25, 600));
		ambientSource2.play(bufferAmbient);
		Source ambientSource3 = new Source();
		ambientSource3.setVolume(3);
		ambientSource3.setLooping(true);
		ambientSource3.setPosition(new Vector3f(150, 25, 500));
		ambientSource3.play(bufferAmbient);
		
		//water stuff
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		Vector4f reflectionClipPlane = new Vector4f(0, 1, 0, -water.getHeight());
		Vector4f refractionClipPlane = new Vector4f(0, -1, 0, water.getHeight());
		
		//game loop
		while(!Display.isCloseRequested()){
			renderOptionsListener();
			//update player, camera and audio sources
			Player player = (Player)playerEntity;
			player.move(terrain, water);
			camera.move();
			updateAudio(camera, water, playerSource, playerSource2, bufferBreathing, bufferSigh, bufferSwimming);
			//Load Entities and terrain for rendering
			for(Entity e : entities) {
				renderer.processEntity(e);
				if(e instanceof Shark)
					((Shark) e).update(terrain, water, player);
			}
			renderer.processTerrain(terrain);
			
			/* SHADOW MAP */
			renderer.renderShadowMap(light);
			/* REFLECTION */
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.setPitch(-camera.getPitch());
			renderer.render(light, camera, reflectionClipPlane, false);
			camera.getPosition().y += distance;
			camera.setPitch(-camera.getPitch());
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();

			/* REFRACTION */
			GL11.glEnable(GL30.GL_CLIP_DISTANCE1);
			fbos.bindRefractionFrameBuffer();
			renderer.render(light, camera, refractionClipPlane, false);
			GL11.glDisable(GL30.GL_CLIP_DISTANCE1);
			fbos.unbindCurrentFrameBuffer();

			//render terrain, entities and water. Clears buffers.
			renderer.render(light, camera, reflectionClipPlane, true);
			waterRenderer.render(water, camera);
			//update
			DisplayManager.updateDisplay();
		}
		
		//clean up
		cleanUp(bonFireSource, waterShader, loader, renderer);
	}
	
	/**
	 * Render scene with different options:
	 * ex. Wireframe
	 */
	private static void renderOptionsListener() {
		if(Keyboard.isKeyDown(Keyboard.KEY_X)) {
			GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
		}else {
			GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
		}
	}
	/**
	 * Clean up before exit
	 * @param bonFireSource
	 * @param waterShader
	 * @param loader
	 * @param renderer
	 */
	private static void cleanUp(Source bonFireSource, WaterShader waterShader, Loader loader, MasterRenderer renderer) {
		bonFireSource.delete();
		waterShader.cleanUp();
		loader.cleanUp();
		renderer.cleanUp();
		DisplayManager.closeDisplay();
	}
	/**
	 * Updates audio listener and sources (dynamic sources only)
	 * @param camera
	 * @param water
	 * @param playerSource
	 * @param playerSource2
	 * @param bufferBreathing
	 * @param bufferSigh
	 * @param bufferSwimming
	 */
	private static void updateAudio(Camera camera, WaterTile water, Source playerSource, Source playerSource2, int bufferBreathing, int bufferSigh, int bufferSwimming) {
		Player player = camera.getPlayer();
		Vector3f playerPos = player.getPosition();
		//update source positions
		playerSource.setPosition(playerPos);
		playerSource2.setPosition(playerPos);
		//update orientation
		AudioManager.setListenerData(camera, player);
		
		if(player.getStamina() <=0 && !playerSource2.isPLaying())
			playerSource2.play(bufferSigh);
				
		/*if(player.getSprinting()) {
			if(!playerSource.isPLaying())
				playerSource.play(bufferBreathing);
		}else*/ if(playerPos.y <= water.getHeight()) {
			if(!playerSource.isPLaying())
				playerSource.play(bufferSwimming);
		}else
			playerSource.stop();
	}

}
