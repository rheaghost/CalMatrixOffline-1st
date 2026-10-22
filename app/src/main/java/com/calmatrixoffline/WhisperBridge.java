package com.calmatrixoffline;

public class WhisperBridge {
    static {
        System.loadLibrary("whisper_bridge");
    }
    public static native boolean loadWhisperModel(String modelPath);
    public static native boolean isModelLoaded();
    public static native String  transcribePcm(short[] pcmData, int nThreads, String language);
    public static native String  detectLanguage(short[] pcmData);   // ← ADD THIS  //2610181552
    public static native void    freeWhisperModel();
}
