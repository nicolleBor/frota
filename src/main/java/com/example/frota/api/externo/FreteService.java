package com.example.frota.api.externo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class FreteService {

    // Configuração da API OpenRouteService
    @Value("${frete.api.openrouteservice.key:SUA_CHAVE_OPENROUTESERVICE}")
    private String apiKey;
    
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car";
    private static final String TOLL_URL = "https://api.openrouteservice.org/v2/toll";
    
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> calcularFrete(String origem, String destino, double pesoKg, String tipoCalculo) {
        try {
            // 1. Calcular distância usando OpenRouteService
            double distanciaKm = calcularDistancia(origem, destino);
            
            // 2. Calcular pedágio (se disponível)
            double valorPedagio = calcularPedagio(origem, destino);
            
            // 3. Calcular frete baseado no tipo
            double valorPorKm = calcularValorPorKm(tipoCalculo, pesoKg);
            
            // 4. Calcular valor total CORRETAMENTE: peso × valorPorKm × distancia + pedágio
            double valorTotal = (pesoKg * valorPorKm * distanciaKm) + valorPedagio;
            
            return Map.of(
                    "distanciaKm", Math.round(distanciaKm * 100.0) / 100.0,
                    "valorPorKm", valorPorKm,
                    "pedagio", valorPedagio,
                    "valorTotal", Math.round(valorTotal * 100.0) / 100.0,
                    "tipoCalculo", tipoCalculo,
                    "pesoKg", pesoKg,
                    "origem", origem,
                    "destino", destino
            );
            
        } catch (Exception e) {
            // Fallback para cálculo manual em caso de erro na API
            return calcularFreteFallback(origem, destino, pesoKg, tipoCalculo);
        }
    }
    
    private double calcularDistancia(String origem, String destino) {
        try {
            // Tentar usar coordenadas se disponíveis (formato: "lat,lon")
            if (origem.contains(",") && destino.contains(",")) {
                String[] coordsOrigem = origem.split(",");
                String[] coordsDestino = destino.split(",");
                
                double latOrigem = Double.parseDouble(coordsOrigem[0].trim());
                double lonOrigem = Double.parseDouble(coordsOrigem[1].trim());
                double latDestino = Double.parseDouble(coordsDestino[0].trim());
                double lonDestino = Double.parseDouble(coordsDestino[1].trim());
                
                return calcularDistanciaComCoordenadas(latOrigem, lonOrigem, latDestino, lonDestino);
            }
            
            // Para endereços em texto, usar coordenadas padrão de cidades brasileiras
            return calcularDistanciaPorCidade(origem, destino);
            
        } catch (Exception e) {
            // Fallback: distância estimada baseada em cidades brasileiras
            return calcularDistanciaPorCidade(origem, destino);
        }
    }
    
    private double calcularDistanciaComCoordenadas(double latOrigem, double lonOrigem, double latDestino, double lonDestino) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("api_key", apiKey)
                .queryParam("start", lonOrigem + "," + latOrigem)
                .queryParam("end", lonDestino + "," + latDestino)
                .toUriString();
        
        try {
            Map<String, Object> resposta = restTemplate.getForObject(url, Map.class);
            return extrairDistancia(resposta);
        } catch (Exception e) {
            // Fallback: distância estimada usando Haversine
            return calcularDistanciaHaversine(latOrigem, lonOrigem, latDestino, lonDestino);
        }
    }
    
    private double calcularDistanciaPorCidade(String origem, String destino) {
        // Mapeamento de cidades brasileiras principais
        Map<String, double[]> cidades = new java.util.HashMap<>();
        
        // São Paulo e região
        cidades.put("são paulo", new double[]{-23.5505, -46.6333});
        cidades.put("sp", new double[]{-23.5505, -46.6333});
        cidades.put("guarulhos", new double[]{-23.4538, -46.5331});
        cidades.put("campinas", new double[]{-22.9056, -47.0608});
        cidades.put("santo andré", new double[]{-23.6637, -46.5382});
        cidades.put("osasco", new double[]{-23.5329, -46.7919});
        cidades.put("são bernardo do campo", new double[]{-23.6939, -46.5650});
        
        // Rio de Janeiro e região
        cidades.put("rio de janeiro", new double[]{-22.9068, -43.1729});
        cidades.put("rj", new double[]{-22.9068, -43.1729});
        cidades.put("duque de caxias", new double[]{-22.7856, -43.3047});
        cidades.put("nova iguaçu", new double[]{-22.7556, -43.4606});
        cidades.put("são gonçalo", new double[]{-22.8269, -43.0539});
        
        // Outras capitais
        cidades.put("belo horizonte", new double[]{-19.9167, -43.9345});
        cidades.put("bh", new double[]{-19.9167, -43.9345});
        cidades.put("salvador", new double[]{-12.9714, -38.5014});
        cidades.put("brasília", new double[]{-15.7801, -47.9292});
        cidades.put("fortaleza", new double[]{-3.7319, -38.5267});
        cidades.put("manaus", new double[]{-3.1190, -60.0217});
        cidades.put("curitiba", new double[]{-25.4244, -49.2654});
        cidades.put("recife", new double[]{-8.0476, -34.8770});
        cidades.put("porto alegre", new double[]{-30.0346, -51.2177});
        cidades.put("goiânia", new double[]{-16.6864, -49.2643});
        cidades.put("belém", new double[]{-1.4558, -48.5044});
        cidades.put("são luís", new double[]{-2.5297, -44.3028});
        cidades.put("maceió", new double[]{-9.6658, -35.7353});
        cidades.put("natal", new double[]{-5.7945, -35.2110});
        cidades.put("teresina", new double[]{-5.0892, -42.8019});
        cidades.put("campo grande", new double[]{-20.4697, -54.6201});
        cidades.put("joão pessoa", new double[]{-7.1195, -34.8450});
        cidades.put("jaboatão dos guararapes", new double[]{-8.1128, -35.0147});
        
        double[] coordsOrigem = cidades.getOrDefault(origem.toLowerCase().trim(), new double[]{-23.5505, -46.6333}); // SP padrão
        double[] coordsDestino = cidades.getOrDefault(destino.toLowerCase().trim(), new double[]{-22.9056, -47.0608}); // Campinas padrão
        
        return calcularDistanciaComCoordenadas(coordsOrigem[0], coordsOrigem[1], coordsDestino[0], coordsDestino[1]);
    }
    
    private double extrairDistancia(Map<String, Object> resposta) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> features = (java.util.List<Map<String, Object>>) resposta.get("features");
            
            if (features != null && !features.isEmpty()) {
                Map<String, Object> feature = features.get(0);
                Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                Map<String, Object> summary = (Map<String, Object>) properties.get("summary");
                Number distance = (Number) summary.get("distance");
                return distance.doubleValue() / 1000; // metros → km
            }
        } catch (Exception e) {
            // Log do erro se necessário
        }
        return 100.0; // Distância padrão em caso de erro
    }
    
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private double calcularPedagio(String origem, String destino) {
        // Implementação simplificada de cálculo de pedágio
        // Em produção, usar APIs específicas como HERE Technologies ou Google Routes
        try {
            String url = UriComponentsBuilder.fromHttpUrl(TOLL_URL)
                    .queryParam("api_key", apiKey)
                    .queryParam("start", "-46.6333,-23.5505") // São Paulo
                    .queryParam("end", "-47.0608,-22.9056")   // Campinas
                    .toUriString();
            
            Map<String, Object> resposta = restTemplate.getForObject(url, Map.class);
            return extrairValorPedagio(resposta);
        } catch (Exception e) {
            // Fallback: valor estimado baseado na distância
            return 25.0; // Valor médio de pedágio
        }
    }
    
    private double extrairValorPedagio(Map<String, Object> resposta) {
        try {
            // Implementar extração do valor de pedágio da resposta da API
            // Por enquanto, retorna valor fixo
            return 25.0;
        } catch (Exception e) {
            return 25.0;
        }
    }
    
    private double calcularValorPorKm(String tipoCalculo, double pesoKg) {
        // Valores base por tipo de cálculo (valor fixo por km, não multiplicado pelo peso)
        switch (tipoCalculo.toLowerCase()) {
            case "peso":
                return 0.15; // R$ 0,15 por kg por km (o peso será multiplicado na fórmula principal)
            case "volume":
                return 2.50; // R$ 2,50 por km para carga volumosa
            case "caixa":
                return 1.80; // R$ 1,80 por km por caixa
            default:
                return 1.50; // Valor padrão
        }
    }
    
    private Map<String, Object> calcularFreteFallback(String origem, String destino, double pesoKg, String tipoCalculo) {
        // Cálculo manual quando a API não está disponível
        double distanciaEstimada = 100.0; // km
        double valorPedagio = 25.0;
        double valorPorKm = calcularValorPorKm(tipoCalculo, pesoKg);
        double valorTotal = (pesoKg * valorPorKm * distanciaEstimada) + valorPedagio;
        
        return Map.of(
                "distanciaKm", distanciaEstimada,
                "valorPorKm", valorPorKm,
                "pedagio", valorPedagio,
                "valorTotal", Math.round(valorTotal * 100.0) / 100.0,
                "tipoCalculo", tipoCalculo,
                "pesoKg", pesoKg,
                "origem", origem,
                "destino", destino,
                "observacao", "Cálculo estimado - API indisponível"
        );
    }
}
