package water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;
import tools.Maths;

public class WaterShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/water/waterVertexShader.txt";
	private static final String FRAGMENT_FILE = "/water/waterFragmentShader.txt";
	
	private int location_modelMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	private int location_reflectionTexture; 
	private int location_refractionTexture; 
	
    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
 
    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        
    	location_reflectionTexture = getUniformLocation("reflectionTexture");
    	location_refractionTexture = getUniformLocation("refractionTexture");
    }
	
	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
	}
    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(location_projectionMatrix, projection);
    }
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
    }
    public void loadModelMatrix(Matrix4f modelMatrix){
        loadMatrix(location_modelMatrix, modelMatrix);
    }
}
