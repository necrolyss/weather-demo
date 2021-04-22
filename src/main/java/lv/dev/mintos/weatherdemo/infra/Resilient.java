package lv.dev.mintos.weatherdemo.infra;

import an.awesome.pipelinr.Command;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class Resilient implements Command.Middleware {

    private static final String DEFAULT_CONFIG_INSTANCE = "DEFAULT";

    private final RetryRegistry retryRegistry;

    public Resilient(@Value("${app.retry-strategy.max-attempts}") Integer maxAttempts,
                     @Value("${app.retry-strategy.max-wait-duration-msec}") Integer maxWaitDuration) {
        retryRegistry = initRetryRegistry(maxAttempts, maxWaitDuration);
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        return fetchResilient(next::invoke);
    }

    private <R> R fetchResilient(CheckedFunction0<R> loader) {
        Retry retry = retryRegistry.retry(DEFAULT_CONFIG_INSTANCE);
        CheckedFunction0<R> retryableSupplier = Retry.decorateCheckedSupplier(retry, loader);
        return Try.of(retryableSupplier).recover(this::recoverIfNotBusinessException).get();
    }

    private RetryRegistry initRetryRegistry(Integer maxAttempts, Integer maxWaitDuration) {
        return RetryRegistry.of(
                RetryConfig.custom()
                        .maxAttempts(maxAttempts)
                        .waitDuration(Duration.ofMillis(maxWaitDuration))
                        .retryExceptions(IOException.class, TimeoutException.class)
                        .failAfterMaxAttempts(true)
                        .build()
        );
    }

    private <R> R recoverIfNotBusinessException(Throwable throwable) {
        if (throwable instanceof RestException restException) {
            throw restException;
        }
        return null;
    }
}
