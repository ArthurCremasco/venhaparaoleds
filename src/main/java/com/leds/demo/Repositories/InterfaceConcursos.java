package com.leds.demo.Repositories;

import com.leds.demo.Model.Concursos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterfaceConcursos extends JpaRepository<Concursos, Long> {
    Optional<Concursos> findByCodigoConcurso(String codigoConcurso);
}
