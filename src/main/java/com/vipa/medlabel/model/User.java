package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true, length = 20)
    private String phone = "N/A";

    @Column(nullable = true, length = 100)
    private String profileLink = "N/A";

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;

    @OneToMany(mappedBy = "user")
    private List<Project> projects = new ArrayList<>();
}