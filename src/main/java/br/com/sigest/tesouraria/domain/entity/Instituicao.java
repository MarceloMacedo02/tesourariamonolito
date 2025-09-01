package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "instituicoes")
@Data
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "logo", columnDefinition = "LONGBLOB")
    private byte[] logo;

    // @NotBlank(message = "O nome da instituição é obrigatório")
    // @Size(max = 255, message = "O nome da instituição não pode exceder 255
    // caracteres")
    @Column(nullable = false)
    private String nome;

    // @NotBlank(message = "O CNPJ é obrigatório")
    // @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ
    // inválido. Use o formato XX.XXX.XXX/XXXX-XX")
    @Column(nullable = false, unique = true)
    private String cnpj;

    // @Size(max = 500, message = "O endereço não pode exceder 500 caracteres")
    // @Column(length = 500)
    private String endereco;

    // Cargos da instituição
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presidente_id", unique = true)
    private Socio presidente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vice_presidente_id", unique = true)
    private Socio vicePresidente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tesoureiro_id", unique = true)
    private Socio tesoureiro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segundo_tesoureiro_id", unique = true)
    private Socio segundoTesoureiro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretario_id", unique = true)
    private Socio secretario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segundo_secretario_id", unique = true)
    private Socio segundoSecretario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orador_id", unique = true)
    private Socio orador;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presidente_conselho_fiscal_id", unique = true)
    private Socio presidenteConselhoFiscal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segundo_conselho_fiscal_id", unique = true)
    private Socio segundoConselhoFiscal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terceiro_conselho_fiscal_id", unique = true)
    private Socio terceiroConselhoFiscal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primeiro_suplente_conselho_fiscal_id", unique = true)
    private Socio primeiroSuplenteConselhoFiscal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segundo_suplente_conselho_fiscal_id", unique = true)
    private Socio segundoSuplenteConselhoFiscal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terceiro_suplente_conselho_fiscal_id", unique = true)
    private Socio terceiroSuplenteConselhoFiscal;

    // Campo para garantir que só exista uma instituição (sempre será 1)
    @Column(name = "fixed_id", unique = true, nullable = false)
    private Long fixedId = 1L;

    @PrePersist
    @PreUpdate
    public void ensureFixedId() {
        this.fixedId = 1L;
    }
}