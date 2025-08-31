package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SocioDto {

    private Long id;

    // --- Dados Pessoais ---
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "O CPF informado é inválido.")
    private String cpf;

    public void setCpf(String cpf) {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("[^0-9]", "");
        } else {
            this.cpf = null;
        }
    }

    @NotBlank(message = "O grau é obrigatório.")
    private String grau;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate dataNascimento;

    private StatusSocio status;

    // --- Contato ---
    @NotBlank(message = "O email de login é obrigatório.")
    @Email(message = "O formato do email é inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email; 

    @Email(message = "O formato do email alternativo é inválido.")
    @Size(max = 100, message = "O email alternativo não pode exceder 100 caracteres.")
    private String emailAlternativo;

    @Size(max = 20, message = "O número de celular não pode exceder 20 caracteres.")
    private String celular;

    @Size(max = 20, message = "O número do telefone residencial não pode exceder 20 caracteres.")
    private String telefoneResidencial;

    // --- Dados Financeiros ---
    private Long grupoMensalidadeId;

    // --- Endereço Principal (para o formulário) ---
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
}