package com.skillgap.service;

import com.skillgap.dto.GapReportDTO;
import com.skillgap.model.Company;
import com.skillgap.model.GapReport;
import com.skillgap.model.Student;
import com.skillgap.repository.CompanyRepository;
import com.skillgap.repository.GapReportRepository;
import com.skillgap.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GapAnalysisService {

    private final StudentRepository studentRepo;
    private final CompanyRepository companyRepo;
    private final GapReportRepository reportRepo;

    public GapReportDTO analyze(Long studentId, Long companyId) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        Company company = companyRepo.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found"));

        Set<String> studentSkills = student.getSkills().stream()
            .map(s -> s.getName().toLowerCase())
            .collect(Collectors.toSet());

        Set<String> requiredSkills = company.getRequiredSkills().stream()
            .map(s -> s.getName().toLowerCase())
            .collect(Collectors.toSet());

        Set<String> missingSkills = new HashSet<>(requiredSkills);
        missingSkills.removeAll(studentSkills);

        int matched = requiredSkills.size() - missingSkills.size();
        double matchPct = requiredSkills.isEmpty() ? 100.0
            : (matched * 100.0) / requiredSkills.size();

        // Persist
        GapReport report = new GapReport();
        report.setStudent(student);
        report.setCompany(company);
        report.setMatchPercentage(matchPct);
        report.setMissingSkills(String.join(",", missingSkills));
        reportRepo.save(report);

        List<String> missingList = missingSkills.stream()
            .map(s -> capitalize(s))
            .collect(Collectors.toList());

        return new GapReportDTO(
            student.getName(),
            company.getName(),
            matchPct,
            missingList,
            classify(matchPct)
        );
    }

    public List<GapReportDTO> analyzeAll(Long studentId) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        return companyRepo.findAll().stream()
            .map(c -> analyze(studentId, c.getId()))
            .collect(Collectors.toList());
    }

    private String classify(double pct) {
        if (pct >= 80) return "STRONG MATCH";
        if (pct >= 50) return "PARTIAL MATCH";
        return "NOT READY";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}
