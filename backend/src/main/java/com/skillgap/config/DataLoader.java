package com.skillgap.config;

import com.skillgap.model.Company;
import com.skillgap.model.Skill;
import com.skillgap.model.Student;
import com.skillgap.repository.CompanyRepository;
import com.skillgap.repository.SkillRepository;
import com.skillgap.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final SkillRepository skillRepo;
    private final StudentRepository studentRepo;
    private final CompanyRepository companyRepo;

    @Override
    public void run(String... args) throws Exception {
        // create skills
        Skill java = skillRepo.save(new Skill(null, "Java", "Programming"));
        Skill sql = skillRepo.save(new Skill(null, "SQL", "Database"));
        Skill spring = skillRepo.save(new Skill(null, "Spring Boot", "Framework"));
        Skill react = skillRepo.save(new Skill(null, "React", "Frontend"));
        Skill docker = skillRepo.save(new Skill(null, "Docker", "DevOps"));

        Student s1 = new Student();
        s1.setName("Yogesh R");
        s1.setEmail("yogesh@example.com");
        s1.setDepartment("CSE");
        s1.setYear(4);
        s1.setSkills(Set.of(java, sql, spring));
        studentRepo.save(s1);

        Company c1 = new Company();
        c1.setName("TCS Digital");
        c1.setDomain("Product");
        c1.setPackageLpa(8.5);
        c1.setRequiredSkills(Set.of(java, sql, spring, react, docker));
        companyRepo.save(c1);
    }
}
