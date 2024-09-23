package com.leds.demo.Dto;

import java.util.List;
import java.util.Set;

public record DtoConcursos(String orgao, String edital, String codigoConcurso, List<String> vagas){
}
