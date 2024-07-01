package com.haifachagwey.jobportal.controller;

import com.haifachagwey.jobportal.entity.RecruiterProfile;
import com.haifachagwey.jobportal.entity.Users;
import com.haifachagwey.jobportal.repository.UsersRepository;
import com.haifachagwey.jobportal.services.RecruiterProfileService;
import com.haifachagwey.jobportal.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    //  Get the recruiter profile
    @GetMapping
    public String getRecruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(users.getUserId());
//            if (recruiterProfile.isEmpty()) {
                model.addAttribute("profile", recruiterProfile.get());
//            }
        }
        return "recruiter-profile";
    }

    //  Edit recruiter profile
    @PostMapping
    public String addRecruiterProfile(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile
                                    , Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUsername = authentication.getName();
            Users user = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            recruiterProfile.setUserId(user);
            recruiterProfile.setUserAccountId(user.getUserId());
            model.addAttribute("profile", recruiterProfile);
            String fileName = "";
            if (!multipartFile.getOriginalFilename().equals("")) {
                fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                recruiterProfile.setProfilePhoto(fileName);
            }
            RecruiterProfile savedUser = recruiterProfileService.addRecruiterProfile(recruiterProfile);
            String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "redirect:/dashboard";
        }
        return null;
    }
}
