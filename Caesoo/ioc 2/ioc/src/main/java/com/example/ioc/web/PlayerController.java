package com.example.ioc.web;

import com.example.ioc.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/player")
public class PlayerController {

    private Player player;

    @Autowired
    public PlayerController(@Qualifier("somebody") Player player) {
        System.out.println("inject: " + player);
        this.player = player;
    }

    @RequestMapping(path = "/hi", method = RequestMethod.GET)
    public String hi() {
        return player.sayHi();
    }
}
