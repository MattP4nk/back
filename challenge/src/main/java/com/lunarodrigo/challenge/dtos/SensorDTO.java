package com.lunarodrigo.challenge.dtos;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SensorDTO {
    public String type;
    public Integer plantId;
}
