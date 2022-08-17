package com.example.ioc.web;

import com.example.ioc.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserController implements UserController {

    private final Map<Long, User> users;
    private Long counter;

    public InMemoryUserController() {
        users = new HashMap<>();
        counter = 0L;
    }

    @Override
    public User create(User user) {
        user.setId(++counter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public User update(Long id, User user) {
        User userToUpdate = users.get(id);
        if (userToUpdate == null) {
            return null;
        }

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getGender() != null) {
            userToUpdate.setGender(user.getGender());
        }
        if (user.getAge() != null) {
            userToUpdate.setAge(user.getAge());
        }
        return userToUpdate;
    }

    @Override
    public User delete(Long id) {
        return users.remove(id);
    }

    @Override
    public List<User> query(String gender, Integer age) {
        List<User> usersToQuery = new ArrayList<>();
        for (User user : users.values()) {
            if (gender.equals(user.getGender()) && age.equals(user.getAge())) {
                usersToQuery.add(user);
            }
        }
        return usersToQuery;
    }
}
