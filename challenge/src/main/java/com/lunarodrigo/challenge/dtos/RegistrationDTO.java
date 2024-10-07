package com.lunarodrigo.challenge.dtos;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class RegistrationDTO {
    public String email;
    public String username;
    public String password;
}
