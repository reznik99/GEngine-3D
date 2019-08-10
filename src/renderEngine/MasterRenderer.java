package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import water.WaterRenderer;
import water.WaterShader;

/**
 * Master renderer, contains all constants for all 
 * renderers and calls them.
 * @author Francesco
 */
public class MasterRenderer {
	
	public static final float FOV = 85;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 2000;
	
	private Vector3f skyColor = new Vector3f(0, 0.4f, 0.7f);
	private Matrix4f projectionMatrix;
	
	/* shaders and renderers */
	//entities
	private StaticShader entityShader = new StaticShader();
	private EntityRenderer entityRenderer;
	//terrain
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	//skybox
	private SkyboxRenderer skyboxRenderer;
	//shadows
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
	private List<Terrain> terrains = new ArrayList<>();
	
	
	public MasterRenderer(Loader loader, Camera camera) {
		//dont render polygons facing away from camera
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);
	}
	
	/**
	 * 
	 * @param sun
	 * @param camera
	 * @param clipPlane
	 * @param clear Boolean to reduce amount of processed entities 
	 * (in multiple draw calls for other fbos ex. reflections. Load entities in hashmap only once for n renders)
	 */
	public void render(Light sun, Camera camera, Vector4f clipPlane, boolean clear) {
		boolean underwater = camera.getPlayer().getUnderwater();
		prepare();
		//entities
		entityShader.start();
		entityShader.loadUnderWater(underwater);
		entityShader.loadSkyColor(skyColor);
		entityShader.loadLight(sun);
		entityShader.loadViewMatrix(camera);
		entityShader.loadClipPlane(clipPlane);
		entityRenderer.render(entities);
		entityShader.stop();
		//terrain
		terrainShader.start();
		terrainShader.loadUnderWater(underwater);
		terrainShader.loadSkyColor(skyColor);
		terrainShader.loadLight(sun);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadClipPlane(clipPlane);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		//skybox
		skyboxRenderer.render(camera);
		
		if(clear) {
			terrains.clear();
			entities.clear();
		}
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity) {
		TexturedModel texModel = entity.getModel();
		List<Entity> batch = entities.get(texModel);
		if(batch!=null) {//batch already exitst
			batch.add(entity);
		}
		else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(texModel, newBatch);
		}
	}
	
	public void renderShadowMap(Light sun) {
		shadowMapRenderer.render(entities, sun);
	}
	
	public void cleanUp() {
		entityShader.cleanUp();
		terrainShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}  
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);//zbuffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapRenderer.getShadowMap());
	}	
	
    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f))); // * aspectRatio
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
 
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
    
    public Matrix4f getProjectionMatrix() {
    	return this.projectionMatrix;
    }
    
}
