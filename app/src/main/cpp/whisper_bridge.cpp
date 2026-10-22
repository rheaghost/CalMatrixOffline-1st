// whisper_bridge.cpp — minimal JNI bridge for Whisper (v1)
#include <jni.h>
#include <android/log.h>
#include <mutex>
#include "whisper.h"
#include <vector>
#include <string>
#include <chrono> //2610191745

#define TAG "WhisperBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static struct whisper_context* g_ctx = nullptr;
static std::mutex g_mutex;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_calmatrixoffline_WhisperBridge_loadWhisperModel(
        JNIEnv* env, jobject /*this*/, jstring modelPath) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_ctx) { whisper_free(g_ctx); g_ctx = nullptr; }

    const char* path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) return JNI_FALSE;

    whisper_context_params cparams = whisper_context_default_params();
    cparams.use_gpu = false;
    g_ctx = whisper_init_from_file_with_params(path, cparams);
    env->ReleaseStringUTFChars(modelPath, path);

    if (!g_ctx) { LOGE("whisper_init failed"); return JNI_FALSE; }
    LOGI("whisper loaded");
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_calmatrixoffline_WhisperBridge_isModelLoaded(
        JNIEnv* /*env*/, jobject /*this*/) {
    return g_ctx ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_calmatrixoffline_WhisperBridge_freeWhisperModel(
        JNIEnv* /*env*/, jobject /*this*/) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_ctx) { whisper_free(g_ctx); g_ctx = nullptr; }


}


extern "C" JNIEXPORT jstring JNICALL
Java_com_calmatrixoffline_WhisperBridge_transcribePcm(
        JNIEnv* env, jobject /*this*/,
        jshortArray pcmData, jint nThreads, jstring language) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (!g_ctx) return env->NewStringUTF("[error: model not loaded]");

    jsize n = env->GetArrayLength(pcmData);
    jshort* pcm = env->GetShortArrayElements(pcmData, nullptr);
    std::vector<float> samples(n);
    for (jsize i = 0; i < n; i++) samples[i] = pcm[i] / 32768.0f;
    env->ReleaseShortArrayElements(pcmData, pcm, JNI_ABORT);

    whisper_full_params wp = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    wp.print_progress   = false;
    wp.print_realtime   = false;
    wp.print_timestamps = false;
    wp.no_context       = true;
    wp.n_threads        = nThreads > 0 ? nThreads : 4;
    wp.detect_language  = false;
    wp.language         = "en";

    const char* lang = nullptr;
    if (language) {
        lang = env->GetStringUTFChars(language, nullptr);
        if (lang && lang[0] != '\0') wp.language = lang;
    }

    auto t_start = std::chrono::steady_clock::now();
    //2610200520
    wp.temperature_inc = 0.0f;   // ← disables retries entirely
    LOGI("retries disabled, running single-pass");


    int rc = whisper_full(g_ctx, wp, samples.data(), n);

    auto t_end = std::chrono::steady_clock::now();
    long elapsedMs = std::chrono::duration_cast<std::chrono::milliseconds>(t_end - t_start).count();
    LOGI("whisper_full rc=%d elapsed=%ld ms", rc, elapsedMs);

    LOGI("transcribePcm: n=%d threads=%d lang=%s", (int)n, wp.n_threads, wp.language);//2610191436s

    if (lang) env->ReleaseStringUTFChars(language, lang);   // release AFTER whisper_full

    if (rc != 0) return env->NewStringUTF("[error: transcription failed]");

    std::string out;
    int ns = whisper_full_n_segments(g_ctx);
    for (int i = 0; i < ns; i++) {
        const char* t = whisper_full_get_segment_text(g_ctx, i);
        if (t) out += t;
    }
    return env->NewStringUTF(out.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_calmatrixoffline_WhisperBridge_detectLanguage(
        JNIEnv* env, jobject /*this*/, jshortArray pcmData) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (!g_ctx) return env->NewStringUTF("");

    jsize n = env->GetArrayLength(pcmData);
    jshort* pcm = env->GetShortArrayElements(pcmData, nullptr);
    std::vector<float> samples(n);
    for (jsize i = 0; i < n; i++) samples[i] = pcm[i] / 32768.0f;
    env->ReleaseShortArrayElements(pcmData, pcm, JNI_ABORT);

    // Use only the first 3 seconds — enough for detection, fast
    int maxSamples = 16000 * 3;
    int useN = n < maxSamples ? n : maxSamples;

    whisper_full_params wp = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    wp.print_progress  = false;
    wp.print_realtime  = false;
    wp.print_timestamps = false;
    wp.no_context      = true;
    wp.n_threads       = 4;
    wp.detect_language = true;
    wp.language        = "auto";
    wp.single_segment  = true;   // we don't care about the text, only the lang id

    if (whisper_full(g_ctx, wp, samples.data(), useN) != 0) {
        return env->NewStringUTF("");
    }

    int id = whisper_full_lang_id(g_ctx);
    const char* code = whisper_lang_str(id);
    return env->NewStringUTF(code ? code : "");
}//2610091459