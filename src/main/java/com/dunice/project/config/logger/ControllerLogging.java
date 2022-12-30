package com.dunice.project.config.logger;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;


@Slf4j(topic = "logging-module")
@Aspect
@Component
public class ControllerLogging
        extends AbstractLogging {

    private static final String RESPONSE_DURATION = "responseDuration";

    @Pointcut("@annotation(com.dunice.project.config.logger.ControllerLog)")
    public void controller() {
    }

    @Before("controller()")
    public void beforeController() {
        try {
            ThreadContext.put(
                    RESPONSE_DURATION,
                    String.valueOf(System.currentTimeMillis())
            );
        } catch (final Exception exception) {
            log.error(getLoggingError(exception));
        }
    }

    @AfterReturning(value = "controller()", returning = "result")
    public void afterController(final JoinPoint jp,
                                @Nullable final Object result) {
        try {
            val message = getDefaultMessage(jp)
                    .layer(LogMessage.Layer.API)
                    .duration(getResponseDuration())
                    .status(LogMessage.Status.SUCCESS);

            if (log.isTraceEnabled()) {
                message.result(ofNullable(result).map(Object::toString)
                        .orElse(""));
                log.trace(message.build());
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug(message.build());
                return;
            }

            log.info(message.build());
        } catch (final Exception exception) {
            log.error(getLoggingError(exception));
        }
    }

    @AfterThrowing(value = "controller()", throwing = "error")
    public void afterControllerThrow(final JoinPoint jp,
                                     final Exception error) {
        try {
            val message = getDefaultMessage(jp)
                    .layer(LogMessage.Layer.API)
                    .duration(getResponseDuration())
                    .params(getParams(jp))
                    .status(LogMessage.Status.FAILED)
                    .error(error.getMessage())
                    .stacktrace(ExceptionUtils.getStackTrace(error));

            log.error(message.build());
        } catch (final Exception exception) {
            log.error(getLoggingError(exception));
        }
    }

    private Long getResponseDuration() {
        return System.currentTimeMillis()
                - Long.parseLong(ThreadContext.get(RESPONSE_DURATION));
    }

}
