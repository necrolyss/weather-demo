package lv.dev.mintos.weatherdemo.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ApplicationExceptionHandlerTest {

    @Mock
    private Locale locale;

    private ApplicationExceptionHandler applicationExceptionHandler;

    @BeforeEach
    void setUp() {
        AbstractResourceBasedMessageSource bundleMessageSource =
                new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setUseCodeAsDefaultMessage(true);
        applicationExceptionHandler = new ApplicationExceptionHandler(bundleMessageSource);
    }

    @Test
    void handleRestException() {
        RestException restException = new RestException("");
        ResponseEntity<ApiResponse> responseEntity = applicationExceptionHandler.handleRestException(restException, locale);

        assertThat(responseEntity.getBody())
                .returns(Boolean.FALSE, ApiResponse::success)
                .returns(null, ApiResponse::body)
                .returns("exception.generic-rest", ApiResponse::errorMessage);
    }

    @Test
    void handleExceptions() {
        Exception genericException = new Exception("");
        ResponseEntity<ApiResponse> responseEntity = applicationExceptionHandler.handleExceptions(genericException, locale);

        assertThat(responseEntity.getBody())
                .returns(Boolean.FALSE, ApiResponse::success)
                .returns(null, ApiResponse::body)
                .returns("exception.unidentified", ApiResponse::errorMessage);
    }

}