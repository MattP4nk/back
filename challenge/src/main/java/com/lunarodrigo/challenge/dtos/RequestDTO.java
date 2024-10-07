package com.lunarodrigo.challenge.dtos;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class RequestDTO {
    private String area;
    private String command;
    private String target;
    private RegistrationDTO regInfo;
    private PlantDTO plant;
    private SensorDTO sensor;
    private LoginDTO credentials;
    private ChangePasswordDTO newCredentials;
    private String key;
}
