package br.com.sigest.tesouraria.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FornecedorDto {
    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String celular;
    private String telefoneComercial;
    private LocalDate dataCadastro;
    private boolean ativo;
    private List<EnderecoDto> enderecos;
}