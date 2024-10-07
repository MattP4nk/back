package com.lunarodrigo.challenge.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plants")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlantModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    @Column
    private String country;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plant")
    @JsonManagedReference
    private List<SensorModel> sensorList;

    @Column
    private Integer totalReadings;

    @Column
    private Integer totalWarnings;

    @Column
    private Integer totalRedAlerts;

    public void addSensor(SensorModel sensor) {
        this.sensorList.add(sensor);
    }
}
