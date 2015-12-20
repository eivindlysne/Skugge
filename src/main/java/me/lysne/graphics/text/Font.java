package me.lysne.graphics.text;

import me.lysne.Config;
import me.lysne.util.FileUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Font {

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

    private void createTexture() {

        FileUtil.Image image = FileUtil.loadImageFile(
                ClassLoader.getSystemResource(Config.FONT_DIR + textureFile).getPath(),
                true
        );

        textureHandle = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
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
                            break;
                        case "spacing":
                            String[] spacingS = propertyValue.split(",");
                            for (int i = 0; i < spacingS.length; i++)
                                spacing[i] = Integer.parseInt(spacingS[i]);
                            break;
                        // common
                        case "lineHeight":
                            lineHeight = Integer.parseInt(propertyValue);
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
                            glyph.x = Double.parseDouble(propertyValue);
                            break;
                        case "y":
                            glyph.y = Double.parseDouble(propertyValue);
                            break;
                        case "width":
                            glyph.width = Double.parseDouble(propertyValue);
                            break;
                        case "height":
                            glyph.height = Double.parseDouble(propertyValue);
                            break;
                        case "xoffset":
                            glyph.xoffset = Double.parseDouble(propertyValue);
                            break;
                        case "yoffset":
                            glyph.yoffset = Double.parseDouble(propertyValue);
                            break;
                        case "xadvance":
                            glyph.yadvance = Double.parseDouble(propertyValue);
                            break;
                    }
                } else if (s.equals("char") && glyph.id != -1) {

                    glyphs.put(glyph.id, glyph);
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
