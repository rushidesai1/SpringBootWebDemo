package com.test.interceptors;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class LoggerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println(">>>>>Model : " + modelAndView.getModel());
        ContentCachingResponseWrapper responseCache = new ContentCachingResponseWrapper(response);
        System.out.println(">>>>>Model : " + getResponsePayload(responseCache));

        super.postHandle(request, responseCache, handler, modelAndView);
    }

    private HttpServletResponse loggingResponseWrapper(HttpServletResponse response) {
        return new HttpServletResponseWrapper(response) {
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return new DelegatingServletOutputStream(
                        new TeeOutputStream(super.getOutputStream(), loggingOutputStream())
                );
            }
        };
    }

    private OutputStream loggingOutputStream() {
        return System.out;
    }


    private String getResponsePayload(ContentCachingResponseWrapper response) {
//        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        byte[] buf = response.getContentAsByteArray();
        String contentAsString = getContentAsString(buf, this.maxPayloadLength, response.getCharacterEncoding());
        if (contentAsString != null && !contentAsString.isEmpty()) {
            return contentAsString;
        }
//        if (wrapper != null) {
//
//            byte[] buf = wrapper.getContentAsByteArray();
//
//            if (buf.length > 0) {
//                int length = Math.min(buf.length, 5120);
//                try {
//                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
//                    return new String(buf, wrapper.getCharacterEncoding());
//                } catch (UnsupportedEncodingException ex) {
//                     NOOP
//                    ex.printStackTrace();
//                }
//            }
//        }
        return "[unknown]";
    }

    private int maxPayloadLength = 1000;

    private String getContentAsString(byte[] buf, int maxLength, String charsetName) {
        if (buf == null || buf.length == 0) return "";
        int length = Math.min(buf.length, this.maxPayloadLength);
        try {
            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }
}
