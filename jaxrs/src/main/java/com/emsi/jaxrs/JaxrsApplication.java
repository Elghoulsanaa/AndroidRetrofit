package com.emsi.jaxrs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.emsi.jaxrs.entities.Compte;
import com.emsi.jaxrs.entities.TypeCompte;
import com.emsi.jaxrs.repositories.CompteRepository;

import java.util.Date;

@SpringBootApplication
public class JaxrsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaxrsApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CompteRepository compteRepository) {
        return args -> {
            for (int i = 0; i < 20; i++) {
                TypeCompte type = (i % 2 == 0) ? TypeCompte.EPARGNE : TypeCompte.COURANT;
                compteRepository.save(new Compte(null, Math.random() * 9000, new Date(), type));
            }
            compteRepository.findAll().forEach(System.out::println);
        };
    }
}
