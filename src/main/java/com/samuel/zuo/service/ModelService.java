package com.samuel.zuo.service;

import okhttp3.Request;

/**
 * description: ModelService
 * date: 2025/4/7 15:19
 * author: samuel_zuo
 * version: 1.0
 */
public interface ModelService {
    String getModelName();
    String getRemoteAPIUrl();
    String getRemoteAPIToken();
    Request buildRequest(String prompt);
    String parseResponse(String text);
}
