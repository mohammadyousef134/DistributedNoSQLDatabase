package com.example.nosql_database_management_system.exception;

import com.example.nosql_database_management_system.model.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public APIResponse handelResourceNotFound(ResourceNotFoundException e) {
        return new APIResponse(404, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public APIResponse handleGeneral(Exception ex) {
        return new APIResponse(500, "Something went wrong");
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public APIResponse handelForbidden(ForbiddenException e) {
        return new APIResponse(403, e.getMessage());
    }


}
