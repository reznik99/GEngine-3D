package water;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import tools.Maths;

public class WaterRenderer {

	private static final String DUDV_MAP = "/water/waterDUDV";
	private static final float WAVE_SPEED = 0.015f;
	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers fbos;
	
	private int dudvTexture;
	private float moveFactor = 0;

	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
		this.shader = shader;
		this.fbos = fbos;
		dudvTexture = loader.loadTexture(DUDV_MAP);
		this.setUpVAO(loader);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();

	}

	public void render(WaterTile water, Camera camera) {
		this.prepareRenderer(camera);

		Matrix4f modelMatrix = Maths.createTransformationMatrix(
				new Vector3f(water.getX(), water.getHeight(), water.getZ()), 0, 0, 0, 
				WaterTile.TILE_SIZE);
		//render
		shader.loadModelMatrix(modelMatrix);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());

		this.unbind();
	}

	private void prepareRenderer(Camera camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
		moveFactor %= 1;
		shader.loadOffset(moveFactor);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
	}

	private void unbind() {
		//disable attribs in vao
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader) {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);//2d
	}
}
