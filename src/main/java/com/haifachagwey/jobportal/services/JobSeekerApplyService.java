package com.haifachagwey.jobportal.services;

import com.haifachagwey.jobportal.entity.JobPostActivity;
import com.haifachagwey.jobportal.entity.JobSeekerApply;
import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    @Autowired
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId){
        return jobSeekerApplyRepository.findJobSeekerApplyByUserId(userAccountId);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job){
        return jobSeekerApplyRepository.findJobSeekerApplyByJob(job);
    }

    public void addJobSeekerApply(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }
}
