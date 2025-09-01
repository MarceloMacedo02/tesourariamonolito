package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioImportResultDTO {
    private int insertedCount;
    private int updatedCount;
    private int errorCount;
    private String message;
}