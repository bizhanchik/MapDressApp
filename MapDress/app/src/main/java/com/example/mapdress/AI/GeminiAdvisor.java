package com.example.mapdress.AI;

import android.util.Log;

import com.example.mapdress.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class GeminiAdvisor {

    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    public static String getClothingAdvice(String weather, double temperature) {
        String prompt = "На улице " + temperature + "°C и " + weather +
                ". Дай краткий и практичный совет, как одеться человеку.";

        GenerativeModel model = new GenerativeModel(
                "gemini-pro",
                BuildConfig.GEMINI_API_KEY
        );

        // Альтернативный способ без Builder
        Content input = new Content(Collections.singletonList(new TextPart(prompt)));

        try {
            Content response = model.generateContent(input);
            if (response != null && !response.getParts().isEmpty()) {
                Part part = response.getParts().get(0);
                if (part instanceof TextPart) {
                    return ((TextPart) part).getText();
                } else {
                    return "Ответ не является текстом.";
                }
            } else {
                return "Нет совета.";
            }
        } catch (Exception e) {
            Log.e("GeminiAdvisor", "Ошибка обращения к Gemini", e);
            return "Ошибка при обращении к Gemini.";
        }
    }


}
