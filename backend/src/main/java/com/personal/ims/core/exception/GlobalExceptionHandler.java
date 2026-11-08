package com.personal.ims.core.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ProblemDetailFactory problemDetailFactory;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException e) {

        ProblemDetail detail = problemDetailFactory
                .createProblemDetail(e.getProblemCode(), e.getMessage());

        return ResponseEntity
                .status(detail.getStatus())
                .body(detail);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception e) {

        ProblemDetail detail = problemDetailFactory
                .createProblemDetail(ProblemCode.INTERNAL_ERROR, null);

        log.error("Unhandled exception, occurenceId={}", detail.getInstance(), e);

        return ResponseEntity
                .status(detail.getStatus())
                .body(detail);
    }

}
