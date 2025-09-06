package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/socios")
public class SocioApiController {

    @Autowired
    private SocioService socioService;

    @GetMapping("/all")
    public List<SocioDto> getAllSocios() {
        return socioService.findAll().stream().map(socioService::toDto).collect(Collectors.toList());
    }
}