
package com.calmatrixoffline;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.File;
import java.io.IOException;

public class GoogleImageSearch {
    private static final String UPLOAD_URL = "https://www.google.com/searchbyimage/upload";
    private OkHttpClient client = new OkHttpClient();

    public String uploadAndGetSearchUrl(File imageFile) throws IOException {
        // Build multipart request
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("encoded_image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                .addFormDataPart("sch", "sch")
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = client.newCall(request).execute()) {
            // The response contains the redirect URL
            String location = response.header("Location");
            if (location != null && location.startsWith("/searchbyimage")) {
                return "https://www.google.com" + location;
            }
            // Sometimes the response body contains the URL directly
            String body = response.body().string();
            // Parse body to extract the URL (if needed)
            return extractUrlFromBody(body);
        }
    }

    private String extractUrlFromBody(String body) {
        // Look for "window.location" or "URL" in the response
        // Simple regex extraction
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("https?://www\\.google\\.com/searchbyimage[^\"]+");
        java.util.regex.Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}