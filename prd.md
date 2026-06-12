# Skill Gap Analyzer for Placements

> A web-based application that helps students identify the gap between their current skills and company-required skills — enabling smarter, targeted placement preparation.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [System Architecture](#3-system-architecture)
4. [Core Features](#4-core-features)
5. [Database Schema](#5-database-schema)
6. [Backend — Spring Boot API](#6-backend--spring-boot-api)
7. [Frontend — React](#7-frontend--react)
8. [Skill Gap Analysis Logic](#8-skill-gap-analysis-logic)
9. [API Endpoints](#9-api-endpoints)
10. [Project Structure](#10-project-structure)
11. [Setup & Installation](#11-setup--installation)
12. [Sample Output](#12-sample-output)

---

## 1. Project Overview

**Skill Gap Analyzer for Placements** is a full-stack web application designed for students preparing for campus placements. The system:

- Accepts a student's current skill set as input
- Compares it against the skill requirements of target companies
- Calculates a **match percentage** for each company
- Highlights **missing skills** and suggests a learning path

### Problem Statement

Students often apply to companies without knowing which specific skills they lack. This leads to rejections and wasted preparation time. The Skill Gap Analyzer bridges this information gap by providing data-driven insights.

### Goals

- Help students focus on the right skills for the right companies
- Give placement coordinators a bird's-eye view of batch readiness
- Reduce mismatch between student profiles and job requirements

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React.js, Axios, React Router |
| Backend | Java 17, Spring Boot 3.x |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| API Style | REST |
| Auth (optional) | Spring Security + JWT |

---

## 3. System Architecture

```
┌─────────────────────────────────────────────────────┐
│                  React Frontend                     │
│   (Student Profile | Company View | Gap Report)    │
└────────────────────┬────────────────────────────────┘
                     │ HTTP / REST (Axios)
┌────────────────────▼────────────────────────────────┐
│              Spring Boot Backend                    │
│   ┌──────────────┐  ┌──────────────────────────┐   │
│   │ REST Controllers│  │  Gap Analysis Service    │   │
│   └──────┬───────┘  └──────────┬───────────────┘   │
│          │                     │                    │
│   ┌──────▼─────────────────────▼───────────────┐   │
│   │          Service Layer (Business Logic)     │   │
│   └──────────────────────┬──────────────────────┘   │
│                          │                          │
│   ┌──────────────────────▼──────────────────────┐   │
│   │     Repository Layer (Spring Data JPA)      │   │
│   └──────────────────────┬──────────────────────┘   │
└──────────────────────────┼──────────────────────────┘
                           │ JDBC
┌──────────────────────────▼──────────────────────────┐
│                  MySQL Database                     │
│  students | skills | companies | company_skills |  │
│  student_skills | gap_reports                      │
└─────────────────────────────────────────────────────┘
```

---

## 4. Core Features

### Student Side
- Register and build a skill profile
- View matched companies with a percentage score
- See missing skills per company
- Get a prioritized learning suggestion list

### Admin / Coordinator Side
- Add and manage companies and their required skills
- View all student gap reports
- Filter students by skill readiness percentage

### Analysis Engine
- Set-based comparison of student skills vs company requirements
- Weighted match scoring (optional: skill priority levels)
- Categorization: **Strong Match**, **Partial Match**, **Not Ready**

---

## 5. Database Schema

### `students`
```sql
CREATE TABLE students (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    department  VARCHAR(100),
    year        INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### `skills`
```sql
CREATE TABLE skills (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    category    VARCHAR(100)   -- e.g., "Programming", "Database", "Cloud"
);
```

### `student_skills`
```sql
CREATE TABLE student_skills (
    student_id  BIGINT,
    skill_id    BIGINT,
    proficiency ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
    PRIMARY KEY (student_id, skill_id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (skill_id)   REFERENCES skills(id)
);
```

### `companies`
```sql
CREATE TABLE companies (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    domain      VARCHAR(100),   -- e.g., "Product", "Service", "Startup"
    package_lpa DECIMAL(5, 2)
);
```

### `company_skills`
```sql
CREATE TABLE company_skills (
    company_id  BIGINT,
    skill_id    BIGINT,
    is_mandatory BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (company_id, skill_id),
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (skill_id)   REFERENCES skills(id)
);
```

### `gap_reports`
```sql
CREATE TABLE gap_reports (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id       BIGINT,
    company_id       BIGINT,
    match_percentage DECIMAL(5, 2),
    missing_skills   TEXT,          -- JSON array of missing skill names
    generated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (company_id) REFERENCES companies(id)
);
```

---

## 6. Backend — Spring Boot API

### Project Dependencies (`pom.xml`)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/skill_gap_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Entity — `Student.java`
```java
@Entity
@Data
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String department;
    private int year;

    @ManyToMany
    @JoinTable(
        name = "student_skills",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();
}
```

### Entity — `Skill.java`
```java
@Entity
@Data
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
}
```

### Entity — `Company.java`
```java
@Entity
@Data
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String domain;
    private Double packageLpa;

    @ManyToMany
    @JoinTable(
        name = "company_skills",
        joinColumns = @JoinColumn(name = "company_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();
}
```

### Service — `GapAnalysisService.java`
```java
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

        // Missing skills
        Set<String> missingSkills = new HashSet<>(requiredSkills);
        missingSkills.removeAll(studentSkills);

        // Match percentage
        int matched = requiredSkills.size() - missingSkills.size();
        double matchPct = requiredSkills.isEmpty() ? 100.0
            : (matched * 100.0) / requiredSkills.size();

        // Persist report
        GapReport report = new GapReport();
        report.setStudent(student);
        report.setCompany(company);
        report.setMatchPercentage(matchPct);
        report.setMissingSkills(String.join(",", missingSkills));
        reportRepo.save(report);

        return new GapReportDTO(
            student.getName(),
            company.getName(),
            matchPct,
            new ArrayList<>(missingSkills),
            classify(matchPct)
        );
    }

    private String classify(double pct) {
        if (pct >= 80) return "STRONG MATCH";
        if (pct >= 50) return "PARTIAL MATCH";
        return "NOT READY";
    }
}
```

### Controller — `GapAnalysisController.java`
```java
@RestController
@RequestMapping("/api/gap")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
        // Analyze student against all companies
        return ResponseEntity.ok(gapService.analyzeAll(studentId));
    }
}
```

---

## 7. Frontend — React

### Folder Structure
```
src/
├── api/
│   └── axiosConfig.js
├── components/
│   ├── StudentForm.jsx
│   ├── CompanyList.jsx
│   ├── GapReport.jsx
│   └── SkillBadge.jsx
├── pages/
│   ├── Dashboard.jsx
│   ├── AnalyzePage.jsx
│   └── ReportPage.jsx
├── App.jsx
└── main.jsx
```

### `axiosConfig.js`
```js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export default api;
```

### `AnalyzePage.jsx`
```jsx
import { useState } from 'react';
import api from '../api/axiosConfig';
import GapReport from '../components/GapReport';

export default function AnalyzePage() {
  const [studentId, setStudentId] = useState('');
  const [companyId, setCompanyId] = useState('');
  const [report, setReport]       = useState(null);

  const handleAnalyze = async () => {
    const res = await api.get('/gap/analyze', {
      params: { studentId, companyId }
    });
    setReport(res.data);
  };

  return (
    <div className="analyze-page">
      <h2>Skill Gap Analyzer</h2>
      <input
        placeholder="Student ID"
        value={studentId}
        onChange={e => setStudentId(e.target.value)}
      />
      <input
        placeholder="Company ID"
        value={companyId}
        onChange={e => setCompanyId(e.target.value)}
      />
      <button onClick={handleAnalyze}>Analyze</button>
      {report && <GapReport data={report} />}
    </div>
  );
}
```

### `GapReport.jsx`
```jsx
export default function GapReport({ data }) {
  const { studentName, companyName, matchPercentage, missingSkills, status } = data;

  const color = matchPercentage >= 80 ? 'green'
              : matchPercentage >= 50 ? 'orange' : 'red';

  return (
    <div className="gap-report">
      <h3>{studentName} → {companyName}</h3>
      <p style={{ color, fontWeight: 'bold', fontSize: '1.4rem' }}>
        {matchPercentage.toFixed(1)}% Match
      </p>
      <span className={`badge badge-${status.toLowerCase().replace(' ', '-')}`}>
        {status}
      </span>

      <h4>Missing Skills</h4>
      {missingSkills.length === 0
        ? <p>✅ No missing skills!</p>
        : <ul>{missingSkills.map(s => <li key={s}>{s}</li>)}</ul>
      }
    </div>
  );
}
```

---

## 8. Skill Gap Analysis Logic

```
Student Skills  = { Java, SQL, Spring Boot }
Company Skills  = { Java, SQL, React, Docker, Spring Boot }

Matched Skills  = { Java, SQL, Spring Boot }         → 3
Missing Skills  = { React, Docker }                  → 2
Total Required  = 5

Match % = (3 / 5) × 100 = 60%  →  PARTIAL MATCH
```

### Classification Table

| Match % | Status | Color |
|---|---|---|
| 80% – 100% | STRONG MATCH | 🟢 Green |
| 50% – 79% | PARTIAL MATCH | 🟡 Orange |
| 0% – 49% | NOT READY | 🔴 Red |

---

## 9. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/students` | Register a new student |
| `POST` | `/api/students/{id}/skills` | Add skills to student |
| `GET` | `/api/students/{id}` | Get student profile |
| `POST` | `/api/companies` | Add a company |
| `POST` | `/api/companies/{id}/skills` | Add required skills to company |
| `GET` | `/api/companies` | List all companies |
| `GET` | `/api/gap/analyze?studentId=&companyId=` | Analyze gap for one company |
| `GET` | `/api/gap/analyze/all?studentId=` | Analyze gap for all companies |
| `GET` | `/api/gap/reports/{studentId}` | Get saved reports for a student |
| `GET` | `/api/skills` | List all available skills |

---

## 10. Project Structure

```
skill-gap-analyzer/
├── backend/
│   ├── src/main/java/com/skillgap/
│   │   ├── controller/
│   │   │   ├── StudentController.java
│   │   │   ├── CompanyController.java
│   │   │   └── GapAnalysisController.java
│   │   ├── service/
│   │   │   ├── StudentService.java
│   │   │   ├── CompanyService.java
│   │   │   └── GapAnalysisService.java
│   │   ├── repository/
│   │   │   ├── StudentRepository.java
│   │   │   ├── CompanyRepository.java
│   │   │   ├── SkillRepository.java
│   │   │   └── GapReportRepository.java
│   │   ├── model/
│   │   │   ├── Student.java
│   │   │   ├── Company.java
│   │   │   ├── Skill.java
│   │   │   └── GapReport.java
│   │   ├── dto/
│   │   │   └── GapReportDTO.java
│   │   └── SkillGapApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
└── frontend/
    ├── public/
    ├── src/
    │   ├── api/axiosConfig.js
    │   ├── components/
    │   ├── pages/
    │   ├── App.jsx
    │   └── main.jsx
    ├── package.json
    └── vite.config.js
```

---

## 11. Setup & Installation

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+

### Backend Setup
```bash
# 1. Create the database
mysql -u root -p
CREATE DATABASE skill_gap_db;
EXIT;

# 2. Update application.properties with your DB credentials

# 3. Build and run
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

---

## 12. Sample Output

### API Response — `GET /api/gap/analyze?studentId=1&companyId=3`
```json
{
  "studentName": "Yogesh R",
  "companyName": "TCS Digital",
  "matchPercentage": 60.0,
  "missingSkills": ["React", "Docker"],
  "status": "PARTIAL MATCH"
}
```

### Gap Report — All Companies (Student View)

| Company | Match % | Status | Missing Skills |
|---|---|---|---|
| TCS Digital | 60% | PARTIAL MATCH | React, Docker |
| Infosys | 80% | STRONG MATCH | Docker |
| Zoho | 40% | NOT READY | React, Redis, Kafka, AWS |
| Freshworks | 90% | STRONG MATCH | GraphQL |

---

*Built with Java · Spring Boot · MySQL · React*
