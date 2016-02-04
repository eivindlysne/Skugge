package me.lysne.game;

import me.lysne.engine.Config;
import me.lysne.engine.audio.AudioManager;
import me.lysne.engine.graphics.Camera;
import me.lysne.engine.graphics.ShaderProgram;
import me.lysne.engine.text.Font;
import me.lysne.engine.text.TextMesh;
import me.lysne.engine.window.Display;
import me.lysne.engine.window.Input;
import me.lysne.engine.window.Timer;
import me.lysne.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Game {

    private Display display;
    private AudioManager audioManager;
    private Input input;
    private Camera camera;
    private World world;
    private Font font;

    private ShaderProgram terrainShader;
    private ShaderProgram textShader;
    private ShaderProgram fboShader;

    private TextMesh testText;

    public Game() {

        display = new Display(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, Config.WINDOW_TITLE);
        camera = new Camera(new Vector3f(0, 0, 0));
        input = new Input(display);

        audioManager = new AudioManager();

        world = new World();
        terrainShader = new ShaderProgram("default");
        terrainShader.registerUniforms(
                "view",
                "projection",
                "clipPlane",
                "transform.position",
                "transform.orientation",
                "transform.scale",
                "lightPosition",
                "lightColor",
                "skyColor"
        );

        textShader = new ShaderProgram("text");
        textShader.registerUniforms("viewProjection", "model", "font");
        textShader.setUniform("font", 0);

        fboShader = new ShaderProgram("fbo");
        fboShader.registerUniforms("fboTexture");
        fboShader.setUniform("fboTexture", 0);

        font = new Font(Config.FONT_DIR + "signed.fnt");
        testText = new TextMesh("FPS: 00", font, 2, new Vector2f(0, Config.WINDOW_HEIGHT), 12, true)
                .color(0.9f, 0.8f, 0.7f).build();
    }


    private void destroy() {

        display.destroy();
        audioManager.destroy();
        terrainShader.destroy();
        textShader.destroy();
        fboShader.destroy();
        world.destroy();
        font.destroy();
        testText.destroy();
    }

    private void update() {

        camera.update(input);
        world.update(camera);
        audioManager.update(camera, input);
    }

    private void render() {

        display.clear();


        world.draw(camera, terrainShader, fboShader);


        textShader.use();
        textShader.setUniform("viewProjection", camera.ortho);
        textShader.setUniform("model", testText.modelMatrix());
        font.bindTexture(0);
        testText.draw();

        display.swap();
    }

    public void run() {

        display.show();

        double lastTime = Timer.getTime();
        double frameCounter = 0.0;
        double unprocessedTime = 0.0;
        int frames = 0;

        while (!display.shouldClose()) {

            boolean render = false;

            double startTime = Timer.getTime();
            double elapsedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += elapsedTime;
            frameCounter += elapsedTime;

            if (frameCounter >= 1.0) {

                frames = 0;
                frameCounter = 0.0;
            }

            while (unprocessedTime > Config.FRAME_TIME) {

                display.poll();

                update();

                render = true;

                unprocessedTime -= Config.FRAME_TIME;
            }

            if (render) {
                render();
                frames++;
            } // Else sleep?
        }

        destroy();
    }
}
