package com.lunarodrigo.challenge.dtos;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogDTO {
    private String admin;
    private Date date;
    private String area;
    private String command;
    private String target;
}
