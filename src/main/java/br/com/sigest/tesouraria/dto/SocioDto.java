package br.com.sigest.tesouraria.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@Getter
@Setter
public class SocioDto {
    private Long id;
    private String nome;
    private String cpf;
    private String grau;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
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