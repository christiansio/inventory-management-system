package com.personal.ims.core.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ProblemCode problemCode;

    // constructor
    public AppException(ProblemCode problemCode, String safeDetail, Throwable cause) {
        super(safeDetail, cause);
        this.problemCode = problemCode;
    }

    // Delegates to the three argument constructor with no cause
    public AppException(ProblemCode problemCode, String safeDetail) {
        this(problemCode, safeDetail, null);
    }

    // Delegates with no custom detail, or cause
    public AppException(ProblemCode problemCode) {
        this(problemCode, null, null);
    }

}
