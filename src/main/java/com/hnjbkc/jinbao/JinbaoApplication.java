package com.hnjbkc.jinbao;

import com.hnjbkc.jinbao.base.BaseDaoImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hnjbkc.jinbao.*")
@EnableJpaRepositories(repositoryBaseClass = BaseDaoImpl.class)
public class JinbaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JinbaoApplication.class, args);
    }

}
