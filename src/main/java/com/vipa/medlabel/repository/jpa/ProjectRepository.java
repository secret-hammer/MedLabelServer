package com.vipa.medlabel.repository.jpa;

import com.vipa.medlabel.model.Project;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {

    Optional<Project> findByProjectId(int projectId);
    // Additional custom queries can be defined here
}