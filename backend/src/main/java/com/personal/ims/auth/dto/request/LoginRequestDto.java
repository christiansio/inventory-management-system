package com.personal.ims.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

        @NotBlank @Email String email,

        @NotBlank @Size(max = 64, min = 8) String password) {

}
