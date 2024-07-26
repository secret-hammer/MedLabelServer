package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    // Additional custom queries can be defined here
}