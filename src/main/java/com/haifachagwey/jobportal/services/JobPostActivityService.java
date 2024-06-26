package com.haifachagwey.jobportal.services;

import com.haifachagwey.jobportal.entity.*;
import com.haifachagwey.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }


    public JobPostActivity addNewJobPostActivity(JobPostActivity jobPostActivity){
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDTO> getRecruiterJobs(int recruiter){
        List<IRecruiterJobs> recruiterJobsDTOs = jobPostActivityRepository.getRecruiterJobs(recruiter);
        List<RecruiterJobsDTO> recruiterJobsDTOList = new ArrayList<>();
        for (IRecruiterJobs rec : recruiterJobsDTOs) {
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
            recruiterJobsDTOList.add(new RecruiterJobsDTO(rec.getTotalCandidates(), rec.getJob_post_id(),
                    rec.getJob_title(), loc, comp));
        }
        return recruiterJobsDTOList;
    }

    public JobPostActivity getJobPostActivityById(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search (String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate(job, location, remote, type):
                jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }
}
