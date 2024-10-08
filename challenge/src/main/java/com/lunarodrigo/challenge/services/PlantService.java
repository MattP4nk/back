package com.lunarodrigo.challenge.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.lunarodrigo.challenge.dtos.PlantDTO;
import com.lunarodrigo.challenge.dtos.ResponseDTO;
import com.lunarodrigo.challenge.dtos.SensorDTO;
import com.lunarodrigo.challenge.models.PlantModel;
import com.lunarodrigo.challenge.models.SensorModel;
import com.lunarodrigo.challenge.repositories.IPlantRepository;
import com.lunarodrigo.challenge.repositories.ISensorRepository;

@Service
public class PlantService {

    ResponseDTO responseDTO = new ResponseDTO();

    @Autowired
    IPlantRepository plantRepository;

    @Autowired
    ISensorRepository sensorRepository;

    public ResponseDTO findAllPlants() {
        List<PlantModel> plants = plantRepository.findAll();
        responseDTO.setStatus("OK");
        responseDTO.setPack(plants);
        return responseDTO;
    }

    public ResponseDTO findAllSensors() {
        List<SensorModel> sensors = sensorRepository.findAll();
        responseDTO.setStatus("OK");
        responseDTO.setPack(sensors);
        return responseDTO;
    }

    public ResponseDTO createPlant(PlantDTO plantDto) {
        PlantModel plantModel = PlantModel.builder()
                .name(plantDto.getName())
                .country(plantDto.getCountry())
                .totalReadings(0)
                .totalWarnings(0)
                .totalRedAlerts(0)
                .build();
        try {
            plantRepository.save(plantModel);
            responseDTO.setStatus("OK");
            responseDTO.setPack(plantModel);
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("Duplicate entry")) {
                responseDTO.setStatus("FAILED. Name already in use.");
            } else {
                responseDTO.setStatus("Unknown Error");
            }
        }
        return responseDTO;
    }

    public ResponseDTO deletePlant(String target) {
        try {
            PlantModel plantModel = plantRepository.findByName(target);
            if (plantModel == null) {
                throw new NotFoundException();
            }
            plantRepository.delete(plantModel);
            responseDTO.setStatus("OK");
        } catch (NotFoundException e) {
            responseDTO.setStatus("FAILED. " + target + " not found in plants list.");
        }
        return responseDTO;
    }

    public ResponseDTO addSensor(SensorDTO sensorDTO) {
        try {
            PlantModel plant = plantRepository.findById(sensorDTO.getPlantId()).orElseThrow();
            if (plant == null) {
                throw new Exception();
            }
            String sensorName = plant.getName() + " Sensor " + (plant.getSensorList().size() + 1) + " "
                    + sensorDTO.getType();
            SensorModel sensor = SensorModel.builder()
                    .type(sensorDTO.getType())
                    .value("OK")
                    .name(sensorName)
                    .totalReadings(0)
                    .totalWarnings(0)
                    .totalRedAlerts(0)
                    .plant(plant)
                    .build();
            sensorRepository.save(sensor);
            responseDTO.setStatus("OK");
            responseDTO.setToLog(sensorName);
            responseDTO.setPack(plant);
        } catch (Exception e) {
            responseDTO.setStatus("FAILED. Incorrect plant id");
        }
        return responseDTO;
    }

    public ResponseDTO deleteSensor(String target) {
        try {
            SensorModel sensor = sensorRepository.findByName(target);
            if (sensor == null) {
                throw new NotFoundException();
            }
            sensorRepository.delete(sensor);
            responseDTO.setStatus("OK");
        } catch (NotFoundException e) {
            responseDTO.setStatus("FAILED. " + target + " is not found in plants list.");
        }
        return responseDTO;
    }

    /**
     * Este puede ser el metodo mas "enrrredado" de este servicio. Tiene dos
     * implementaciones que dependen de "target". La primera es "all" que busca
     * /todos los sensores y la segunda es el nombre de la planta que buscaria solo
     * los sensores de dicha planta.
     * Tanto los sensores como las plantas llevan sus propios conteos de mediciones.
     * Los sensores llevan sus propios conteos mientras que las plantas llevan los
     * conteos de la suma de sensores. Esto lo hice asi para despues poder sumar
     * mediciones globalmente segun el tipo de sensor.
     * 
     * @param target: string - Puede ser "all" o el nombre de una planta.
     * @return responseDto
     * @throws NotFoundException
     */
    public ResponseDTO updateValues(String target) throws NotFoundException {
        if ("all".equals(target)) {
            List<PlantModel> plantModels = new ArrayList<>();
            try {
                List<SensorModel> sensors = sensorRepository.findAll();
                for (SensorModel sensor : sensors) {
                    Integer index = null;
                    PlantModel plant = sensor.getPlant();
                    /**
                     * Verificamos si plant esta en el array Plants de estarlo vamos a trabajar con
                     * el elemento del array (seria -en teoria- el mas actualizado). Usamos el la
                     * variable index para despues poder guardarlo en el mismo lugar.
                     */
                    if (plantModels.contains(plant)) {
                        index = plantModels.indexOf(plant);
                        plant = plantModels.get(index);
                    }
                    sensor.setValue(getNewValue());
                    switch (sensor.getValue()) {

                        case "OK" -> {
                            sensor.setTotalReadings(sensor.getTotalReadings() + 1);
                            plant.setTotalReadings(plant.getTotalReadings() + 1);
                        }
                        case "Warning" -> {
                            sensor.setTotalWarnings(sensor.getTotalWarnings() + 1);
                            plant.setTotalWarnings(plant.getTotalWarnings() + 1);
                        }
                        case "Danger" -> {
                            sensor.setTotalRedAlerts(sensor.getTotalRedAlerts() + 1);
                            plant.setTotalRedAlerts(plant.getTotalRedAlerts() + 1);
                        }
                    }
                    /**
                     * Si index sigue siendo null significa que nunca fue modificado, ergo, este
                     * "plant" no esta contenido en plantModels. De lo contrario "actualizamos" el
                     * objeto usando el index.
                     */
                    if (index == null) {
                        plantModels.add(plant);
                    } else {
                        plantModels.set(index, plant);
                    }
                }
                /**
                 * Una vez actualizados los valores guardamos en la base de datos.
                 */
                plantRepository.saveAll(plantModels);
                sensorRepository.saveAll(sensors);
                responseDTO.setStatus("OK");
                responseDTO.setPack(plantRepository.findAll());
            } catch (Exception e) {
                responseDTO.setStatus("FAILED");
                responseDTO.setPack(e.getLocalizedMessage());
            }
        } else {
            /**
             * Esta rama es parecida a la anterior pero mas simple al trabajar solamente con
             * una planta.
             */
            try {
                PlantModel plant = plantRepository.findByName(target);
                List<SensorModel> sensors = plant.getSensorList();
                for (SensorModel sensor : sensors) {
                    sensor.setValue(getNewValue());
                    switch (sensor.getValue()) {
                        case "OK" -> {
                            sensor.setTotalReadings(sensor.getTotalReadings() + 1);
                            plant.setTotalReadings(plant.getTotalReadings() + 1);
                        }
                        case "Warning" -> {
                            sensor.setTotalWarnings(sensor.getTotalWarnings() + 1);
                            plant.setTotalWarnings(plant.getTotalWarnings() + 1);
                        }
                        case "Danger" -> {
                            sensor.setTotalRedAlerts(sensor.getTotalRedAlerts() + 1);
                            plant.setTotalRedAlerts(plant.getTotalRedAlerts() + 1);
                        }
                    }
                }
                plantRepository.save(plant);
                sensorRepository.saveAll(sensors);
                responseDTO.setStatus("OK");
                responseDTO.setPack(plantRepository.findAll());
            } catch (NotFoundException e) {
                responseDTO.setStatus("FAILED. Wrong plant Id");
                responseDTO.setPack(e.getLocalizedMessage());
            }
        }
        return responseDTO;
    }

    /**
     * Este es el metodo que genera aleatoriamente las mediciones para updateValues
     */
    private String getNewValue() {
        Random random = new Random();
        Integer range = random.nextInt(100) + 1;
        if (range < 10) {
            return "Danger";
        } else if (range > 9 && range < 41) {
            return "Warning";
        } else {
            return "OK";
        }
    }

}
