package com.lunarodrigo.challenge.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
    public String username;
    public String password;
}
