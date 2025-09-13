package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocioImportResultDTO {
    private int insertedCount;
    private int updatedCount;
    private int errorCount;
    private String message;
}