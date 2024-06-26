package com.haifachagwey.jobportal.repository;

import com.haifachagwey.jobportal.entity.JobPostActivity;
import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    List<JobSeekerSave> findJobSeekerSaveByUserId(JobSeekerProfile jobSeekerProfile);

    List<JobSeekerSave> findJobSeekerSaveByJob(JobPostActivity job);
}
