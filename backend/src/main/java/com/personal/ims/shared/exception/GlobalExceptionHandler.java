package com.personal.ims.shared.exception;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                // Get all the validation errors from the exception and use the helper method to
                // form detail extension format
                List<ValidationErrorDto> errors = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(this::toValidationErrorDto)
                                .toList();

                // Create a problem detail object that uses ProblemCode validation error enum w/
                // properties
                ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(
                                ProblemCode.VALIDATION_ERROR, null);

                // add extension error extension to the detail
                problemDetail.setProperty("error", errors);

                return handleExceptionInternal(
                                ex,
                                problemDetail,
                                headers,
                                HttpStatus.BAD_REQUEST,
                                request);
        }

        private ValidationErrorDto toValidationErrorDto(ObjectError error) {

                // get code from the ObjectError
                String errorCode = error.getCode();

                // if error is a field error, then get the specific field where the validation
                // error happened
                String field = error instanceof FieldError fieldError
                                ? fieldError.getField()
                                : null;

                // if code starts with NotNull or NotBlank, return appropriate
                if (errorCode != null && (errorCode.startsWith("NotNull")) && (errorCode.startsWith("NotBlank"))) {
                        return new ValidationErrorDto(field, "required", "This field is required.");

                }

                // if validation error is not field level, return general error
                return new ValidationErrorDto(field, "invalid", "Invalid value.");
        }

}
