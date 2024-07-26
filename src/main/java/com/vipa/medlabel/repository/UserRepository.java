package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Additional custom queries can be defined here
}