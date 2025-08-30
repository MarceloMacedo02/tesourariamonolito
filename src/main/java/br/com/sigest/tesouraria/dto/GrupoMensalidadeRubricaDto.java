package br.com.sigest.tesouraria.dto;

import lombok.Data;

@Data
public class GrupoMensalidadeRubricaDto {
    private Long id;
    private Long rubricaId;
    private String rubricaNome;
    private Float valor; // Recebe valor formatado (ex: R$ 100,00)
}
