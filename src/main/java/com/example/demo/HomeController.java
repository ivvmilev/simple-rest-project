package com.example.demo;


import com.example.demo.dto.UserDto;
import com.example.demo.exception.ExistingUserException;
import com.example.demo.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController
{
    private final UserService userService;

    public HomeController(UserService userServiceImpl)
    {
        this.userService = userServiceImpl;
    }

    @GetMapping("/")
    public String viewHomePage(Model model)
    {
        return findPaginated(1, "name", "asc", model);
    }

    @GetMapping("/listUsers")
    public String getUsers(Model model)
    {
        // create model attribute to bind form data
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    @PostMapping("/saveUser")
    public String saveEmployee(@ModelAttribute("employee") User user) throws ExistingUserException
    {
        userService.createUser(user);
        return "redirect:/";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable(value = "id") long id)
    {

        // call delete employee method
        this.userService.deleteUser(id);
        return "redirect:/";
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model)
    {
        int pageSize = 5;

        Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<User> listUsers = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listUsers", listUsers);
        return "index";
    }
}