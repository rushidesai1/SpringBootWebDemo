package com.test.logging;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

//    @Pointcut("execution(* *.*(..))")
//    protected void allMethod() {
//    }
//
//    @Pointcut("execution(public * *(..))")
//    protected void loggingPublicOperation() {
//    }

//    @Pointcut("execution(* *.*(..))")
//    protected void loggingAllOperation() {
//    }

//    @Pointcut("within(org.learn.log..*)")
//    private void logAnyFunctionWithinResource() {
//    }

    //before -> Any resource annotated with @Controller annotation
    //and all method and function taking HttpServletRequest as first parameter
//    @Before("controller() && allMethod() && args(..,request)")
//    public void logBefore(JoinPoint joinPoint, HttpServletRequest request) {
    @Before("controller() || restController()")
    public void logBefore(JoinPoint joinPoint) {

        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.debug("Entering in Method :  " + joinPoint.getSignature().getName());
        log.debug("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.debug("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.debug("Target class : " + joinPoint.getTarget().getClass().getName());

//        if (null != request) {
//            log.debug("Start Header Section of request ");
//            log.debug("Method Type : " + request.getMethod());
//            Enumeration headerNames = request.getHeaderNames();
//            while (headerNames.hasMoreElements()) {
//                String headerName = headerNames.nextElement();
//                String headerValue = request.getHeader(headerName);
//                log.debug("Header Name: " + headerName + " Header Value : " + headerValue);
//            }
//            log.debug("Request Path info :" + request.getServletPath());
//            log.debug("End Header Section of request ");
//        }
    }

    //After -> All method within resource annotated with @Controller annotation
    // and return a  value
//    @AfterReturning(pointcut = "controller() && allMethod()", returning = "result")
    @AfterReturning(pointcut = "controller() || restController()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String returnValue = this.getValue(result);
//        log.debug("Method Return value : " + returnValue);
        log.info("Method Return value : " + returnValue);
    }

    //After -> Any method within resource annotated with @Controller annotation
    // throws an exception ...Log it
//    @AfterThrowing(pointcut = "controller() && allMethod()", throwing = "exception")
    @AfterThrowing(pointcut = "controller()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        log.error("Cause : " + exception.getCause());
    }

    //Around -> Any method within resource annotated with @Controller annotation
//    @Around("controller() && allMethod()")
    @Around("controller() && restController()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.debug("Method " + className + "." + methodName + " ()" + " execution time : "
                    + elapsedTime + " ms");
            log.info("Method " + className + "." + methodName + " ()" + " execution time : "
                    + elapsedTime + " ms");

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in "
                    + joinPoint.getSignature().getName() + "()");
            throw e;
        }
    }

    private String getValue(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }
}
