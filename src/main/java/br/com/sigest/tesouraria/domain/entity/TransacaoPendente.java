package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma transação pendente de associação com sócio.
 * Essas transações foram importadas do OFX mas não foi possível identificar
 * automaticamente o sócio correspondente.
 */
@Entity
@Table(name = "transacoes_pendentes")
@Data
@NoArgsConstructor
public class TransacaoPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(length = 500)
    private String descricao;

    @Column(length = 100)
    private String documento;

    @Column(name = "fornecedor_ou_socio", length = 255)
    private String fornecedorOuSocio;

    @Column(name = "data_importacao", nullable = false)
    private LocalDate dataImportacao;

    @Column(name = "arquivo_origem", length = 255)
    private String arquivoOrigem;

    @Column(name = "processado", nullable = false)
    private Boolean processado = false;

    public TransacaoPendente(LocalDate data, TipoTransacao tipo, BigDecimal valor,
            String descricao, String documento, String fornecedorOuSocio,
            String arquivoOrigem) {
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.documento = documento;
        this.fornecedorOuSocio = fornecedorOuSocio;
        this.arquivoOrigem = arquivoOrigem;
        this.dataImportacao = LocalDate.now();
        this.processado = false;
    }
}