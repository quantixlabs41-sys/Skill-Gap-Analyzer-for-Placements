package com.skillgap.controller;

import com.skillgap.dto.GapReportDTO;
import com.skillgap.service.GapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gap")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GapAnalysisController {

    private final GapAnalysisService gapService;

    @GetMapping("/analyze")
    public ResponseEntity<GapReportDTO> analyze(
            @RequestParam Long studentId,
            @RequestParam Long companyId
    ) {
        return ResponseEntity.ok(gapService.analyze(studentId, companyId));
    }

    @GetMapping("/analyze/all")
    public ResponseEntity<List<GapReportDTO>> analyzeAll(
            @RequestParam Long studentId
    ) {
        return ResponseEntity.ok(gapService.analyzeAll(studentId));
    }
}
