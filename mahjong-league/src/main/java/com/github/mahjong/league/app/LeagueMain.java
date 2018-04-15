package com.github.mahjong.league.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LeagueMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
    }

}
