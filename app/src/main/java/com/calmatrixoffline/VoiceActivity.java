package com.calmatrixoffline;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceActivity extends AppCompatActivity {

    private static final int PERM_REQUEST = 200;

    private TextView resultText;
    private Button voiceBtn, backBtn;

    private SpeechRecognizer speechRecognizer;
    private IntentHolder intentHolder;
    private boolean listening = false;
    private boolean usingOnDevice = false;

    // Tiny helper to hold the intent without needing an extra import declaration style
    private static class IntentHolder {
        final android.content.Intent intent;
        IntentHolder(android.content.Intent i) { this.intent = i; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        resultText = findViewById(R.id.resultText);
        voiceBtn = findViewById(R.id.voiceBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());
        voiceBtn.setOnClickListener(v -> onVoiceTap());

        setupRecognizer();
    }

    private void setupRecognizer() {
        android.content.Intent intent = new android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        this.intentHolder = new IntentHolder(intent);

        // Try on-device first (Android 12+). Fall back to online automatically.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && SpeechRecognizer.isOnDeviceRecognitionAvailable(this)) {
            createOnDeviceRecognizer();
        } else {
            createOnlineRecognizer();
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createOnDeviceRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(this);
            usingOnDevice = true;
            attachListener();
            resultText.setText("Offline voice ready. Tap to speak.");
        } catch (Exception e) {
            // If on-device fails immediately, drop to online
            createOnlineRecognizer();
        }
    }

    private void createOnlineRecognizer() {
        if (speechRecognizer != null) {
            try { speechRecognizer.destroy(); } catch (Exception ignored) {}
            speechRecognizer = null;
        }
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            usingOnDevice = false;
            attachListener();
            resultText.setText("Online voice ready. Tap to speak.");
        } else {
            voiceBtn.setEnabled(false);
            resultText.setText("Speech recognition not available on this device.");
        }
    }

    private void attachListener() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {
                resultText.setText("🎤 Listening...");
            }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}




            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    handleRecognizedText(matches.get(0));
                } else {
                    resultText.setText("No speech detected. Try again.");
                }
                listening = false;
                voiceBtn.setText("Speak");
            }



            @Override
            public void onError(int error) {
                // If on-device failed, silently retry with online
                if (usingOnDevice && (error == SpeechRecognizer.ERROR_CLIENT
                        || error == SpeechRecognizer.ERROR_SERVER
                        || error == SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED)) {
                    createOnlineRecognizer();
                    resultText.setText("Switched to online. Tap to speak again.");
                    listening = false;
                    voiceBtn.setText("Speak");
                    return;
                }
                resultText.setText("Error: " + getErrorText(error));
                listening = false;
                voiceBtn.setText("Speak");
            }



            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    resultText.setText("🎤 " + matches.get(0));
                }
            }

            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void onVoiceTap() {
        if (listening) {
            if (speechRecognizer != null) speechRecognizer.stopListening();
            listening = false;
            voiceBtn.setText("Speak");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERM_REQUEST);
            return;
        }
        startListening();
    }

    private void startListening() {
        if (speechRecognizer == null) {
            resultText.setText("Recognizer not ready.");
            return;
        }
        try {
            speechRecognizer.startListening(intentHolder.intent);
            listening = true;
            voiceBtn.setText("Stop");
            resultText.setText("🎤 Listening...");
        } catch (Exception e) {
            resultText.setText("Could not start: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else {
            Toast.makeText(this, "Mic permission needed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            try { speechRecognizer.destroy(); } catch (Exception ignored) {}
            speechRecognizer = null;
        }
    }

    private void handleRecognizedText(String spokenText) {
        resultText.setText("🗣️ You said:\n" + spokenText + "\n\nSearching...");

        String[] words = spokenText.toLowerCase().split(" ");
        boolean found = false;
        for (String word : words) {
            FoodDatabase.FoodItem food = FoodDatabase.search(word);
            if (food != null) {
                resultText.setText("✅ Found: " + food.name + "\n" +
                        "Calories: " + food.calories + " cal\n" +
                        "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g\n" +
                        "Serving: " + food.serving);
                Toast.makeText(this, "✅ Found: " + food.name, Toast.LENGTH_SHORT).show();

                android.content.Intent result = new android.content.Intent();
                result.putExtra("recognized_food", food.name);
                setResult(RESULT_OK, result);
                voiceBtn.postDelayed(this::finish, 1200);


                found = true;
                break;
            }
        }
        if (!found) {
            resultText.setText("❌ No food found for:\n" + spokenText + "\n\nTry: 'cheeseburger', 'chicken', 'apple'");
            Toast.makeText(this, "No food found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio error";
            case SpeechRecognizer.ERROR_CLIENT: return "Client error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH: return "No match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "Recognizer busy";
            case SpeechRecognizer.ERROR_SERVER: return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "No speech input";
            default: return "Unknown error (" + errorCode + ")";
        }
    }
}