package com.haifachagwey.jobportal.services;

import com.haifachagwey.jobportal.entity.JobPostActivity;
import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.JobSeekerSave;
import com.haifachagwey.jobportal.repository.JobSeekerSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerSaveRepository.findJobSeekerSaveByUserId(userAccountId);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job){
        return jobSeekerSaveRepository.findJobSeekerSaveByJob(job);
    }

    public void addJobSeekerSave(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
