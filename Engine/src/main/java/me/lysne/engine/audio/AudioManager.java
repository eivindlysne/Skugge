package me.lysne.engine.audio;

import me.lysne.engine.graphics.Camera;
import me.lysne.engine.window.Input;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class AudioManager {

    public static final int MAX_SOURCES = 16;

    private long device;
    private long context;

    private final int[] sources = new int[MAX_SOURCES];
    private final boolean[] sourcesIdle = new boolean[MAX_SOURCES];

    private Set<Music> playingMusic = new HashSet<>();

    public AudioManager() {

        device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (device == MemoryUtil.NULL)
            throw new RuntimeException("Failed to open default audio device!");

        ALCCapabilities capabilities = ALC.createCapabilities(device);

        context = ALC10.alcCreateContext(device, (ByteBuffer) null);
        ALC10.alcMakeContextCurrent(context);

        AL.createCapabilities(capabilities);

        for (int i = 0; i < sources.length; i++) {
            int source = AL10.alGenSources();
            if (AL10.alGetError() != AL10.AL_NO_ERROR)
                break;
            sources[i] = source;
            sourcesIdle[i] = true;
        }

//        // Temp
//        Music m = new Music(
//                this,
//                ClassLoader.getSystemResource(Config.MUSIC_DIR + "DiamondQuest.ogg").getFile());
//        m.play(new Vector3f(0f,0f,0f), false);
//        playingMusic.add(m);
    }

    public void destroy() {

        for (Music m : playingMusic)
            m.destroy();

        for (int source : sources) {
            if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_STOPPED)
                AL10.alSourceStop(source);
            AL10.alDeleteSources(source);
        }

        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

    public void update(final Camera camera, final Input input) {

        Vector3f lPosition = camera.position();
        AL10.alListener3f(AL10.AL_POSITION, lPosition.x, lPosition.y, lPosition.z);

        // TODO Velocity and orientation. Lets do default for now

        if (input.keyDownWithReset(GLFW.GLFW_KEY_P)) {
            for (Music m : playingMusic)
                if (m.isPlaying())
                    m.pause();
                else
                    m.play(new Vector3f(0f,0f,0f), false);
        }
        for (Music m : playingMusic)
            m.update();
    }

    public int requestIdleSource() {

        for (int i = 0; i < MAX_SOURCES; i++) {
            if (sourcesIdle[i]) {
                sourcesIdle[i] = false;
                int source = sources[i];
                int state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
                if (state != AL10.AL_PLAYING && state != AL10.AL_PAUSED) {
                    AL10.alSourceStop(source);
                    // Invalidate buffer?
                    AL10.alSourcei(source, AL10.AL_BUFFER, 0);
                    AL10.alSourcef(source, AL10.AL_GAIN, 1);
                    AL10.alSourcef(source, AL10.AL_PITCH, 1);
                    return source;
                }
            }
        }
        return -1;
    }

    public void releaseSource(int source) {

        AL10.alSourceStop(source);
        AL10.alSourcei(source, AL10.AL_BUFFER, 0);
        // This is probably the most efficient way when
        // the number of simultaneous sources is small
        for (int i = 0; i < MAX_SOURCES; i++) {
            if (source == sources[i]) {
                sourcesIdle[i] = true;
                break;
            }
        }
    }

    public void unregister(Music music) {

        playingMusic.remove(music);
    }
}
