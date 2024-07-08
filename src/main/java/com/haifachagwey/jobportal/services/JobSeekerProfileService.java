package com.haifachagwey.jobportal.services;

import com.haifachagwey.jobportal.entity.JobSeekerProfile;
import com.haifachagwey.jobportal.entity.Users;
import com.haifachagwey.jobportal.repository.JobSeekerProfileRepository;
import com.haifachagwey.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UsersRepository usersRepository;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository, UsersRepository usersRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.usersRepository = usersRepository;
    }

    public Optional<JobSeekerProfile> getJobSeekerProfileById(Integer id){
        return jobSeekerProfileRepository.findById(id);
    }

    public JobSeekerProfile addJobSeekerProfile(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentJobSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUsername = authentication.getName();
            Users user = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = this.getJobSeekerProfileById(user.getUserId());
            return seekerProfile.orElse(null);
        } else return null;
    }
}
