package me.lysne;

import me.lysne.audio.AudioManager;
import me.lysne.graphics.*;
import me.lysne.graphics.text.Font;
import me.lysne.graphics.text.TextMesh;
import me.lysne.window.Display;
import me.lysne.window.Input;
import me.lysne.window.Timer;
import me.lysne.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Main {

    private Display display;
    private AudioManager audioManager;
    private Input input;
    private Camera camera;
    private World world;
    private Font font;

    private ShaderProgram defaultShader;
    private ShaderProgram textShader;

    private TextMesh testText;

    public Main() {

        display = new Display(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, Config.WINDOW_TITLE);
        camera = new Camera(new Vector3f(0, 0, 0));
        input = new Input(display);

        audioManager = new AudioManager();

        world = new World();
        defaultShader = new ShaderProgram("default");
        //defaultShader = new ShaderProgram("cel");
        defaultShader.registerUniforms(
                "view",
                "projection",
                "transform.position",
                "transform.orientation",
                "transform.scale",
                "lightPosition",
                "lightColor",
                "skyColor");
        textShader = new ShaderProgram("text");
        textShader.registerUniforms("viewProjection", "model", "font");
        textShader.setUniform("font", 0);

        font = new Font(Config.FONT_DIR + "signed.fnt");
        testText = new TextMesh("FPS: 00", font, 2, new Vector2f(0, Config.WINDOW_HEIGHT), 12, true)
                .color(0f, 0f, 0f).build();
    }

    private void destroy() {

        display.destroy();
        audioManager.destroy();
        defaultShader.destroy();
        textShader.destroy();
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

        // Rendering
        defaultShader.use();
        defaultShader.setUniform("view", camera.view);
        defaultShader.setUniform("projection", camera.projection);
        world.draw(defaultShader);

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


    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
}
