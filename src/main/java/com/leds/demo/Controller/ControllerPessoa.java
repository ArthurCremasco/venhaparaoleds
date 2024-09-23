package com.leds.demo.Controller;

import com.leds.demo.Dto.DtoPessoas;
import com.leds.demo.Model.Pessoas;
import com.leds.demo.Repositories.InterfacePessoas;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
public class ControllerPessoa {

    @Autowired
    InterfacePessoas interfacePessoas;

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

            if (interfacePessoas.findByCpf(cpf).isEmpty()) {
                String profissaoStr = linha.substring(linha.indexOf("[") + 1, linha.indexOf("]"));
                List<String> profissoes = List.of(profissaoStr.split(", "));
                DtoPessoas pessoaRecordDto = new DtoPessoas(nome, dataNascimento, cpf, profissoes);
                savePessoa(pessoaRecordDto);
            }
        }
        return "Pessoas salvas no banco";
    }
}
