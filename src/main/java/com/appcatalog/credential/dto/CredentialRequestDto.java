package com.appcatalog.credential.dto;

import com.appcatalog.credential.Credential;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRequestDto {

  @NotBlank(message = "Username cannot be empty")
  private String username;

  @NotBlank(message = "Password cannot be empty")
  private String password;

  public Credential toEntity() {

    return Credential.builder().username(this.username).password(this.password).build();
  }
}
