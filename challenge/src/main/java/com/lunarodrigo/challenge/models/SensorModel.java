package com.lunarodrigo.challenge.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sensors")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SensorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String value;

    @Column
    private Integer totalReadings;

    @Column
    private Integer totalWarnings;

    @Column
    private Integer totalRedAlerts;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    @JsonBackReference
    private PlantModel plant;
}
