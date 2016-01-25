package me.lysne.graphics.text;

import me.lysne.Config;
import me.lysne.util.FileUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Font {

    private static final float LINE_HEIGHT = 0.03f;
    private static final int PADDING = 8; // Same as when creating the font!

    private static final Pattern WHITESPACE_NOT_IN_QUOTES = Pattern.compile("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    private static final Pattern PROPERTY = Pattern.compile("^([\\w]+)=([\\w|\\W]+)$");

    private String face;
    private int stretchH;
    private int[] padding = new int[] {0, 0, 0, 0};
    private int[] spacing = new int[] {0, 0};

    private int lineHeight;
    private int base;
    private int scaleW;
    private int scaleH;

    private int paddingWidth;
    private int paddingHeight;
    private float spaceWidth;
    private float verticalPerPixelSize;
    private float horizontalPerPixelSize;

    private String textureFile;
    private int textureHandle;

    private Map<Integer, Glyph> glyphs = new HashMap<>();

    public Font(String resource) {

        readFontFile(resource);
        createTexture();

        if (Config.FONT_DEBUG)
            printDebug();

    }

    public void destroy() {

        GL11.glDeleteTextures(textureHandle);
    }

    public void bindTexture(int unit) {

        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
    }

    public Glyph getGlyph(char c) {

        return glyphs.get((int) c);
    }

    public float spaceWidth() {

        return spaceWidth;
    }

    private void createTexture() {

        FileUtil.Image image = FileUtil.loadImageFile(
                ClassLoader.getSystemResource(Config.FONT_DIR + textureFile).getFile(),
                true
        );
        System.out.println(ClassLoader.getSystemResource(Config.FONT_DIR + textureFile).getFile());

        textureHandle = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                image.width,
                image.height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                image.data
        );
        //GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); // TODO: Giving us anything?

        image.destroy();
    }

    private void readFontFile(String resource) {

        ArrayList<String> lines = FileUtil.readFileLines(ClassLoader.getSystemResourceAsStream(resource));
        Glyph glyph = new Glyph();

        for (String line : lines) {

            for (String s : WHITESPACE_NOT_IN_QUOTES.split(line)) {

                Matcher matcher = PROPERTY.matcher(s);

                if (matcher.matches()) {

                    // Group 0 is the whole matching string
                    String propertyName = matcher.group(1);
                    String propertyValue = matcher.group(2);

                    switch (propertyName) {

                        // info
                        case "face":
                            face = propertyValue.replace("\"", "");
                            break;
                        case "stretchH":
                            stretchH = Integer.parseInt(propertyValue);
                            break;
                        case "padding":
                            String[] paddingS = propertyValue.split(",");
                            for (int i = 0; i < paddingS.length; i++)
                                padding[i] = Integer.parseInt(paddingS[i]);
                            paddingWidth = padding[1] + padding[3];
                            paddingHeight = padding[0] + padding[2];
                            break;
                        case "spacing":
                            String[] spacingS = propertyValue.split(",");
                            for (int i = 0; i < spacingS.length; i++)
                                spacing[i] = Integer.parseInt(spacingS[i]);
                            break;
                        // common
                        case "lineHeight":
                            lineHeight = Integer.parseInt(propertyValue);
                            int lineHeightPixels = lineHeight - paddingHeight;
                            verticalPerPixelSize = LINE_HEIGHT / lineHeightPixels;
                            horizontalPerPixelSize = verticalPerPixelSize / Config.ASPECT_RATIO;
                            break;
                        case "base":
                            base = Integer.parseInt(propertyValue);
                            break;
                        case "scaleW":
                            scaleW = Integer.parseInt(propertyValue);
                            break;
                        case "scaleH":
                            scaleH = Integer.parseInt(propertyValue);
                            break;
                        // page
                        case "file":
                            textureFile = propertyValue.replace("\"", "");
                            break;
                        // char
                        case "id":
                            int id = Integer.parseInt(propertyValue);
                            // NOTE: Hack to skip over the page id.
                            // Also, not a very good one...
                            if (id > 0)
                                glyph.id = id;
                            break;
                        case "x":
                            float x = Float.parseFloat(propertyValue);
                            glyph.xTexCoord = (x + (padding[1] - PADDING)) / scaleW;
                            break;
                        case "y":
                            float y = Float.parseFloat(propertyValue);
                            glyph.yTexCoord = -((y + (padding[0] - PADDING)) / scaleH);
                            break;
                        case "width":
                            float width = Float.parseFloat(propertyValue) - (paddingWidth - (2 * PADDING));
                            glyph.quadWidth = width * horizontalPerPixelSize;
                            glyph.xTexSize = width / scaleW;
                            break;
                        case "height":
                            float height = Float.parseFloat(propertyValue) - (paddingHeight - (2 * PADDING));
                            glyph.quadHeight = height * verticalPerPixelSize;
                            glyph.yTexSize = height / scaleH;
                            break;
                        case "xoffset":
                            float xoffset = Float.parseFloat(propertyValue);
                            glyph.xoffset = (xoffset + padding[1] - PADDING) * horizontalPerPixelSize;
                            break;
                        case "yoffset":
                            float yoffset = Float.parseFloat(propertyValue);
                            glyph.yoffset = (yoffset + padding[0] - PADDING) * verticalPerPixelSize;
                            break;
                        case "xadvance":
                            glyph.xadvance = (Float.parseFloat(propertyValue) - paddingWidth) * horizontalPerPixelSize;
                            break;
                    }
                } else if (s.equals("char") && glyph.id != -1) {
                    if (glyph.id == 32) {
                        spaceWidth = glyph.xadvance;
                    } else {
                        glyphs.put(glyph.id, glyph);
                    }
                    glyph = new Glyph();
                }
            }
        }
    }

    private void printDebug() {
        System.out.println("=== Font ===");
        System.out.println(face);
        System.out.println(stretchH);
        System.out.println(Arrays.toString(padding));
        System.out.println(Arrays.toString(spacing));
        System.out.println(lineHeight);
        System.out.println(base);
        System.out.println(scaleW);
        System.out.println(scaleH);
        System.out.println(textureFile);
        System.out.println(glyphs.size());
    }
}
