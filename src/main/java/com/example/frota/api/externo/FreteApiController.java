package com.example.frota.api.externo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/frete")
public class FreteApiController {

    @Autowired
    private FreteService freteService;

    @PostMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcularFrete(@RequestBody Map<String, Object> dados) {
        String origem = (String) dados.get("origem");
        String destino = (String) dados.get("destino");
        double pesoKg = ((Number) dados.get("pesoKg")).doubleValue();
        String tipoCalculo = (String) dados.get("tipoCalculo");

        return ResponseEntity.ok(freteService.calcularFrete(origem, destino, pesoKg, tipoCalculo));
    }
}
