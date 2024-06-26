package com.haifachagwey.jobportal.repository;

import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
