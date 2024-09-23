package com.leds.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
public class Pessoas {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String data_nascimento;

    private String cpf;

    private List<String> profissoes;
}