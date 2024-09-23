package com.leds.demo.Controller;

import com.leds.demo.Model.Concursos;
import com.leds.demo.Model.Pessoas;
import com.leds.demo.Repositories.InterfaceConcursos;
import com.leds.demo.Repositories.InterfacePessoas;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@RestController
public class ControllerConcurso {

    @Autowired
    InterfaceConcursos interfaceConcursos;

    @Autowired
    InterfacePessoas interfacePessoas;

    @PostMapping("/concursos")
    public ResponseEntity<Concursos> saveConcurso(@RequestBody @Valid Concursos concurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interfaceConcursos.save(concurso));
    }

    @GetMapping("/concursos")
    public ResponseEntity<List<Concursos>> getAllConcursos() {
        return ResponseEntity.status(HttpStatus.OK).body(interfaceConcursos.findAll());
    }

    public void init() throws IOException {
        readConcursosFromFile();
    }

    public String readConcursosFromFile() throws IOException {
        File file = new File("C:\\Users\\arthu\\Downloads\\springboot\\springboot\\src\\main\\java\\com\\example\\springboot\\concursos.txt");
        List<String> linhas = Files.readAllLines(file.toPath());

        for (String linha : linhas) {
            String[] partes = linha.split(" ", 4);
            if (partes.length < 4) continue;

            String orgao = partes[0];
            String edital = partes[1];
            String codigoConcurso = partes[2];

            // Verifica se o código do concurso já está cadastrado
            if (interfaceConcursos.findByCodigoConcurso(codigoConcurso).isPresent()) {
                continue; // Se já estiver cadastrado, pula para a próxima linha
            }

            // Extrai as vagas da linha
            String vagasStr = linha.substring(linha.indexOf("[") + 1, linha.indexOf("]"));
            List<String> vagas = List.of(vagasStr.split(", "));

            // Cria o modelo do concurso
            Concursos concurso = new Concursos();
            concurso.setOrgao(orgao);
            concurso.setEdital(edital);
            concurso.setCodigoConcurso(codigoConcurso);
            concurso.setVagas(vagas);

            // Salva o concurso no banco
            saveConcurso(concurso);
        }
        return "Concursos salvos no banco";
    }



    @GetMapping("/concursos/vagas/{cpf}")
    public ResponseEntity<List<String>> getVagasByCpf(@PathVariable(value = "cpf") String cpf) {
        Optional<Pessoas> pessoaOpt = interfacePessoas.findByCpf(cpf);

        if (pessoaOpt.isPresent()) {
            Pessoas pessoa = pessoaOpt.get();
            List<String> profissoes = pessoa.getProfissoes();
            Set<String> vagasCorrespondentes = new HashSet<>(); // Usar um Set para evitar duplicatas

            // Buscar todos os concursos
            List<Concursos> concursos = interfaceConcursos.findAll();

            // Filtrar vagas de acordo com as profissões
            for (Concursos concurso : concursos) {
                for (String vaga : concurso.getVagas()) {
                    if (profissoes.stream().anyMatch(vaga::equalsIgnoreCase)) {
                        vagasCorrespondentes.add(vaga); // Adiciona ao Set
                    }
                }
            }

            // Converte o Set de volta para uma List antes de retornar
            return ResponseEntity.ok(new ArrayList<>(vagasCorrespondentes));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

