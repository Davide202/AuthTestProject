package com.example.test.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


@Log4j2
//@Component
public class CustomLoggingFilter implements Filter {

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {

            logRequest(httpRequest);

            ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

            chain.doFilter(request, responseWrapper);

            logResponse(httpRequest, responseWrapper);
        } else {
            chain.doFilter(request, response);
        }
    }


    private void logRequest(HttpServletRequest request) {
        if (request.getRequestURI().contains("swagger")) return;
        log.info("Incoming Request: [{}] {}", request.getMethod(), request.getRequestURI());
        request.getHeaderNames().asIterator().forEachRemaining(header ->
                log.info("Header: {} = {}", header, request.getHeader(header))
        );
    }

    private void logResponse(
            HttpServletRequest request,
            ResponseWrapper responseWrapper
    ) throws IOException {
        if (request.getRequestURI().contains("swagger")) return;
        log.info("Outgoing Response for [{}] {}: Status = {}",
                request.getMethod(), request.getRequestURI(), responseWrapper.getStatus());
        log.info("Response Body: {}", responseWrapper.getBodyAsString());
    }

    public static class ResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private final PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }

                @Override
                public void write(int b) {
                    outputStream.write(b);
                }
            };
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            super.flushBuffer();
            writer.flush();
        }

        public String getBodyAsString() {
            writer.flush();
            return outputStream.toString();
        }
    }
}
