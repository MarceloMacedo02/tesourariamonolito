package br.com.sigest.tesouraria.dto;

import lombok.Data;

@Data
public class EnderecoDto {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}