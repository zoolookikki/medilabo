package com.medilabo.patient_service.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * For logging HTTP request details.
 *
 * <p>This filter logs the HTTP method and full URL of every incoming request, including query parameters if present.</p>
 *
 */
@Component
@Log4j2
public class LoggingFilter implements Filter {

    /**
     * Logs the details of the incoming HTTP request and continues the filter chain.
     *
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        // Retrieve the base URL
        String url = req.getRequestURL().toString();

        // Retrieve query parameters, if any
        String queryString = req.getQueryString();

        // Construct the full URL with query parameters
        String fullUrl = (queryString != null) ? url + "?" + queryString : url;

        // Get the HTTP method (GET, POST...)
        String method = req.getMethod();

        // Log the HTTP method and full URL
        log.info("Request received : [{}] {}", method, fullUrl);

        // Continue the filter chain
        chain.doFilter(request, response);  
    }
}
