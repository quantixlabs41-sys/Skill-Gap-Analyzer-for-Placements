package com.skillgap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GapReportDTO {
    private String studentName;
    private String companyName;
    private Double matchPercentage;
    private List<String> missingSkills;
    private String status;
}
