package com.emsi.jaxrs.repositories;

import com.emsi.jaxrs.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {
}