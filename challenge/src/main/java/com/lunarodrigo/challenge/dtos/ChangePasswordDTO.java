package com.lunarodrigo.challenge.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDTO {
    public String username;
    public String oldPassword;
    public String newPassword;
}
