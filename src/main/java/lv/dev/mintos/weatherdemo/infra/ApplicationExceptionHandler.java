package lv.dev.mintos.weatherdemo.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private static final String UNIDENTIFIED_ERROR = "exception.unidentified";
    private static final String GENERIC_REST_ERROR = "exception.generic-rest";

    private static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    private final MessageSource messageSource;

    @Autowired
    public ApplicationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ApiResponse> handleRestException(RestException ex, Locale locale) {
        String causeTranslation = messageSource.getMessage(ex.getMessageId(), ex.getParams(), locale);
        String errorMessage = messageSource.getMessage(GENERIC_REST_ERROR, new String[]{causeTranslation}, locale);
        return new ResponseEntity<>(new ApiResponse(errorMessage), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleExceptions(Exception ex, Locale locale) {
        logger.error("Unidentified exception occurred", ex);
        String errorMessage = messageSource.getMessage(UNIDENTIFIED_ERROR, null, locale);
        return new ResponseEntity<>(new ApiResponse(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
