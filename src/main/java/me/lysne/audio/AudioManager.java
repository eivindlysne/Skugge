package me.lysne.audio;

import me.lysne.Config;
import me.lysne.graphics.Camera;
import me.lysne.window.Input;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AudioManager {

    public static final int MAX_SOURCES = 16;

    public static final int DEFAULT_FREQUENCY = 0;
    public static final int DEFAULT_REFRESH = 0;
    public static final boolean DEFAULT_SYNC = false;

    private final ALDevice device;
    private final ALContext context;

    private final int[] sources = new int[MAX_SOURCES];
    private final boolean[] sourcesIdle = new boolean[MAX_SOURCES];

    private Set<Music> playingMusic = new HashSet<>();

    public AudioManager() {
        this(null);
    }

    public AudioManager(String deviceName) {
        this(deviceName, DEFAULT_FREQUENCY, DEFAULT_REFRESH, DEFAULT_SYNC);
    }

    public AudioManager(String deviceName, int frequency, int refresh, boolean sync) {
        // NOTE: Throws runtime exception on failure
        device = ALDevice.create(deviceName);
        context = ALContext.create(device, frequency, refresh, sync);
        checkErrors(context);

        if (Config.AUDIO_DEBUG) {
            printALCInfo(context);
            printALInfo(context);
            checkErrors(context);
        }

        for (int i = 0; i < sources.length; i++) {
            int source = AL10.alGenSources();
            if (AL10.alGetError() != AL10.AL_NO_ERROR)
                break;
            sources[i] = source;
            sourcesIdle[i] = true;
        }

        //checkCapabilities();

        // Temp
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

        context.destroy();
        device.destroy();
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

    // TODO
    private void checkCapabilities() {

        // AL capabilities
        ALCapabilities alCapabilities = context.getCapabilities();


        // ALC capabilities
        ALCCapabilities alcCapabilities = device.getCapabilities();
    }

    private static void checkErrors(ALContext context) {
        ALUtil.checkALCError(context.getDevice());
        ALUtil.checkALError();
    }

    private static void printALCInfo(ALContext alContext) {
        // we're running 1.1, so really no need to query for the 'ALC_ENUMERATION_EXT' extension
        ALCCapabilities capabilities = ALC.getCapabilities();
        if (capabilities.ALC_ENUMERATION_EXT) {
            if (capabilities.ALC_ENUMERATE_ALL_EXT) {
                printDevices(alContext, 0, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER, "playback");
            } else {
                printDevices(alContext, 0, ALC10.ALC_DEVICE_SPECIFIER, "playback");
            }
            printDevices(alContext, 0, ALC11.ALC_CAPTURE_DEVICE_SPECIFIER, "capture");
        } else {
            System.out.println("No device enumeration available");
        }

        if (capabilities.ALC_ENUMERATE_ALL_EXT) {
            System.out.println("Default playback device: " + ALC10.alcGetString(0, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER));
        } else {
            System.out.println("Default playback device: " + ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));
        }

        System.out.println("Default capture device: " + ALC10.alcGetString(0, ALC11.ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER));

        System.out.println("alContext = " + alContext);
        System.out.println("alContext.getDevice() = " + alContext.getDevice());
        int majorVersion = ALC10.alcGetInteger(alContext.getDevice().address(), ALC10.ALC_MAJOR_VERSION);
        int minorVersion = ALC10.alcGetInteger(alContext.getDevice().address(), ALC10.ALC_MINOR_VERSION);
        checkErrors(alContext);

        System.out.println("ALC version: " + majorVersion + "." + minorVersion);

        System.out.println("ALC extensions:");
        String[] extensions = ALC10.alcGetString(alContext.getDevice().address(), ALC10.ALC_EXTENSIONS).split(" ");
        for (String extension : extensions) {
            if (extension.trim().isEmpty()) {
                continue;
            }
            System.out.println("    " + extension);
        }
        checkErrors(alContext);
    }

    private static void printALInfo(ALContext alContext) {

        System.out.println("OpenAL vendor string: " + AL10.alGetString(AL10.AL_VENDOR));
        System.out.println("OpenAL renderer string: " + AL10.alGetString(AL10.AL_RENDERER));
        System.out.println("OpenAL version string: " + AL10.alGetString(AL10.AL_VERSION));
        System.out.println("AL extensions:");
        String[] extensions = AL10.alGetString(AL10.AL_EXTENSIONS).split(" ");
        for (String extension : extensions) {
            if (extension.trim().isEmpty()) {
                continue;
            }
            System.out.println("    " + extension);
        }
        checkErrors(alContext);
    }


    private static void printDevices(ALContext alContext, long contextDevice, int which, String kind) {

        List<String> devices = ALC.getStringList(contextDevice, which);
        checkErrors(alContext);

        System.out.println("Available " + kind + " devices: ");
        for ( String device : devices ) {
            System.out.println("    " + device);
        }
    }
}
