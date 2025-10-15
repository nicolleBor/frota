package com.example.frota.api.externo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class FreteService {

    private static final String API_KEY = "SUA_CHAVE_OPENROUTESERVICE";
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    public Map<String, Object> calcularFrete(String origem, String destino, double pesoKg, String tipoCalculo) {
        // Converter endereços para coordenadas (geocoding pode ser adicionado depois)
        // Aqui é apenas exemplo com coordenadas fixas
        double latOrigem = -23.5505;  // São Paulo
        double lonOrigem = -46.6333;
        double latDestino = -22.9056; // Campinas
        double lonDestino = -47.0608;

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("api_key", API_KEY)
                .queryParam("start", lonOrigem + "," + latOrigem)
                .queryParam("end", lonDestino + "," + latDestino)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        Map resposta = restTemplate.getForObject(url, Map.class);

        @SuppressWarnings("unchecked")
        double distanciaKm = 0.0;

        if (resposta.get("features") instanceof java.util.List<?> featuresList && !featuresList.isEmpty()) {
            Object featureObj = featuresList.get(0);
            if (featureObj instanceof Map<?, ?> featureMap) {
                Object propertiesObj = featureMap.get("properties");
                if (propertiesObj instanceof Map<?, ?> propertiesMap) {
                    Object summaryObj = propertiesMap.get("summary");
                    if (summaryObj instanceof Map<?, ?> summaryMap) {
                        Object distanceObj = summaryMap.get("distance");
                        if (distanceObj instanceof Number distanceNumber) {
                            distanciaKm = distanceNumber.doubleValue() / 1000; // metros → km
                        }
                    }
                }
            }
        }

        double valorPorKm = 5.0; // valor fixo exemplo
        double pedagio = 20.0; // mock por enquanto

        double valorTotal = distanciaKm * valorPorKm + pedagio;

        return Map.of(
                "distanciaKm", distanciaKm,
                "valorPorKm", valorPorKm,
                "pedagio", pedagio,
                "valorTotal", valorTotal
        );
    }
}
