package com.halodoc.batavia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halodoc.batavia.entity.common.ErrorResponse;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.config.ConfigClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component( "restAuthenticationEntryPoint" )
@Slf4j
public class RestAuthenticationEntryPoint extends SimpleUrlAuthenticationFailureHandler
        implements AuthenticationEntryPoint {

    @Autowired
    private ConfigClient<BataviaAppConfiguration> configClient;

    public RestAuthenticationEntryPoint() {
     super();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String requestUri = request.getRequestURI().toLowerCase();
        //Rest end point
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setMessage("Unauthorized");

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, this.getRedirectUrl() + "/403");
    }

    private String getRedirectUrl(){
        return this.configClient.getAppConfig().getGoogleSsoConfiguration().getRedirectUrl();
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String requestUri = request.getRequestURI().toLowerCase();
        if (requestUri.startsWith(request.getContextPath() + "/login")
                || requestUri.endsWith(".js")
                || requestUri.endsWith(".css")
                || requestUri.endsWith(".js.map")
                || requestUri.endsWith(".eot")
                || requestUri.endsWith(".svg")
                || requestUri.endsWith(".ttf")
                || requestUri.endsWith(".woff")
                || requestUri.endsWith(".woff2")
                || requestUri.endsWith(".jpg")
                || requestUri.endsWith(".jpeg")
                || requestUri.endsWith(".png")
                || requestUri.endsWith(".ico")) {
            //Ignore
        } else {
            //Rest end point
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            errorResponse.setMessage("Unauthorized");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writer().writeValue(response.getOutputStream(), errorResponse);
        }
    }

}
