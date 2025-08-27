package br.com.sigest.tesouraria.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade Fornecedor, espelhada na estrutura da entidade Socio.
 * Inclui suporte a múltiplos endereços.
 */
@Entity
@Table(name = "fornecedores")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, length = 20, nullable = false)
    private String cnpj;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String celular;

    @Column(length = 20)
    private String telefoneComercial;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    private boolean ativo = true;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "fornecedor_enderecos", joinColumns = @JoinColumn(name = "fornecedor_id"))
    private List<Endereco> enderecos = new ArrayList<>();
}