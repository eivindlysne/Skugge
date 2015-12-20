package me.lysne;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Config {

    // Math
    public static final long SEED = 0L;
    public static final float EPSILON = 0.001f;

    // Debug
    public static final boolean DEBUG = true;
    public static final boolean AUDIO_DEBUG = false;
    public static final boolean FONT_DEBUG = false;

    // Flags
    public static final boolean FLYING = true;

    // Window
    public static final int WINDOW_WIDTH = 960;
    public static final int WINDOW_HEIGHT = WINDOW_WIDTH / 16 * 9;
    public static final String WINDOW_TITLE = "Stuff";

    // Color
    public static final Vector4f SKY_COLOR = new Vector4f(0.53f, 0.81f, 0.98f, 0f);

    // Timing
    public static final int FRAME_RATE = 60;
    public static final double FRAME_TIME = 1.0 / FRAME_RATE;

    // Input
    public static final float VELOCITY = 0.2f;
    public static final double MOUSE_SENSITIVITY = 0.2;

    // Path
    public static final String SHADER_DIR = "shaders/";
    public static final String MUSIC_DIR = "audio/music/";
    public static final String SOUND_DIR = "audio/sound/";
    public static final String FONT_DIR = "fonts/";
}
