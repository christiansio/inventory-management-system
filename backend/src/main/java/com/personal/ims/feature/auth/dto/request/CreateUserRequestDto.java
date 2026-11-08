package com.personal.ims.feature.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDto(

        @Email @NotBlank String email,

        @NotNull Long roleId,

        @NotNull Long locationId) {

}
