package com.calmatrixoffline;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.io.ByteArrayOutputStream;

public class AudioCaptureHelper {

    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL     = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT      = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder;
    private Thread      thread;
    private volatile boolean running;
    private final ByteArrayOutputStream buf = new ByteArrayOutputStream();

    public boolean start() {
        int minBuf = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT);
        if (minBuf <= 0) return false;

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE, CHANNEL, FORMAT, minBuf * 4);
        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            recorder.release();
            recorder = null;
            return false;
        }

        synchronized (buf) { buf.reset(); }
        running = true;
        recorder.startRecording();

        thread = new Thread(() -> {
            byte[] tmp = new byte[minBuf];
            while (running) {
                int n = recorder.read(tmp, 0, tmp.length);
                if (n > 0) {
                    synchronized (buf) { buf.write(tmp, 0, n); }
                }
            }
        }, "WhisperCapture");
        thread.start();
        return true;
    }

    public short[] stop() {
        running = false;
        if (thread != null) {
            try { thread.join(2000); } catch (InterruptedException ignored) {}
            thread = null;
        }
        if (recorder != null) {
            try { recorder.stop(); } catch (IllegalStateException ignored) {}
            recorder.release();
            recorder = null;
        }

        byte[] raw;
        synchronized (buf) { raw = buf.toByteArray(); }
        int n = raw.length / 2;
        short[] out = new short[n];
        for (int i = 0; i < n; i++) {
            int lo = raw[i * 2]     & 0xFF;
            int hi = raw[i * 2 + 1] & 0xFF;
            out[i] = (short)((hi << 8) | lo);
        }
        return out;
    }
}