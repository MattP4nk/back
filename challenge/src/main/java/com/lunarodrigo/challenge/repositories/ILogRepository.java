package com.lunarodrigo.challenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lunarodrigo.challenge.models.LogModel;

@Repository
public interface ILogRepository extends JpaRepository<LogModel, Integer> {

}
