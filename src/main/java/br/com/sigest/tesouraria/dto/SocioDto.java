package br.com.sigest.tesouraria.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SocioDto {
    private Long id;
    private String nome;
    private String cpf;
    private String grau;
    private LocalDate dataNascimento;
    private String emailAlternativo;
    private String enderecoResidencial;
    private String celular;
    private String telefoneResidencial;
    private LocalDate dataCadastro;
    private Long usuarioId; // Assuming we only need the ID for Usuario
    private String status; // Assuming we only need the name for StatusSocio
    private Long grupoMensalidadeId; // Assuming we only need the ID for GrupoMensalidade
    private Long socioTitularId; // Assuming we only need the ID for titular Socio
    private List<SocioDto> dependentes;
}