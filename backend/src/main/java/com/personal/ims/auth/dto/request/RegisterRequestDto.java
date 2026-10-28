package com.personal.ims.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(

        @Email @NotBlank String email,

        @NotBlank String firstName,

        @NotBlank String lastName,

        @NotNull Long roleId,

        @NotNull Long locationId) {

}
