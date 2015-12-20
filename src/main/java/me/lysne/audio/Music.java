package me.lysne.audio;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Music {

    public static final int BUFFER_SIZE = 4096 * 10;
    public static final int NUM_BUFFERS = 3;
    public static final int BYTES_PER_SAMPLE = 2;

    private AudioManager audioManager;

    private int source = -1;
    private long decoderHandle = -1;
    private IntBuffer buffers = BufferUtils.createIntBuffer(NUM_BUFFERS);
    private ByteBuffer pcm = BufferUtils.createByteBuffer(BUFFER_SIZE);
    private STBVorbisInfo info = STBVorbisInfo.create();

    private int format;
    private int sampleRate;
    private int bufferOverhead = 0;
    private float volume = 1;
    private float pan = 0;
    private boolean looping = false;
    private boolean playing = false;
    private int samplesPerBuffer = BUFFER_SIZE / BYTES_PER_SAMPLE;
    private int renderedSamples = 0;
    private float secondsPerBuffer = 0;
    private float renderedSeconds = 0;

    public Music(final AudioManager audioManager, String resource) {

        this.audioManager = audioManager;

        decoderHandle = OggDecoder.openStream(resource, info);

        format = info.channels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
        sampleRate = info.sample_rate();

        AL10.alGenBuffers(buffers);

        secondsPerBuffer = (float) (BUFFER_SIZE - bufferOverhead) / (BYTES_PER_SAMPLE * info.channels() * sampleRate);
    }

    public void destroy() {

        stop();
        AL10.alDeleteBuffers(buffers);

        // NOTE: source is managed by the AudioManager
        // and so we don't destroy it here.
    }

    public void play(final Vector3f position, boolean looping) {

        // Skip this section if we are simply paused
        if (source == -1) {
            int source = audioManager.requestIdleSource();
            if (source == -1) {
                System.err.println("No obtainable source");
                return;
            }
            this.source = source;
            this.looping = looping;

            AL10.alSourcei(source, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
            AL10.alSourcef(source, AL10.AL_GAIN, volume);
            AL10.alSource3f(source, AL10.AL_POSITION, position.x, position.y, position.z);

            for (int i = 0; i < NUM_BUFFERS; i++) {
                int buffer = buffers.get(i);
                fill(buffer);
                AL10.alSourceQueueBuffers(source, buffer);
            }
        }

        if (!playing) {
            playing = true;
            AL10.alSourcePlay(source);
        }
    }

    private boolean fill(int buffer) {

        pcm.clear();
        int samples = OggDecoder.decodeStream(decoderHandle, pcm, info.channels(), samplesPerBuffer);
        if (samples > 0) {
            AL10.alBufferData(buffer, format, pcm, sampleRate);
            renderedSamples += samples;
            return true;
        }
        return false;
    }

    public void update() {

        if (source == -1)
            return;

        boolean endOfStream = false;
        int processedBuffers = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
        while (processedBuffers-- > 0) {

            int buffer = AL10.alSourceUnqueueBuffers(source);
            if (buffer == AL10.AL_INVALID_VALUE)
                break;

            renderedSeconds += secondsPerBuffer;

            // Continue so we can un-queue all buffers
            if (endOfStream)
                continue;

            if (fill(buffer))
                AL10.alSourceQueueBuffers(source, buffer);
            else
                endOfStream = true;
        }

        // What if we are looping? Reset stream to the beginning?
        if (endOfStream && AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED) == 0)
            stop();

        // Resume if we have stopped playing for some reason, e.g. underflow
        if (playing && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING)
            AL10.alSourcePlay(source);
    }

    public void stop() {

        if (source == -1)
            return;

        audioManager.unregister(this);
        audioManager.releaseSource(source);

        OggDecoder.closeStream(decoderHandle);

        source = -1;
        decoderHandle = -1;

        renderedSamples = 0;
        renderedSeconds = 0f;
        playing = false;
    }

    public void pause() {

        if (source == -1)
            return;

        AL10.alSourcePause(source);
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }
}
