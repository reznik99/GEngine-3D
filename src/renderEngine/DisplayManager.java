package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int FPS_CAP = 120;
    
    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay(){

    	//version 3.2
        ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat().withDepthBits(24), attribs);
            Display.setTitle("Gorini 3D Engine");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT); //use whole window for rendering
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f; //in seconds
        lastFrameTime = currentFrameTime;
    }
    
    public static float getFrameTimeSeconds() {
    	return delta;
    }

    public static void closeDisplay(){
        Display.destroy();
    }
    
    private static long getCurrentTime() {
    	return Sys.getTime()*1000 / Sys.getTimerResolution();
    }
}
