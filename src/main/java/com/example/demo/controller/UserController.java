package com.example.demo.controller;


import com.example.demo.UserModelAssembler;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.exception.ExistingUserException;
import com.example.demo.service.UserService;
import com.sun.istack.NotNull;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController
{
    private final UserService userService;

    private final UserModelAssembler assembler;

    public UserController(UserService userService, UserModelAssembler assembler)
    {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping("/user")
    public Slice<User> getUsers(
            @RequestParam(defaultValue = "1") int pageNumber
            , @RequestParam(defaultValue = "name") String sortField
            , @RequestParam(defaultValue = "asc") String order
    )
    {
        return userService.findPaginated(pageNumber, sortField, order);
    }

    @GetMapping("/users")
    CollectionModel<EntityModel<User>> all()
    {
        List<EntityModel<User>> users = getAllUsers().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    public List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public EntityModel<User> getOneUser(@PathVariable Long id)
    {
        User user = userService.getUserById(id);

        return assembler.toModel(user);
    }

    @PostMapping("/user")
    public UserDto saveUser(@RequestBody @NotNull UserDto user) throws ExistingUserException
    {
        return userService.createUser(user);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable(value = "id") long id)
    {
        this.userService.deleteUser(id);
    }
}
