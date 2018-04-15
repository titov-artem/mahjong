package com.github.mahjong.security.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SecurityMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
    }

}
