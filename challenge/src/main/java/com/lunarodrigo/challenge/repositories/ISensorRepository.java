package com.lunarodrigo.challenge.repositories;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lunarodrigo.challenge.models.SensorModel;

@Repository
public interface ISensorRepository extends JpaRepository<SensorModel, Integer> {
    SensorModel findByName(String name) throws NotFoundException;
}
