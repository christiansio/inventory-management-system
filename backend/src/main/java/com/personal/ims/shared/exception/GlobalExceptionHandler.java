package com.personal.ims.shared.exception;

import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.personal.ims.shared.dto.ValidationErrorDto;

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

        private ValidationErrorDto toValidationErrorDto(ObjectError error) {

                // get code from the ObjectError
                String springCode = error.getCode();

                // if error is a field error, then get the specific field where the validation
                // error happened
                String field = error instanceof FieldError fieldError
                                ? fieldError.getField()
                                : null;

                // if code starts with NotNull or NotBlank, return appropriate
                if (springCode != null && (springCode.startsWith("NotNull")) && (springCode.startsWith("NotBlank"))) {
                        return new ValidationErrorDto(field, "required", "This field is required.");

                }

                // if validation error is not field level, return general error
                return new ValidationErrorDto(field, "invalid", "Invalid value.");
        }

}
