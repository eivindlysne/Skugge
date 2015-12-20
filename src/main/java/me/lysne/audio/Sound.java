package me.lysne.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;

public class Sound {

    private AudioManager audioManager;
    private int buffer = -1;
    private int source = -1;

    public Sound(final AudioManager audioManager, final ByteBuffer pcm, int channels, int sampleRate) {

        this.audioManager = audioManager;
        buffer = AL10.alGenBuffers();
        AL10.alBufferData(buffer, channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, pcm, sampleRate);
    }

    public void destroy() {

        AL10.alDeleteBuffers(buffer);
    }

    public void play(float volume, final Vector3f position) {

        int source = audioManager.requestIdleSource();
        if (source == -1) {
            System.err.println("No obtainable source");
            return;
        }
        this.source = source;

        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE /* Don't allow looping sounds for now */);
        AL10.alSourcef(source, AL10.AL_GAIN, volume);
        AL10.alSource3f(source, AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alSourcePlay(source);
    }

    public void stop() {
        AL10.alSourceStop(source);
        source = -1;
    }

    // NOTE: Cant really resume atm.
    public void pause() {
        AL10.alSourcePause(source);
    }
}
