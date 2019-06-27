package skybox;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import shaders.ShaderProgram;
import tools.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "/skybox/skyboxFragmentShader.txt";

	private int location_projectionMatrix;
	private int location_viewMatrix;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		//remove translation so skybox is always moving with player
		viewMatrix.m30 = 0;
		viewMatrix.m31 = 0;
		viewMatrix.m32 = 0;
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
