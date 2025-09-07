package br.com.sigest.tesouraria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.service.CobrancaService;
import br.com.sigest.tesouraria.service.FornecedorService;
import br.com.sigest.tesouraria.service.SocioService;

@RestController
@RequestMapping("/api/associados")
public class AssociadoController {

    @Autowired
    private SocioService socioService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private CobrancaService cobrancaService;

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarAssociado(@RequestParam String searchType, @RequestParam String documento) {
        if ("socio".equals(searchType)) {
            return socioService.findByCpf(documento)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else if ("fornecedor".equals(searchType)) {
            return fornecedorService.findByCnpj(documento)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.badRequest().body("Tipo de busca inválido.");
    }

    @GetMapping("/{id}/cobrancas")
    public ResponseEntity<?> getCobrancas(@PathVariable Long id, @RequestParam String type) {
        if ("socio".equals(type)) {
            List<Cobranca> cobrancas = cobrancaService.findBySocioIdAndStatus(id, StatusCobranca.ABERTA);
            return ResponseEntity.ok(cobrancas);
        } else if ("fornecedor".equals(type)) {
            Fornecedor fornecedor = fornecedorService.findById(id);
            List<Cobranca> cobrancas = cobrancaService.findByPagadorAndStatus(fornecedor.getNome(),
                    StatusCobranca.ABERTA);
            return ResponseEntity.ok(cobrancas);
        }
        return ResponseEntity.badRequest().body("Tipo de busca inválido.");
    }
}
