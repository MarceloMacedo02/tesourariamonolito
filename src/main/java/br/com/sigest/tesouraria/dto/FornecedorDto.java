package br.com.sigest.tesouraria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import br.com.sigest.tesouraria.domain.entity.Endereco;

/**
 * DTO para Fornecedor, espelhado na estrutura de SocioDto.
 * Utilizado para validação e transferência de dados com a View.
 */
@Data
@NoArgsConstructor
public class FornecedorDto {

    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres.")
    private String nome;

    @NotBlank(message = "O CNPJ/CPF é obrigatório.")
    @Size(max = 20, message = "O CNPJ/CPF não pode exceder 20 caracteres.")
    private String cnpj;

    private boolean ativo = true;

    @Email(message = "O formato do email é inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email;

    @Size(max = 20, message = "O número de celular não pode exceder 20 caracteres.")
    private String celular;

    @Size(max = 20, message = "O número do telefone comercial não pode exceder 20 caracteres.")
    private String telefoneComercial;

    private List<Endereco> enderecos = new ArrayList<>();
}