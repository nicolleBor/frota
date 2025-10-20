package com.example.frota.solicitacao;

import com.example.frota.caixa.Caixa;
import com.example.frota.produto.Produto;
import org.springframework.stereotype.Service;

@Service
public class ValidacaoService {
    
    /**
     * Valida se o produto cabe na caixa selecionada
     * @param produto Produto a ser transportado
     * @param caixa Caixa selecionada
     * @return true se o produto cabe na caixa, false caso contrário
     */
    public boolean produtoCabeNaCaixa(Produto produto, Caixa caixa) {
        if (produto == null || caixa == null) {
            return false;
        }
        
        // Verificar se todas as dimensões do produto são menores ou iguais às da caixa
        boolean comprimentoOk = produto.getComprimento() <= caixa.getComprimento();
        boolean larguraOk = produto.getLargura() <= caixa.getLargura();
        boolean alturaOk = produto.getAltura() <= caixa.getAltura();
        
        return comprimentoOk && larguraOk && alturaOk;
    }
    
    /**
     * Retorna uma mensagem detalhada sobre por que o produto não cabe na caixa
     * @param produto Produto a ser transportado
     * @param caixa Caixa selecionada
     * @return Mensagem explicativa
     */
    public String getMensagemValidacao(Produto produto, Caixa caixa) {
        if (produto == null || caixa == null) {
            return "Produto ou caixa não selecionados.";
        }
        
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("O produto não cabe na caixa selecionada:\n");
        
        if (produto.getComprimento() > caixa.getComprimento()) {
            mensagem.append(String.format("- Comprimento: Produto %.2fm > Caixa %.2fm\n", 
                produto.getComprimento(), caixa.getComprimento()));
        }
        
        if (produto.getLargura() > caixa.getLargura()) {
            mensagem.append(String.format("- Largura: Produto %.2fm > Caixa %.2fm\n", 
                produto.getLargura(), caixa.getLargura()));
        }
        
        if (produto.getAltura() > caixa.getAltura()) {
            mensagem.append(String.format("- Altura: Produto %.2fm > Caixa %.2fm\n", 
                produto.getAltura(), caixa.getAltura()));
        }
        
        mensagem.append("\nPor favor, selecione uma caixa maior ou um produto menor.");
        
        return mensagem.toString();
    }
    
    /**
     * Calcula o volume do produto em metros cúbicos
     * @param produto Produto
     * @return Volume em m³
     */
    public double calcularVolumeProduto(Produto produto) {
        if (produto == null) {
            return 0.0;
        }
        return produto.getComprimento() * produto.getLargura() * produto.getAltura();
    }
    
    /**
     * Calcula o peso cubado do produto
     * @param produto Produto
     * @return Peso cubado em kg (Volume × 300 kg/m³)
     */
    public double calcularPesoCubado(Produto produto) {
        double volume = calcularVolumeProduto(produto);
        return volume * 300.0; // Fator de cubagem para transporte rodoviário
    }
    
    /**
     * Determina qual peso será usado para cobrança (maior entre peso real e cubado)
     * @param produto Produto
     * @return Peso para cobrança em kg
     */
    public double determinarPesoCobrado(Produto produto) {
        double pesoReal = produto.getPeso();
        double pesoCubado = calcularPesoCubado(produto);
        return Math.max(pesoReal, pesoCubado);
    }
}

