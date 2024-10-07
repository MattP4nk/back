package com.lunarodrigo.challenge.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Changes_Log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String admin;

    @Column
    private LocalDateTime date;

    @Column
    private String area;

    @Column
    private String command;

    @Column
    private String target;
}
