package com.calmatrixoffline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private static final int MAIN_REQUEST_CODE = 300;

    private TextView resultText;
    private Button voiceBtn, backBtn;
    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;
    private boolean listening = false;

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
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            voiceBtn.setEnabled(false);
            resultText.setText("Speech recognition not available on this device");
            return;
        }

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle p) {
                resultText.setText("🎤 Listening...");
            }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float r) {}
            @Override public void onBufferReceived(byte[] b) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                listening = false;
                voiceBtn.setText("Speak");
                resultText.setText("Voice error (" + error + ") — try again");
            }

            @Override
            public void onResults(Bundle results) {
                listening = false;
                voiceBtn.setText("Speak");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    handleRecognizedText(matches.get(0));
                } else {
                    resultText.setText("No speech detected");
                }
            }

            @Override
            public void onPartialResults(Bundle partial) {
                ArrayList<String> m = partial.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (m != null && !m.isEmpty()) resultText.setText("🎤 " + m.get(0));
            }

            @Override public void onEvent(int t, Bundle p) {}
        });

        resultText.setText("Tap to speak");
    }

    private void onVoiceTap() {
        if (recognizer == null) return;
        if (listening) {
            recognizer.stopListening();
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
        listening = true;
        voiceBtn.setText("Stop");
        resultText.setText("🎤 Listening...");
        recognizer.startListening(recognizerIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            resultText.setText("Microphone granted — tap Speak again");
        } else if (requestCode == PERM_REQUEST) {
            resultText.setText("Microphone permission denied");
        }
    }

    private void handleRecognizedText(String spokenText) {
        String cleaned = spokenText.toLowerCase()
                .replaceAll("[^a-z0-9가-힣 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        resultText.setText("🗣 You said: " + spokenText + "\n\nSearching...");

        String[] words = cleaned.split(" ");
        for (String word : words) {
            if (word.isEmpty()) continue;
            FoodDatabase.FoodItem food = FoodDatabase.search(word);
            if (food != null) {
                resultText.setText("✅ Found: " + food.name + "\n" +
                        "Calories: " + food.calories + " cal\n" +
                        "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g\n" +
                        "Serving: " + food.serving);

                voiceBtn.postDelayed(() -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("FOOD_NAME", food.name);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }, 1200);
                return;
            }
        }
        resultText.setText("❌ No food found for: " + spokenText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            try { recognizer.destroy(); } catch (Exception ignored) {}
            recognizer = null;
        }
    }
}