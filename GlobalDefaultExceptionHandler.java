package com.halodoc.batavia.controller;

import com.halodoc.batavia.entity.common.ErrorResponse;
import com.halodoc.batavia.exception.HalodocWebException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = {"com.halodoc.batavia"})
@Order(32000)
@Slf4j
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private  ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorResponse errorResponse){
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(errorResponse, headers,
                HttpStatus.valueOf(errorResponse.getStatusCode()));
    }


    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Internal Error", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(status.value());
        errorResponse.setMessage(ex.getMessage());
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Throwable t) {

        log.error("Unknown error", t);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage(t.getMessage());
        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException rae) {
        log.error("ResourceAccessException", rae);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage(rae.getMessage());
        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(HalodocWebException.class)
    public ResponseEntity<ErrorResponse> handleHalodocException(HalodocWebException he) {
        log.error("HalodocWebException", he);
        return createErrorResponseEntity(ErrorResponse.fromHalodocWebException(he));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage("Access denied");
        errorResponse.setCode(String.valueOf(HttpStatus.FORBIDDEN.value()));
        return createErrorResponseEntity(errorResponse);
    }


    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleRestClientResponseException(RestClientResponseException ex) {
        log.error("RestClientResponseException", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(ex.getRawStatusCode());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(String.valueOf(ex.getRawStatusCode()));
        return createErrorResponseEntity(errorResponse);
    }

}
