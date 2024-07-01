package com.haifachagwey.jobportal.controller;

import com.haifachagwey.jobportal.entity.JobPostActivity;
import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.JobSeekerSave;
import com.haifachagwey.jobportal.entity.Users;
import com.haifachagwey.jobportal.services.JobPostActivityService;
import com.haifachagwey.jobportal.services.JobSeekerProfileService;
import com.haifachagwey.jobportal.services.JobSeekerSaveService;
import com.haifachagwey.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    //  Save a job
    @PostMapping("/{id}/save")
    public String saveJob(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersService.findUserByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getJobPostActivityById(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());
            } else {
                throw new RuntimeException("User not found");
            }
            jobSeekerSaveService.addJobSeekerSave(jobSeekerSave);
        }
        return "redirect:/dashboard";
    }

    //   Get saved jobs
    @GetMapping("/saved-jobs")
    public String getSavedJobs(Model model) {
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);
        return "saved-jobs";
    }

    //  Remove a job from saved job list
    //  public void unsaveJob(){}

}
