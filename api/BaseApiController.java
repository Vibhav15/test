package com.halodoc.batavia.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halodoc.batavia.exception.APIException;
import com.halodoc.batavia.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseApiController {
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";
    protected static final String DEFAULT_PAGE = "1";
    protected static final String DEFAULT_LIMIT = "10";

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

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map handleException(Exception e) {
        if (e instanceof APIException) {
            log.error(e.getMessage());
            Map data = new HashMap();
            ObjectMapper mapper = new ObjectMapper();
            try {
                data = mapper.readValue(e.getMessage(), HashMap.class);
            } catch (IOException ioe) {
                log.info("Error json", ioe);
            }

            return response(data, Boolean.FALSE);
        }

        log.info(e.getMessage());

        return response(e.getMessage(), Boolean.FALSE);
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public @ResponseBody Map handleNotFoundException(Exception ex, HttpServletResponse response) {
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("data", ex.getMessage());
        errorMap.put("status", "error");
        response.setStatus(HttpStatus.NOT_FOUND.value());

        return errorMap;
    }
}
