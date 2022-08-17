package com.example.ioc.config;

import com.example.ioc.model.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean("somebody")
    public Player getSomebody() {
        Player p = new Player() {
            @Override
            public String sayHi() {
                return "I am somebody";
            }
        };
        System.out.println("config:" + p);
        return p;
    }

    @Bean("nobody")
    public Player getNobody() {
        return new Player() {
            @Override
            public String sayHi() {
                return "I am nobody";
            }
        };
    }
}
