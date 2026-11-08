package com.personal.ims.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ProblemCode {

        // Use ENUMs with default fields
        VALIDATION_ERROR(
                        "validation-error",
                        "Request validation failed",
                        HttpStatus.BAD_REQUEST,
                        "One or more request fields are invalid."),

        INVALID_CREDENTIALS(
                        "invalid-credentials",
                        "Authentication failed",
                        HttpStatus.UNAUTHORIZED,
                        "The supplied credentials are invalid."),

        UNAUTHENTICATED(
                        "unauthenticated",
                        "Authentication required",
                        HttpStatus.UNAUTHORIZED,
                        "Valid authentication is required to access this resource."),

        ACCESS_DENIED(
                        "access-denied",
                        "Access denied",
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to perform this operation"),

        RESOURCE_NOT_FOUND(
                        "resource-not-found",
                        "Resource not found",
                        HttpStatus.NOT_FOUND,
                        "The requested resource could not be found"),

        INVALID_REFERENCE(
                        "invalid-reference",
                        "Invalid reference",
                        HttpStatus.BAD_REQUEST,
                        "One or more referenced records do not exist."),

        CONFLICT(
                        "conflict",
                        "Request conflict",
                        HttpStatus.CONFLICT,
                        "The request conflicts with the current resource state."),

        INTERNAL_ERROR(
                        "internal-error",
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "The server encountered an unexpected condition.");

        // ENUMs locked properties
        private final String slug;
        private final String title;
        private final HttpStatus status;
        private final String defaultDetail;

        // Initializes this problem code's slug, title, HTTP status, and default detail.
        ProblemCode(String slug, String title, HttpStatus status, String detail) {
                this.slug = slug;
                this.title = title;
                this.status = status;
                this.defaultDetail = detail;
        }

}
