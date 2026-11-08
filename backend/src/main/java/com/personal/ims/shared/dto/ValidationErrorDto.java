package com.personal.ims.shared.dto;

public record ValidationErrorDto(
        String field,
        String code,
        String message) {

}
