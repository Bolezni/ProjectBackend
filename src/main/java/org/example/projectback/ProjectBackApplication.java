package org.example.projectback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ProjectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBackApplication.class, args);
    }

}
