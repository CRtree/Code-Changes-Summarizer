package com.samuel.zuo.factory;

import com.samuel.zuo.service.DeepSeekModelService;
import com.samuel.zuo.service.ModelService;
import com.samuel.zuo.service.OllamaService;
import com.samuel.zuo.service.OpenAIModelService;

/**
 * description: ModelServiceFactory
 * date: 2025/4/7 15:18
 * author: samuel_zuo
 * version: 1.0
 */
public class ModelServiceFactory {
    private static ModelService deepSeekModelService = new DeepSeekModelService();
    private static ModelService ollamaModelService = new OllamaService();
    private static OpenAIModelService openAIModelService = new OpenAIModelService();

    public static ModelService getModelService(String modelName) {
        if (deepSeekModelService.getModelName().equals(modelName)) {
            return deepSeekModelService;
        } else if (openAIModelService.getModelName().equals(modelName)) {
            return openAIModelService;
        } else {
            return ollamaModelService;
        }
    }
}
