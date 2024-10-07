package com.lunarodrigo.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lunarodrigo.challenge.dtos.LogDTO;
import com.lunarodrigo.challenge.dtos.RequestDTO;
import com.lunarodrigo.challenge.dtos.ResponseDTO;
import com.lunarodrigo.challenge.security.jwt.JWTService;
import com.lunarodrigo.challenge.services.LogServices;
import com.lunarodrigo.challenge.services.PlantService;
import com.lunarodrigo.challenge.services.UserService;

@RestController
@RequestMapping("/communiations")
public class Controller {

    @Autowired
    PlantService plantService;

    @Autowired
    UserService userService;

    @Autowired
    JWTService jwtService;

    @Autowired
    LogServices logServices;

    @GetMapping()
    public String Readme() {
        return "Readme!? I haven't written it yet!";
    }

    @PostMapping()
    public ResponseEntity Communications(@RequestBody RequestDTO requestDTO) {

        boolean willLog = false;
        ResponseDTO responseDTO = new ResponseDTO();
        switch (requestDTO.getArea()) {
            case "users" -> {
                switch (requestDTO.getCommand()) {
                    case "login" -> {
                        responseDTO = userService.login(requestDTO.getCredentials());
                    }
                    case "register" -> {
                        responseDTO = userService.register((requestDTO.getRegInfo()));
                    }
                    case "delete" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = userService.deleteUser(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "upgrade" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = userService.upgradeUser(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }

                    }
                    case "downgrade" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = userService.downgradeAdmin(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "passwordChange" -> {
                        try {
                            jwtService.validateToken(requestDTO.getKey());
                            responseDTO = userService.changePassword(requestDTO.getNewCredentials());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    default -> {
                        responseDTO.setStatus("BAD_REQUEST. Wrong command.");
                    }
                }
            }
            case "logs" -> {
                try {
                    jwtService.validateAdminToken(requestDTO.getKey());
                    responseDTO = logServices.readLogs();
                } catch (Exception e) {
                    responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                }
            }

            case "plants_sensors" -> {
                switch (requestDTO.getCommand()) {
                    case "createPlant" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = plantService.createPlant(requestDTO.getPlant());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "getPlants" -> {
                        try {
                            jwtService.validateToken(requestDTO.getKey());
                            responseDTO = plantService.findAllPlants();
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "deletePlant" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = plantService.deletePlant(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "addSensor" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = plantService.addSensor(requestDTO.getSensor());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "deleteSensor" -> {
                        willLog = true;
                        try {
                            jwtService.validateAdminToken(requestDTO.getKey());
                            responseDTO = plantService.deleteSensor(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "getSensors" -> {
                        try {
                            jwtService.validateToken(requestDTO.getKey());
                            responseDTO = plantService.findAllSensors();
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }
                    }
                    case "updateValues" -> {
                        try {
                            jwtService.validateToken(requestDTO.getKey());
                            responseDTO = plantService.updateValues(requestDTO.getTarget());
                        } catch (Exception e) {
                            responseDTO.setStatus("ILLEGAL. You have no access to this function.");
                        }

                    }
                    default -> {
                        responseDTO.setStatus("BAD_REQUEST. Wrong command.");
                    }
                }
            }
            default -> {
                responseDTO.setStatus("BAD REQUEST. Invalid Area");
            }
        }
        if (responseDTO.getStatus().equals("OK")) {
            if (willLog) {
                String targetName;
                targetName = switch (requestDTO.getCommand()) {
                    case "createPlant" -> requestDTO.getPlant().getName();
                    case "addSensor" -> responseDTO.getToLog();
                    default -> requestDTO.getTarget();
                };
                LogDTO log = LogDTO.builder()
                        .admin(jwtService.userFromJWT(requestDTO.getKey()))
                        .area(requestDTO.getArea())
                        .command(requestDTO.getCommand())
                        .target(targetName)
                        .build();
                logServices.createLog(log);
            }
            return new ResponseEntity(responseDTO, HttpStatus.OK);
        } else if (responseDTO.getStatus().startsWith("ILLEGAL.")) {
            return new ResponseEntity(responseDTO, HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
        }

    }

}
