package com.lunarodrigo.challenge.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunarodrigo.challenge.dtos.LogDTO;
import com.lunarodrigo.challenge.dtos.ResponseDTO;
import com.lunarodrigo.challenge.models.LogModel;
import com.lunarodrigo.challenge.repositories.ILogRepository;

@Service
public class LogServices {

    ResponseDTO response = new ResponseDTO();

    @Autowired
    ILogRepository logRepository;

    public void createLog(LogDTO logDTO) {
        LogModel log = LogModel.builder()
                .admin(logDTO.getAdmin())
                .area(logDTO.getArea())
                .command(logDTO.getCommand())
                .target(logDTO.getTarget())
                .date(LocalDateTime.now())
                .build();
        logRepository.save(log);
    }

    public ResponseDTO readLogs() {
        response.setStatus("OK");
        response.setPack(logRepository.findAll());
        return response;
    }

}
