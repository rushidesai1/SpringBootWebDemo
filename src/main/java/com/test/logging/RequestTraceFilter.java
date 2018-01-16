package com.test.logging;

import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

//@Component("RequestTraceFilter")
@Component
public class RequestTraceFilter extends WebRequestTraceFilter {


    /**
     * Create a new {@link WebRequestTraceFilter} instance.
     *
     * @param repository the trace repository
     * @param properties the trace properties
     */
    public RequestTraceFilter(TraceRepository repository, TraceProperties properties) {
        super(repository, properties);
    }

    @Override
    protected Map<String, Object> getTrace(HttpServletRequest request) {
        Map<String, Object> trace = super.getTrace(request);
        trace.put("Before", "Element");
        return trace;
    }

    @Override
    protected void enhanceTrace(Map<String, Object> trace, HttpServletResponse response) {
        super.enhanceTrace(trace, response);
        //Add more elements
        trace.put("After", "Element");
    }
}
