package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
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

public class Driver {

	public static void main(String[] args){



		DisplayManager.createDisplay();

		Loader loader = new Loader();

		// *********TERRAIN TEXTURE STUFF***********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "heightmap");
		

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> fancyEntities = new ArrayList<Entity>();


		/* LOAD Models and Textures */
		TexturedModel dragon = new TexturedModel(OBJLoader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("metal")));
		dragon.getTexture().setReflectivity(10);
		dragon.getTexture().setShineDamper(10);

		TexturedModel stall = new TexturedModel(OBJLoader.loadObjModel("stall", loader), new ModelTexture(loader.loadTexture("stallTexture")));
		stall.getTexture().setReflectivity(2);
		stall.getTexture().setShineDamper(50);

		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("pine", loader), new ModelTexture(loader.loadTexture("pine")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);

		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);

		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);
		
		TexturedModel rock = new TexturedModel(OBJLoader.loadObjModel("rock", loader), new ModelTexture(loader.loadTexture("metal")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);
		
		TexturedModel tower = new TexturedModel(OBJLoader.loadObjModel("tower1", loader), new ModelTexture(loader.loadTexture("tower1")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);
		
		TexturedModel building = new TexturedModel(OBJLoader.loadObjModel("building1", loader), new ModelTexture(loader.loadTexture("metal")));
		stall.getTexture().setReflectivity(10);
		stall.getTexture().setShineDamper(10);
		
		TexturedModel water = new TexturedModel(OBJLoader.loadObjModel("plane", loader), new ModelTexture(loader.loadTexture("metal")));
		dragon.getTexture().setReflectivity(10);
		dragon.getTexture().setShineDamper(10);

		Random rand = new Random();
		for(int i=0; i<200; i++) {
			float x = rand.nextFloat()*240;
			float z = rand.nextFloat()*240;
			Vector3f position = new Vector3f(x ,terrain.getHeightAt(x, z), z);
			TexturedModel model = tree;
			if(i<50)
				model = rand.nextFloat()>0.5 ? grass : rand.nextFloat()>0.5 ? rock : grass;
			
			Entity entity = new Entity(model, position, 0, rand.nextFloat()*360,0,1);
			entity.setScale(rand.nextFloat()*1.5f + 0.4f);
			entities.add(entity);
		}
		Entity buildingEntity = new Entity(building, new Vector3f(125, terrain.getHeightAt(125, 205), 205), 0,0,0,1);
		buildingEntity.setScale(0.25f);
		buildingEntity.setRotY(180);
		Entity towerEntity = new Entity(tower, new Vector3f(65, terrain.getHeightAt(65, 75), 75), 0,0,0,1);
		towerEntity.setScale(6f);
		Entity stallEntity = new Entity(stall, new Vector3f(20,0,20), 0,0,0,1);
		
		entities.add(buildingEntity);
		entities.add(towerEntity);
		entities.add(stallEntity);
		
		//fancy entities
		Entity dragonEntity = new Entity(dragon, new Vector3f(10,0,20), 0, 180, 0, 0.5f);
		Entity waterEntity = new Entity(water, new Vector3f(200,-2f,200), 0, 0 ,0,200);
		fancyEntities.add(dragonEntity);
		fancyEntities.add(waterEntity);

		//camera and light
		Light light = new Light(new Vector3f(50,2000,50), new Vector3f(1,1,0.75f));
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
			camera.move();//allow movement
			dragonEntity.increaseRotation(0, .5f, 0);
			//game update
			for(Entity e : entities)
				renderer.processEntity(e);
			for(Entity e : fancyEntities)
				renderer.processFancyEntity(e);
			
			renderer.processTerrain(terrain); //render terrain
			renderer.render(light, camera);	  //render entities

			DisplayManager.updateDisplay();

		}

		//clean up
		loader.cleanUp();
		renderer.cleanUp();
		DisplayManager.closeDisplay();

	}

}
