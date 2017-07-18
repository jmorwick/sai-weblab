package net.sourcedestination.sai.weblab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:weblab-components.xml")
public class WeblabApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WeblabApp.class, args);
    }

}