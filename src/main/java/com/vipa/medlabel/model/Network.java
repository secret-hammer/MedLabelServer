package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Network")
public class Network {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int networkId;

    @Column(nullable = false, length = 50)
    private String networkName;

    @Column(nullable = true, length = 2000)
    private String description = "N/A";

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;
}