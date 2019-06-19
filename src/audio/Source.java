package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Source {
	
	private int sourceId;
	private float volume;
	private float pitch;
	private Vector3f position = new Vector3f(0, 0, 0);
	

	public Source() {
		sourceId = AL10.alGenSources();
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1f);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1f);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 
				position.x, position.y, position.z);
	}
	
	public void play(int buffer) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		continuePlaying();
	}
	
	public void delete() {
		stop();
		AL10.alDeleteSources(sourceId);
	}
	
	public void pause() {
		AL10.alSourcePause(sourceId);
	}
	
	public void continuePlaying() {
		AL10.alSourcePlay(sourceId);
	}
	
	public void stop() {
		AL10.alSourceStop(sourceId);
	}
	
	public boolean isPLaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public void setLooping(boolean looping) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING,
				looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setVolume(float volume) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setPosition(Vector3f pos) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 
				pos.x, pos.y, pos.z);
	}
}
