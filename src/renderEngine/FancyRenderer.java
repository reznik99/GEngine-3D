package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.FancyShader;
import shaders.StaticShader;
import textures.ModelTexture;
import tools.Maths;

public class FancyRenderer {
	
	private FancyShader shader;
	
	public FancyRenderer(FancyShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for(TexturedModel model : entities.keySet()) {
			
			this.prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			
			for(Entity e : batch) {
				this.prepareInstance(e);
				//render
				GL11.glDrawElements(GL11.GL_TRIANGLES, 
						model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			this.unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel texModel) {
		RawModel model = texModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		//enable attribs in vao
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
//		ModelTexture texture = texModel.getTexture();
//		this.shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texModel.getTexture().getID());
	}

	private void unbindTexturedModel() {
		//disable attribs in vao
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void prepareInstance(Entity entity) {
		//load tranformation matrix in shader
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
