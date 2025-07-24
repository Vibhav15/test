package com.halodoc.batavia.controller.api;

import com.halodoc.batavia.entity.common.ErrorResponse;
import com.halodoc.batavia.exception.HalodocWebException;
import com.newrelic.api.agent.NewRelic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HalodocBaseApiController {
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";
    protected static final String DEFAULT_PAGE = "1";
    protected static final String DEFAULT_LIMIT = "10";
    protected static final String DEFAULT_STRING = "";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    protected Map response(Object data, Boolean isSuccess) {
        Map<String, Object> map = new HashMap<>();
        if (isSuccess) {
            map.put("status", STATUS_SUCCESS);
        } else {
            map.put("status", STATUS_ERROR);
        }

        map.put("data", data);

        return map;
    }

    protected Map response(Object data) {
        return response(data, Boolean.TRUE);
    }

    @ExceptionHandler(HalodocWebException.class)
    public ResponseEntity<ErrorResponse> handleHalodocException(HalodocWebException he) {
        if (he.isExpected()) {
            NewRelic.noticeError(he, true);
        }
        return new ResponseEntity<>(ErrorResponse.fromHalodocWebException(he),
                HttpStatus.valueOf(he.getStatusCode()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleHalodocException(AccessDeniedException ex) {
        ErrorResponse errorMessage = new ErrorResponse();
        errorMessage.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorMessage.setMessage("Access denied");
        errorMessage.setCode(String.valueOf(HttpStatus.FORBIDDEN.value()));
        return new ResponseEntity<>(errorMessage,
                HttpStatus.valueOf(errorMessage.getStatusCode()));
    }
}
