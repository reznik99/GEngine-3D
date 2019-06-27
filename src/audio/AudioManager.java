package audio;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import tools.Maths;

public class AudioManager {
	
	private static List<Integer> buffers = new ArrayList<>();

	public static void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setListenerData(Camera camera, Player player) {
		Vector3f pos = camera.getPosition();
		FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		listenerOri.put( 0, viewMatrix.m01 );
		listenerOri.put( 1, viewMatrix.m02 );
		listenerOri.put( 2, viewMatrix.m03 );
		listenerOri.put( 3, viewMatrix.m11 );
		listenerOri.put( 4, viewMatrix.m12 );
		listenerOri.put( 5, viewMatrix.m13 );
		
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}
	
	public static int loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	public static void cleanUp() {
		for(Integer i : buffers)
			AL10.alDeleteBuffers(i);
		AL.destroy();
	}
}
