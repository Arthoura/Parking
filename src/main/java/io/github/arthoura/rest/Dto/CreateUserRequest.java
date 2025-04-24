package io.github.arthoura.rest.Dto;

import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @NotBlank(message = "birthday is required")
    private String birthday;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "cpf is required")
    private String cpf;

    @NotBlank(message = "email is required")
    private String email;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
