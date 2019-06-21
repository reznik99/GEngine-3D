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
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
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
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "heightmap2");
		
		List<Entity> entities = new ArrayList<Entity>();

		/* LOAD Models and Textures */
		TexturedModel dragon = new TexturedModel(OBJLoader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("metal")));
		dragon.getTexture().setReflectivity(10);
		dragon.getTexture().setShineDamper(10);

		TexturedModel stall = new TexturedModel(OBJLoader.loadObjModel("stall", loader), new ModelTexture(loader.loadTexture("stallTexture")));
		stall.getTexture().setReflectivity(2);
		stall.getTexture().setShineDamper(50);

		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("redridgeTree", loader), new ModelTexture(loader.loadTexture("redridgeTree1")));

		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));

		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		
		TexturedModel rock = new TexturedModel(OBJLoader.loadObjModel("rock", loader), new ModelTexture(loader.loadTexture("metal")));
		
		TexturedModel tower = new TexturedModel(OBJLoader.loadObjModel("tower1", loader), new ModelTexture(loader.loadTexture("tower1")));
		
		TexturedModel building = new TexturedModel(OBJLoader.loadObjModel("building1", loader), new ModelTexture(loader.loadTexture("metal")));
		
		TexturedModel waterModel = new TexturedModel(OBJLoader.loadObjModel("plane", loader), new ModelTexture(loader.loadTexture("water")));
		waterModel.getTexture().setReflectivity(1);
		waterModel.getTexture().setShineDamper(5);
		
		TexturedModel statueModel = new TexturedModel(OBJLoader.loadObjModel("alliance_statue", loader), new ModelTexture(loader.loadTexture("alliance_statue")));
		statueModel.getTexture().setReflectivity(1);
		statueModel.getTexture().setShineDamper(5);
		
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("Chogall", loader), new ModelTexture(loader.loadTexture("Chogall")));
		playerModel.getTexture().setReflectivity(0.2f);
		playerModel.getTexture().setShineDamper(5);
		
		TexturedModel bonfireModel = new TexturedModel(OBJLoader.loadObjModel("bonfire", loader), new ModelTexture(loader.loadTexture("bonfire")));
		
		TexturedModel statue2Model = new TexturedModel(OBJLoader.loadObjModel("lotharStatue", loader), new ModelTexture(loader.loadTexture("lotharStatue1")));
		statue2Model.getTexture().setReflectivity(0.2f);
		statue2Model.getTexture().setShineDamper(5);
		
		TexturedModel fallenTreeModel = new TexturedModel(OBJLoader.loadObjModel("fallenRedridgeTree", loader), new ModelTexture(loader.loadTexture("fallenRedridgeTree")));
		//generate Random Entities
		Random rand = new Random();
		for(int i=0; i<400; i++) {
			float x = rand.nextFloat()*800;
			float z = rand.nextFloat()*800;
			float y = terrain.getHeightAt(x, z);
			while(y<-2f) {
				x = rand.nextFloat()*800;
				z = rand.nextFloat()*800;
				y = terrain.getHeightAt(x, z);
			}
			Vector3f position = new Vector3f(x , y, z);
			TexturedModel model = tree;
			if(i<150)
				model = rand.nextFloat()>0.5 ? grass : rand.nextFloat()>0.5 ? rock : flower;
			
			Entity entity = new Entity(model, position, 0, rand.nextFloat()*360,0,1);
			entity.setScale(rand.nextFloat()*1f + 1f);
			entities.add(entity);
		}
		
		//hardCoded Entities
		Entity buildingEntity = new Entity(building, new Vector3f(125, terrain.getHeightAt(125, 205), 205), 0,180,0,0.25f);
		Entity towerEntity = new Entity(tower, new Vector3f(65, terrain.getHeightAt(65, 75), 75), 0,0,0,7f);
		Entity towerEntity2 = new Entity(tower, new Vector3f(650, terrain.getHeightAt(650, 300), 300), 0,0,0,8f);
		Entity stallEntity = new Entity(stall, new Vector3f(20,0,20), 0,0,0,1);
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
		entities.add(stallEntity);
		
		//player
		Entity playerEntity = new Player(playerModel, new Vector3f(220,terrain.getHeightAt(223, 198)+10,198),0,0,0,3f);
		entities.add(playerEntity);
		
		//camera and light
		Light light = new Light(new Vector3f(223,terrain.getHeightAt(223, 198)+13,198), new Vector3f(1,0.85f,0.55f));
		Camera camera = new Camera((Player) playerEntity);
		MasterRenderer renderer = new MasterRenderer();
		
		//audio
		AudioManager.init();
		int buffer = AudioManager.loadSound("audio/bonfire.wav");
		Source bonFireSource = new Source();
		bonFireSource.setVolume(3);
		bonFireSource.setLooping(true);
		bonFireSource.setPosition(bonfireEntity.getPosition());
		bonFireSource.play(buffer);
		
		//water stuff
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		WaterTile water = new WaterTile(400, 400, -2.5f);
		
		while(!Display.isCloseRequested()){
			//update entities
			AudioManager.setListenerData(
					playerEntity.getPosition().x, 
					playerEntity.getPosition().y, 
					playerEntity.getPosition().z);
			camera.move();
			((Player)playerEntity).move(terrain);
			//light.update();
			
			//Load Entities for rendering
			for(Entity e : entities)
				renderer.processEntity(e);
			renderer.processTerrain(terrain);
			
			/* REFLECTION */
			Vector4f reflectionClipPlane = new Vector4f(0, 1, 0, -water.getHeight());
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
			reflectionClipPlane = new Vector4f(0, 1, 0, 10000000);
			/* END REFLECTION */

			//render and clear buffers
			renderer.render(light, camera, reflectionClipPlane, true);	 
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
