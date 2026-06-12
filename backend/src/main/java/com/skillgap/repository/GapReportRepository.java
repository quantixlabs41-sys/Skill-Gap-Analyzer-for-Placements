package com.skillgap.repository;

import com.skillgap.model.GapReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GapReportRepository extends JpaRepository<GapReport, Long> {
    List<GapReport> findByStudentId(Long studentId);
}
