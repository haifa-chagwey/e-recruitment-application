package com.haifachagwey.jobportal.controller;

import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.Skills;
import com.haifachagwey.jobportal.entity.Users;
import com.haifachagwey.jobportal.repository.UsersRepository;
import com.haifachagwey.jobportal.services.JobSeekerProfileService;
import com.haifachagwey.jobportal.utils.FileDownloadUtil;
import com.haifachagwey.jobportal.utils.FileUploadUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;


@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private JobSeekerProfileService jobSeekerProfileService;
    private UsersRepository usersRepository;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    //  Get job seeker profile
    @GetMapping
    public String getJobSeekerProfile(Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.getUserId());
            if (seekerProfile.isPresent()) {
                jobSeekerProfile = seekerProfile.get();
                if (jobSeekerProfile.getSkills().isEmpty()) {
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
        }
        return "job-seeker-profile";
    }

    //  Edit job seeker profile
    @PostMapping
    //  http://localhost:8080/job-seeker-profile
    public String editJobSeekerProfile(JobSeekerProfile jobSeekerProfile, @RequestParam("image") MultipartFile image,
                                       @RequestParam("pdf") MultipartFile resume, Model model){
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }
        List<Skills> skillsList = new ArrayList<>();
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);
        for (Skills skill : jobSeekerProfile.getSkills()) {
            skill.setJobSeekerProfile(jobSeekerProfile);
        }
        String imageName = "";
        String resumeName = "";
        //  Set image name inside jobSeekerProfile
        if (!Objects.equals(image.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }
        //  Set resume name inside jobSeekerProfile
        if (!Objects.equals(resume.getOriginalFilename(), "")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(resume.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }
        JobSeekerProfile seekerProfile = jobSeekerProfileService.addJobSeekerProfile(jobSeekerProfile);
        try {
            // Save the image (PNG, JPEG) locally
            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
            if(!Objects.equals(image.getOriginalFilename(), "")){
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            // Save the resume (PDF file) locally
            if(!Objects.equals(resume.getOriginalFilename(), "")){
                FileUploadUtil.saveFile(uploadDir, resumeName, resume);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return "redirect:/dashboard";
    }

    //  Get job seeker profile from job details
    @GetMapping("/{id}")
    public String getCandidateProfile(@PathVariable("id") int id, Model model) {
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getJobSeekerProfileById(id);
        model.addAttribute("profile", seekerProfile.get());
        return "job-seeker-profile";
    }

    //  Download resume
    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName, @RequestParam(value = "userId") String userId) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

}
