package com.dunice.project.config.logger;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;


@Slf4j(topic = "logging-module")
public abstract class AbstractLogging {

    private static final int BEARER_LENGTH = 7;
    private static final String NOT_DEFINED = "not_defined";

    private static final String APP_ROUTE_HEADER = "AppRoute";

    private static final String SUB_CLAIM = "sub";

    protected LogMessage.StringBuilder getDefaultMessage(final JoinPoint jp) {
        LogMessage.StringBuilder builder = LogMessage.builder()
                .packageName(getPackageName(jp))
                .methodName(getMethodName(jp));

        HttpServletRequest request = getRequest();
        if (request != null) {
            builder.userId(getClaim(request, SUB_CLAIM));
            builder.appRoute(request.getHeader(APP_ROUTE_HEADER));
        }

        if (log.isDebugEnabled()) {
            builder.params(getParams(jp));
        }

        return builder;
    }

    protected String getSecurityError(final Throwable exception) {
        val builder = LogMessage.builder()
                .status(LogMessage.Status.FAILED)
                .layer(LogMessage.Layer.SECURITY);
        HttpServletRequest request = getRequest();
        if (request != null) {
            builder.userId(getClaim(request, SUB_CLAIM));
            builder.appRoute(request.getHeader(APP_ROUTE_HEADER));
            builder.params(getClaims(request));
        }
        if (exception != null) {
            builder.stacktrace(ExceptionUtils.getStackTrace(exception));
            builder.error(exception.getMessage());
        }

        return builder.build();
    }

    protected String getLoggingError(final Exception exception) {
        return LogMessage.builder()
                .stacktrace(ExceptionUtils.getStackTrace(exception))
                .error(exception.getMessage())
                .layer(LogMessage.Layer.ASPECT)
                .status(LogMessage.Status.FAILED)
                .build();
    }

    @Nullable
    protected HttpServletRequest getRequest() {
        return ofNullable((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    private String getToken(final HttpServletRequest request) {
        return ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(this::extractToken)
                .orElse(null);
    }

    private String extractToken(final String authorization) {
        return authorization.substring(BEARER_LENGTH);
    }

    private String getClaim(final HttpServletRequest request,
                            final String claim) {
        val token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return NOT_DEFINED;
        }
        try {
            return JWTParser.parse(token)
                    .getJWTClaimsSet()
                    .getClaim(claim)
                    .toString();
        } catch (Exception e) {
            return NOT_DEFINED;
        }
    }

    private String getClaims(final HttpServletRequest request) {
        val token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return NOT_DEFINED;
        }
        try {
            return JWTParser.parse(token)
                    .getJWTClaimsSet()
                    .toString();
        } catch (Exception e) {
            return NOT_DEFINED;
        }
    }

    public static String getMethodName(final JoinPoint jp) {
        val signature = (MethodSignature) jp.getSignature();
        return String.format(
                "%s:%s",
                signature.getMethod().getDeclaringClass().getSimpleName(),
                signature.getMethod().getName()
        );
    }

    public static String getPackageName(final JoinPoint jp) {
        val signature = (MethodSignature) jp.getSignature();
        return signature.getMethod().getDeclaringClass().getPackage().getName();
    }

    public static String getParams(final JoinPoint jp) {
        val signature = (MethodSignature) jp.getSignature();
        val names = signature.getParameterNames();
        val values = jp.getArgs();

        return IntStream.range(0, names.length)
                .mapToObj(i -> String.format("%s:%s", names[i], values[i]))
                .collect(toList())
                .toString();
    }

    public static int getStatusCode(final Object result) {
        int statusCode = 0;

        if (result instanceof ResponseEntity) {
            ResponseEntity response = (ResponseEntity) result;
            statusCode = response.getStatusCodeValue();
        }

        return statusCode;
    }

}
