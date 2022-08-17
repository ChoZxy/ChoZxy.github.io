package com.example.ioc.web;

import com.example.ioc.model.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
public interface UserController {

    @PostMapping
    User create(@RequestBody User user);

    @GetMapping(path = "/{user}")
    User get(@PathVariable(name = "user") Long id);

    @PutMapping(path = "/{user}")
    User update(@PathVariable(name = "user") Long id, @RequestBody User user);

    @DeleteMapping(path = "/{user}")
    User delete(@PathVariable(name = "user") Long id);

    @GetMapping(path = "/query")
    List<User> query(@RequestParam(name = "gender") String gender, @RequestParam(name = "age") Integer age);

}
