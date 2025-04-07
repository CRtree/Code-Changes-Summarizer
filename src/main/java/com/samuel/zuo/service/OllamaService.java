package com.samuel.zuo.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.samuel.zuo.entity.OllamaModelList;
import com.samuel.zuo.setting.CommitByAISettingsState;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description: OllamaService
 * date: 2024/11/7 16:59
 * author: samuel_zuo
 * version: 1.0
 */
public class OllamaService implements ModelService {

    @Override
    public String getModelName() {
        return "";
    }

    @Override
    public String getRemoteAPIUrl() {
        return "http://localhost:11434/api/generate";
    }

    @Override
    public String getRemoteAPIToken() {
        return "";
    }

    @Override
    public Request buildRequest(String prompt) {
        JsonObject bodyJson = new JsonObject();
        String model = CommitByAISettingsState.getInstance().model;
        bodyJson.addProperty("model", model);
        bodyJson.addProperty("prompt", prompt);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyJson.toString());
        return new Request.Builder()
                .url( getRemoteAPIUrl())
                .post(requestBody)
                .build();
    }

    @Override
    public String parseResponse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        text = text.trim();
        if (text.endsWith("}")) {
            // parse response body to JsonObject
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(text, JsonObject.class);
            return jsonObject.get("response").getAsString();
        }
        return "";
    }

    public List<String> listLocalModels() {
        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        // Build the request
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/tags")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    // parse response body to JsonObject
                    Gson gson = new Gson();
                    String json = body.string();
                    OllamaModelList ollamaModelList = gson.fromJson(json, OllamaModelList.class);
                    List<String> modelNames = new ArrayList<>();
                    if (ollamaModelList != null && ollamaModelList.getModels() != null) {
                        // List<OllamaModelInfo> sort by modified_at
                        ollamaModelList.getModels().sort((o1, o2) -> o2.getModified_at().compareTo(o1.getModified_at()));
                        for (int i = 0; i < ollamaModelList.getModels().size(); i++) {
                            modelNames.add(ollamaModelList.getModels().get(i).getName());
                        }
                    }
                    return modelNames;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
