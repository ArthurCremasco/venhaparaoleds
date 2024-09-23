package com.leds.demo.Dto;

import java.util.List;
import java.util.Set;

public record DtoPessoas(String nome, String data_nascimento, String cpf, List<String> profissoes) {

}
