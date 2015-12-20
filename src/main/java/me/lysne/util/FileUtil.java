package me.lysne.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtil {

    public static class Image {

        public final ByteBuffer data;
        public final int width;
        public final int height;
        public final int numComponents;

        public Image(ByteBuffer data, int width, int height, int numComponents) {

            this.data = data;
            this.width = width;
            this.height = height;
            this.numComponents = numComponents;
        }

        public void destroy() {

            STBImage.stbi_image_free(data);
        }
    }

    public static String readFile(InputStream stream) {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println("Failed to read file!");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {}
        }

        return builder.toString();
    }

    public static ArrayList<String> readFileLines(InputStream stream) {

        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file!");
        }
        return lines;
    }

    public static Image loadImageFile(String path, boolean flip) {

        STBImage.stbi_set_flip_vertically_on_load(flip ? 1 : 0);

        int reqComp = 4;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer numComponents = BufferUtils.createIntBuffer(1);
        ByteBuffer data = STBImage.stbi_load(path, width, height, numComponents, reqComp);
        return new Image(data, width.get(), height.get(), numComponents.get());
    }

    @Deprecated
    public static ByteBuffer readFileToBuffer(File file) {

        ByteBuffer buffer = null;
        FileInputStream stream = null;
        FileChannel channel = null;
        try {
            stream = new FileInputStream(file);
            channel = stream.getChannel();
            buffer = BufferUtils.createByteBuffer((int) channel.size() + 1);

            while (channel.read(buffer) != -1);
            buffer.flip();
        } catch (IOException e) {
            System.err.println("Failed to read file");
        } finally {
            try {
                stream.close();
                channel.close();
            } catch (IOException | NullPointerException e) {}
        }

        return buffer;
    }
}
