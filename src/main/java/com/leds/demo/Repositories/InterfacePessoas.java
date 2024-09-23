package com.leds.demo.Repositories;

import com.leds.demo.Model.Pessoas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterfacePessoas extends JpaRepository<Pessoas, Long> {
    Optional<Pessoas> findByCpf(String cpf) ;
}
