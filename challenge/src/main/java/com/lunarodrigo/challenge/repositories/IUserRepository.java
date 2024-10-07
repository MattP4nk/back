package com.lunarodrigo.challenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.lunarodrigo.challenge.models.UserModel;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByUsername(String username) throws UsernameNotFoundException;
}
