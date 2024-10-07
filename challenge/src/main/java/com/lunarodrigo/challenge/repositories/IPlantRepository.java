package com.lunarodrigo.challenge.repositories;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lunarodrigo.challenge.models.PlantModel;

@Repository
public interface IPlantRepository extends JpaRepository<PlantModel, Integer> {
    PlantModel findByName(String name) throws NotFoundException;
}