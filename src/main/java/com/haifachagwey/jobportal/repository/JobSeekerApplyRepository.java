package com.haifachagwey.jobportal.repository;

import com.haifachagwey.jobportal.entity.JobPostActivity;
import com.haifachagwey.jobportal.entity.JobSeekerApply;
import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer>
{
    List<JobSeekerApply> findJobSeekerApplyByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findJobSeekerApplyByJob(JobPostActivity job);
}
