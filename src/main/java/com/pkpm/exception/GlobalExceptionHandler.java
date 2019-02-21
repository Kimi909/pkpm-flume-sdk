package com.pkpm.exception;

import com.pkpm.util.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ResultObject handleBusinessValidationException(BusinessException e) {
        logger.error(e.getMessage(), e);
        return ResultObject.failure(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultObject handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResultObject.failure(e.getMessage());
    }
}
