package me.lysne.graphics.buffer;

import me.lysne.Config;
import me.lysne.graphics.Vertex;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;


/**
 * TODO: Support READ or WRITE only binding
 * TODO: Other dimensions than screen size
 * TODO: More color attachments?
 * TODO: Renderbuffer
 */
public class FramebufferObject {

    public static final int READ = GL30.GL_READ_FRAMEBUFFER;
    public static final int DRAW = GL30.GL_DRAW_FRAMEBUFFER;

    private int handle;
    private int colorAttachment = -1;
    private int depthAttachment = -1;

    private int vao;
    private int vbo;
    // TODO: Do we gain anything from using an index buffer here?
    //private int ibo;

    private int width;
    private int height;

    public FramebufferObject() {

        handle = GL30.glGenFramebuffers();

        width = Config.WINDOW_WIDTH;
        height = Config.WINDOW_HEIGHT;
    }

    public void destroy() {

        GL30.glDeleteFramebuffers(handle);
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

    public FramebufferObject withColorAttachment() {

        bind();

        colorAttachment = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorAttachment);
        // Should use nearest neighbor to avoid blurriness
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGB,
                width,
                height,
                0,
                GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE,
                MemoryUtil.NULL
        );

        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D,
                colorAttachment,
                0
        );

        unbind();

        return this;
    }

    public FramebufferObject withDepthAttachment() {

        bind();

        depthAttachment = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthAttachment);
        // Should use nearest neighbor to avoid blurriness
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL14.GL_DEPTH_COMPONENT24,
                width,
                height,
                0,
                GL11.GL_DEPTH_COMPONENT,
                GL11.GL_FLOAT,
                MemoryUtil.NULL
        );

        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT,
                GL11.GL_TEXTURE_2D,
                depthAttachment,
                0
        );

        unbind();

        return this;
    }

    public FramebufferObject build() {

        // TODO: glDrawBuffers?

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();

        // 2 position + 2 texCoords
        final int vertexSize = 2 + 2;
        final int vertexByteSize = vertexSize * 4;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(6 * vertexSize);
        vertexBuffer
                .put(-1f).put(-1f).put(0f).put(0f)
                .put( 1f).put(-1f).put(1f).put(0f)
                .put( 1f).put( 1f).put(1f).put(1f)
                .put( 1f).put( 1f).put(1f).put(1f)
                .put(-1f).put( 1f).put(0f).put(1f)
                .put(-1f).put(-1f).put(0f).put(0f);
        vertexBuffer.flip();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glEnableVertexAttribArray(0); // position
        GL20.glEnableVertexAttribArray(1); // texCoord

        GL20.glVertexAttribPointer(
                0,
                2,
                GL11.GL_FLOAT,
                false,
                vertexByteSize,
                0L
        );
        GL20.glVertexAttribPointer(
                1,
                2,
                GL11.GL_FLOAT,
                false,
                vertexByteSize,
                2L * 4L
        );

        GL30.glBindVertexArray(0);

        return this;
    }

    public void bind() {

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, handle);
    }

    public void unbind() {

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public boolean isComplete() {

        return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    public void clear() {

        // TODO: Check which buffer we have first
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void draw() {

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorAttachment);

        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
    }
}
