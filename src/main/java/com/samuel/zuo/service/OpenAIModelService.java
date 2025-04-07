package com.samuel.zuo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samuel.zuo.setting.CommitByAISettingsState;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * description: OpenAIModelServiceImpl
 * date: 2024/8/6 16:03
 * author: samuel_zuo
 * version: 1.0
 */
public class OpenAIModelService implements ModelService {
    @Override
    public String getModelName() {
        return "gpt-4o";
    }

    @Override
    public String getRemoteAPIUrl() {
        return CommitByAISettingsState.getInstance().aiServerAddress;
    }

    @Override
    public String getRemoteAPIToken() {
        return CommitByAISettingsState.getInstance().token;
    }

    @Override
    public Request buildRequest(String prompt) {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("max_tokens", 1000);
        bodyJson.addProperty("model", "gpt-4o");
        bodyJson.addProperty("temperature", 0.8);
        bodyJson.addProperty("top_p", 1);
        bodyJson.addProperty("presence_penalty", 1);
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        JsonArray messages = new JsonArray();
        messages.add(message);
        bodyJson.add("messages", messages);
        bodyJson.addProperty("stream", true);
        String body = bodyJson.toString();
        System.out.println("request body: " + body);
        //add bearer token to request header
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                body);
        return new Request.Builder()
                .url(getRemoteAPIUrl())
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + getRemoteAPIToken())
                .build();
    }

    @Override
    public String parseResponse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (text.startsWith("data: ")) {
            text = text.substring(6); // Remove "data: "
            if (text.contains("[DONE]")) {
                return "";
            }
        }
        try {
            text = text.trim();
            if (text.contains("choices")) {
                JsonElement jsonElement = JsonParser.parseString(text.trim());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.get("choices").getAsJsonArray().isEmpty()) {
                    return "";
                }
                if (jsonObject.get("choices").getAsJsonArray().get(0).getAsJsonObject().get("delta") == null) {
                    return "";
                }
                if (jsonObject.get("choices").getAsJsonArray().get(0).getAsJsonObject().get("delta").getAsJsonObject().get("content") == null) {
                    return "";
                }
                return jsonObject.get("choices").getAsJsonArray().get(0).getAsJsonObject().get("delta")
                        .getAsJsonObject().get("content").getAsString();
            }
        } catch (Exception e) {
            System.out.println("parse response error, body: " + text);
            e.printStackTrace();
        }
        return "";
    }

}
