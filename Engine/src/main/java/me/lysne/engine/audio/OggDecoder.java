package me.lysne.engine.audio;

import me.lysne.engine.util.FileUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class OggDecoder {

    public static ByteBuffer decode(String resource, STBVorbisInfo info) {

        ByteBuffer buffer = FileUtil.readFileToBuffer(new File(resource));
        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoderHandle = STBVorbis.stb_vorbis_open_memory(buffer, error, null);
        if (decoderHandle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to open Ogg Vorbis file: " + error.get(0));

        STBVorbis.stb_vorbis_get_info(decoderHandle, info);

        int channels = info.channels();
        int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoderHandle);

        ByteBuffer pcm = BufferUtils.createByteBuffer(lengthSamples * 2);

        STBVorbis.stb_vorbis_get_samples_short_interleaved(decoderHandle, channels, pcm, lengthSamples);
        STBVorbis.stb_vorbis_close(decoderHandle);

        return pcm;
    }

    public static int decodeStream(
            long decoderHandle,
            ByteBuffer pcm,
            int channels,
            int lengthSamples) {

        return STBVorbis.stb_vorbis_get_samples_short_interleaved(
                decoderHandle,
                channels,
                pcm,
                lengthSamples
        );
    }

    public static long openStream(String resource, STBVorbisInfo info) {

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoderHandle = STBVorbis.stb_vorbis_open_filename(resource, error, null);
        if (decoderHandle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to open Ogg Vorbis file: " + error.get(0));

        STBVorbis.stb_vorbis_get_info(decoderHandle, info);

        return decoderHandle;
    }

    public static void closeStream(long decoderHandle) {

        STBVorbis.stb_vorbis_close(decoderHandle);
    }
}
