package com.dws.challenge.exception;

import com.dws.challenge.pojos.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(DuplicateAccountIdException.class)
    public ResponseEntity<Response> handleDuplicateAccountIdException(DuplicateAccountIdException exp){
        return new ResponseEntity<>(new Response(509, exp.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Response> handleInvalidException(InvalidRequestException exp){
        return new ResponseEntity<>(new Response(400, exp.getMessage()),HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException methodNotAllowed){
        return new ResponseEntity<>(new Response(405, methodNotAllowed.getMessage()),HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler(InvalidMediaTypeException.class)
    public ResponseEntity<Response> handleInvalidMediaTypeException(InvalidMediaTypeException exp){
        return new ResponseEntity<>(new Response(415, exp.getMessage()),HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
