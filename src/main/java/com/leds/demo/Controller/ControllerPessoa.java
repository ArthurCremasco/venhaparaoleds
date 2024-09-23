package com.leds.demo.Controller;

import com.leds.demo.Dto.DtoPessoas;
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
import java.util.stream.Collectors;

@RestController
public class ControllerPessoa {

    @Autowired
    InterfacePessoas interfacePessoas;

    @Autowired
    private InterfaceConcursos interfaceConcursos;

    @PostMapping("/pessoas")
    public ResponseEntity<Pessoas> savePessoa(@RequestBody @Valid DtoPessoas pessoaRecordDto) {
        Pessoas pessoaModel = new Pessoas();
        BeanUtils.copyProperties(pessoaRecordDto, pessoaModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(interfacePessoas.save(pessoaModel));
    }

    @GetMapping("/pessoas")
    public ResponseEntity<List<Pessoas>> getAllPessoas() {
        return ResponseEntity.status(HttpStatus.OK).body(interfacePessoas.findAll());
    }

    public void init() throws IOException {
        readPessoasFromFile();
    }

    public String readPessoasFromFile() throws IOException {
        File file = new File("C:\\Users\\arthu\\Downloads\\springboot\\springboot\\src\\main\\java\\com\\example\\springboot\\candidatos.txt");
        List<String> linhas = Files.readAllLines(file.toPath());

        for (String linha : linhas) {
            String[] partes = linha.split(" ", 4);
            if (partes.length < 4) continue;

            String nome = partes[0] + " " + partes[1];
            String dataNascimento = partes[2];

            String cpf;
            int indexCpfEnd = linha.indexOf("[");
            if (indexCpfEnd != -1) {
                cpf = linha.substring(partes[0].length() + partes[1].length() + partes[2].length() + 3, indexCpfEnd).trim();
            } else {
                cpf = partes[3];
            }

            // Verifica se o CPF já está cadastrado
            if (interfacePessoas.findByCpf(cpf).isPresent()) {
                continue; // Se já estiver cadastrado, pula para a próxima linha
            }

            // Extrai as profissões e cria o DTO
            String profissaoStr = linha.substring(linha.indexOf("[") + 1, linha.indexOf("]"));
            List<String> profissoes = List.of(profissaoStr.split(", "));
            DtoPessoas pessoaRecordDto = new DtoPessoas(nome, dataNascimento, cpf, profissoes);

            // Salva a nova pessoa no banco
            savePessoa(pessoaRecordDto);
        }

        return "Pessoas salvas no banco";
    }


    @GetMapping("/concursos/candidatos/{codigoConcurso}")
    public ResponseEntity<List<DtoPessoas>> listarCandidatosPorCodigoConcurso(@PathVariable(value = "codigoConcurso") String codigoConcurso) {
        Optional<Concursos> concursoOpt = interfaceConcursos.findByCodigoConcurso(codigoConcurso);

        if (concursoOpt.isPresent()) {
            List<String> vagas = concursoOpt.get().getVagas();
            Set<DtoPessoas> candidatosCorrespondentes = new HashSet<>(); // Usar um Set para evitar duplicatas

            // Buscar todas as pessoas
            List<Pessoas> pessoas = interfacePessoas.findAll();

            // Filtrar candidatos de acordo com as profissões
            for (Pessoas pessoa : pessoas) {
                for (String vaga : vagas) {
                    if (pessoa.getProfissoes().stream().anyMatch(vaga::equalsIgnoreCase)) {
                        // Adiciona ao Set com a lista de profissões
                        candidatosCorrespondentes.add(new DtoPessoas(pessoa.getNome(), pessoa.getData_nascimento(), pessoa.getCpf(), pessoa.getProfissoes()));
                    }
                }
            }

            // Converte o Set de volta para uma List antes de retornar
            return ResponseEntity.ok(new ArrayList<>(candidatosCorrespondentes));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}

