package com.skillgap.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "gap_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GapReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private Double matchPercentage;

    @Column(columnDefinition = "TEXT")
    private String missingSkills; // stored as comma-separated list

    @CreationTimestamp
    private Instant generatedAt;
}
