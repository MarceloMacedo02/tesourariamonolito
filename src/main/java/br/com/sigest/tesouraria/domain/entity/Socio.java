package br.com.sigest.tesouraria.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.sigest.tesouraria.domain.converters.GrauSocioConverter;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

// =============================================================================
// CLASSE DE ENTIDADE (MAPEAMENTO DO BANCO DE DADOS)
// =============================================================================
@Entity
@Table(name = "socios")
@Data
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Convert(converter = GrauSocioConverter.class)
    @Column(nullable = false)
    private String grau;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 100)
    private String emailAlternativo;

    @Column(length = 20)
    private String celular;

    @Column(length = 20)
    private String telefoneResidencial;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "socio_enderecos", joinColumns = @JoinColumn(name = "socio_id"))
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario; // Contém o email principal de login

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSocio status;

    @ManyToOne
    @JoinColumn(name = "grupo_mensalidade_id")
    private GrupoMensalidade grupoMensalidade;
}
