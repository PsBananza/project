package com.dunice.project.config.logger;

import lombok.Builder;
import lombok.val;
import org.json.JSONObject;


public class LogMessage {

    public enum Layer {
        API,
        SERVICE,
        ADAPTER,
        SECURITY,
        ASPECT
    }

    public enum Status {
        SUCCESS,
        FAILED
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @Builder
    private static String createLog(final String userId,
                                    final Layer layer,
                                    final Status status,
                                    final String stacktrace,
                                    final String error,
                                    final String result,
                                    final String params,
                                    final String token,
                                    final String methodName,
                                    final String packageName,
                                    final String appRoute,
                                    final Long duration) {
        val json = new JSONObject();
        json.put("userId", userId);
        json.put("layer", layer);
        json.put("status", status);
        json.put("stacktrace", stacktrace);
        json.put("result", result);
        json.put("params", params);
        json.put("token", token);
        json.put("methodName", methodName);
        json.put("packageName", packageName);
        json.put("duration", duration);
        json.put("error", error);
        json.put("appRoute", appRoute);
        return json.toString();
    }

}
