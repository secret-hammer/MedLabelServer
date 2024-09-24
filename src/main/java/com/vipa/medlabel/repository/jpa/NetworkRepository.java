package com.vipa.medlabel.repository.jpa;

import com.vipa.medlabel.model.Network;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkRepository extends JpaRepository<Network, Integer> {
    // Additional custom queries can be defined here
}
