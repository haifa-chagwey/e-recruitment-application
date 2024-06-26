package com.haifachagwey.jobportal.controller;

import com.haifachagwey.jobportal.entity.Users;
import com.haifachagwey.jobportal.entity.UsersType;
import com.haifachagwey.jobportal.services.UsersService;
import com.haifachagwey.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

//    Returns Register page
    @GetMapping("/register")
    public String register(Model model) {
        List<UsersType> usersTypeList = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypeList);
        model.addAttribute("user", new Users());
        return "register";
    }

//  Action: Adds new user
    @PostMapping("/register/new")
    public String UserRegisteration(@Valid Users user) {
        usersService.addNewUser(user);
//      *** To check this
        return "redirect:/dashboard/";
    }

//  Returns page:  Returns the login page
    @GetMapping("/login")
    public String login(){
        return "login";
    }

//    Action: log out from the application
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";

    }
}
