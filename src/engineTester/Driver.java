package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("/terrain/grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("/terrain/mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("/terrain/grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("/terrain/rock"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("/terrain/blendMap"));
		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "/terrain/heightmap");
		WaterTile water = new WaterTile(400, 400, 0f);//bigger than terrain to give island look
		
		List<Entity> entities = new ArrayList<Entity>();

		/* LOAD Models and Textures */
		TexturedModel treeModel = new TexturedModel(OBJLoader.loadObjModel("/objects/pine", loader), new ModelTexture(loader.loadTexture("/objects/pine")));
		TexturedModel palmModel = new TexturedModel(OBJLoader.loadObjModel("/objects/Palm2LowPoly", loader), new ModelTexture(loader.loadTexture("/objects/Palm2")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("/objects/grassModel2", loader), new ModelTexture(loader.loadTexture("/objects/grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("/objects/grassModel2", loader), new ModelTexture(loader.loadTexture("/objects/flower")));
		TexturedModel rock = new TexturedModel(OBJLoader.loadObjModel("/objects/rock", loader), new ModelTexture(loader.loadTexture("/objects/metal")));
		TexturedModel tower = new TexturedModel(OBJLoader.loadObjModel("/objects/tower1", loader), new ModelTexture(loader.loadTexture("/objects/tower1")));
		TexturedModel building = new TexturedModel(OBJLoader.loadObjModel("/objects/building1", loader), new ModelTexture(loader.loadTexture("/objects/metal")));
		TexturedModel statueModel = new TexturedModel(OBJLoader.loadObjModel("/objects/alliance_statue", loader), new ModelTexture(loader.loadTexture("/objects/alliance_statue")));
		statueModel.getTexture().setReflectivity(1);
		statueModel.getTexture().setShineDamper(5);
		TexturedModel statue2Model = new TexturedModel(OBJLoader.loadObjModel("/objects/lotharStatue", loader), new ModelTexture(loader.loadTexture("/objects/lotharStatue1")));
		statue2Model.getTexture().setReflectivity(0.2f);
		statue2Model.getTexture().setShineDamper(5);
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("/objects/Chogall", loader), new ModelTexture(loader.loadTexture("/objects/Chogall")));
		playerModel.getTexture().setReflectivity(0.2f);
		playerModel.getTexture().setShineDamper(5);
		TexturedModel bonfireModel = new TexturedModel(OBJLoader.loadObjModel("/objects/bonfire", loader), new ModelTexture(loader.loadTexture("/objects/bonfire")));
		TexturedModel fallenTreeModel = new TexturedModel(OBJLoader.loadObjModel("/objects/fallenRedridgeTree", loader), new ModelTexture(loader.loadTexture("/objects/fallenRedridgeTree")));
		
		//generate Random Entities
		Random rand = new Random();
		for(int i=0; i<1000; i++) {
			float x = rand.nextFloat()*Terrain.SIZE;
			float z = rand.nextFloat()*Terrain.SIZE;
			float y = terrain.getHeightAt(x, z);
			float scale = rand.nextFloat()*1f + 3f;
			while(y<water.getHeight()) {
				x = rand.nextFloat()*Terrain.SIZE;
				z = rand.nextFloat()*Terrain.SIZE;
				y = terrain.getHeightAt(x, z);
			}
			Vector3f position = new Vector3f(x , y, z);
			TexturedModel model = y>water.getHeight()+25 ? treeModel : palmModel;
			if(i<750) {
				if(y>water.getHeight()+20) continue;
				model = rand.nextFloat()>0.5 ? grass : rand.nextFloat()>0.5 ? rock : flower;
				scale = rand.nextFloat()*1f + 1f;
			}
			if(model == palmModel) scale = rand.nextFloat()*2f + 1f;
			Entity entity = new Entity(model, position, 0, rand.nextFloat()*360, 0, scale);
			
			entities.add(entity);
		}
		
		//hardCoded Entities
		Entity buildingEntity = new Entity(building, new Vector3f(125, terrain.getHeightAt(125, 205), 205), 0,180,0,0.25f);
		Entity towerEntity = new Entity(tower, new Vector3f(65, terrain.getHeightAt(65, 75), 75), 0,0,0,8f);
		Entity towerEntity2 = new Entity(tower, new Vector3f(650, terrain.getHeightAt(650, 300), 300), 0,0,0,8f);
		Entity statueEntity = new Entity(statueModel, new Vector3f(465,terrain.getHeightAt(465, 270),270),0,90,0,0.2f);
		Entity bonfireEntity = new Entity(bonfireModel, new Vector3f(223,terrain.getHeightAt(223, 198),198),0,90,0,2.5f);
		Entity statueEntity3 = new Entity(statue2Model, new Vector3f(291,terrain.getHeightAt(291, 463)-5,463),0,90,0,2f);
		Entity fallenTreeEntity = new Entity(fallenTreeModel, new Vector3f(341,terrain.getHeightAt(341, 240),240),0,0,0,5f);

		entities.add(fallenTreeEntity);
		entities.add(statueEntity3);
		entities.add(bonfireEntity);
		entities.add(towerEntity2);
		entities.add(statueEntity);
		entities.add(buildingEntity);
		entities.add(towerEntity);
		
		//player
		Entity playerEntity = new Player(playerModel, new Vector3f(220,terrain.getHeightAt(223, 198)+10,198),0,0,0,3f);
		if(!Camera.FIRST_PERSON)//dont render player if firstPerson
			entities.add(playerEntity);
		
		//camera and light
		Light light = new Light(new Vector3f(223,terrain.getHeightAt(223, 198)+15,198), new Vector3f(1,0.85f,0.55f));
		Camera camera = new Camera((Player) playerEntity);
		MasterRenderer renderer = new MasterRenderer(loader);
		
		//audio 
		AudioManager.init();
		int bufferFire = AudioManager.loadSound("audio/bonfire.wav");
		int bufferAmbient = AudioManager.loadSound("audio/islandAmbient.wav");
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
			//update entities
			AudioManager.setListenerData(camera, (Player)playerEntity);
			camera.move();
			boolean underwater = ((Player)playerEntity).move(terrain, water);
			//light.update();
			
			//Load Entities and terrain for rendering
			for(Entity e : entities)
				renderer.processEntity(e);
			renderer.processTerrain(terrain);
			
			/* REFLECTION */
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.setPitch(-camera.getPitch());
			
			renderer.render(light, camera, reflectionClipPlane, false, false);
			
			camera.getPosition().y += distance;
			camera.setPitch(-camera.getPitch());
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			/* END REFLECTION */
			/* REFRACTION */
			GL11.glEnable(GL30.GL_CLIP_DISTANCE1);
			fbos.bindRefractionFrameBuffer();
			
			renderer.render(light, camera, refractionClipPlane, false, false);
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE1);
			fbos.unbindCurrentFrameBuffer();
			/* END REFRACTION */

			//render and clear buffers
			renderer.render(light, camera, reflectionClipPlane, true, underwater);	 
			//render water
			waterRenderer.render(water, camera);
			//update
			DisplayManager.updateDisplay();

		}

		//clean up
		bonFireSource.delete();
		waterShader.cleanUp();
		loader.cleanUp();
		renderer.cleanUp();
		DisplayManager.closeDisplay();

	}

}
