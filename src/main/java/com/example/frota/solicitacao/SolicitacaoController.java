package com.example.frota.solicitacao;

import com.example.frota.api.externo.FreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.frota.caixa.CaixaService;
import com.example.frota.produto.ProdutoService;
import com.example.frota.caminhao.CaminhaoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import java.util.Map;

@Controller
@RequestMapping("/solicitacao")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoMapper solicitacaoMapper;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CaixaService caixaService;

    @Autowired
    private CaminhaoService caminhaoService;

    @Autowired
    private FreteService freteService;

    @GetMapping
    public String listaSolicitacoes(Model model) {
        model.addAttribute("listaSolicitacoes", solicitacaoService.procurarTodos());
        return "solicitacao/listagem";
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
        AtualizacaoSolicitacao dto;
        if (id != null) {
            SolicitacaoTransporte solicitacao = solicitacaoService.procurarPorId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));
            dto = solicitacaoMapper.toAtualizacaoDto(solicitacao);
        } else {
            dto = new AtualizacaoSolicitacao(null, null, null, null, 1, "", "", "", "PENDENTE");
        }
        model.addAttribute("solicitacao", dto);
        model.addAttribute("produtos", produtoService.procurarTodos());
        model.addAttribute("caixas", caixaService.procurarTodos());
        model.addAttribute("caminhoes", caminhaoService.procurarTodos());
        return "solicitacao/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("solicitacao") @Valid AtualizacaoSolicitacao dto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("produtos", produtoService.procurarTodos());
            model.addAttribute("caixas", caixaService.procurarTodos());
            model.addAttribute("caminhoes", caminhaoService.procurarTodos());
            return "solicitacao/formulario";
        }
        try {
            solicitacaoService.salvarOuAtualizar(dto);
            String mensagem = dto.id() != null
                    ? "Solicitação atualizada com sucesso!"
                    : "Solicitação criada com sucesso!";
            redirectAttributes.addFlashAttribute("message", mensagem);
            return "redirect:/solicitacao";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/solicitacao/formulario" + (dto.id() != null ? "?id=" + dto.id() : "");
        }
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteSolicitacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            solicitacaoService.apagarPorId(id);
            redirectAttributes.addFlashAttribute("message", "Solicitação " + id + " apagada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/solicitacao";
    }

    @GetMapping("/calcular-frete")
    public String calcularFrete(@RequestParam String origem, 
                              @RequestParam String destino,
                              @RequestParam Long produtoId,
                              @RequestParam(required = false) Long caminhaoId,
                              @RequestParam(required = false) Long caixaId,
                              Model model) {
        try {
            var produto = produtoService.procurarPorId(produtoId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            
            // Determinar tipo de cálculo baseado no produto (mesma lógica do service)
            String tipoCalculo = determinarTipoCalculo(produto);
            
            // Calcular frete usando o serviço externo
            var resultadoFrete = freteService.calcularFrete(origem, destino, produto.getPeso(), tipoCalculo);
            
            // Se temos caminhão e caixa, calcular peso cubado também
            if (caminhaoId != null && caixaId != null) {
                var caminhao = caminhaoService.procurarPorId(caminhaoId)
                        .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado"));
                caixaService.procurarPorId(caixaId)
                        .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrada"));
                
                // Calcular peso cubado
                double volumeProduto = produto.getVolume();
                double pesoCubado = caminhao.calcularPesoCubado(volumeProduto);
                double pesoReal = produto.getPeso();
                double pesoCobrado = Math.max(pesoCubado, pesoReal);
                
                // Recalcular valor total considerando peso cubado
                double distanciaKm = (Double) resultadoFrete.get("distanciaKm");
                double valorPorKm = (Double) resultadoFrete.get("valorPorKm");
                double valorPedagio = (Double) resultadoFrete.get("pedagio");
                double valorTotalComCubagem = pesoCobrado * valorPorKm * distanciaKm + valorPedagio;
                
                // Adicionar informações de cubagem ao resultado
                resultadoFrete.put("pesoCubado", pesoCubado);
                resultadoFrete.put("pesoReal", pesoReal);
                resultadoFrete.put("pesoCobrado", pesoCobrado);
                resultadoFrete.put("pesoCubadoMaior", pesoCubado > pesoReal);
                resultadoFrete.put("valorTotalComCubagem", valorTotalComCubagem);
                resultadoFrete.put("volumeProduto", volumeProduto);
            }
            
            model.addAttribute("resultado", resultadoFrete);
            model.addAttribute("produto", produto);
            model.addAttribute("origem", origem);
            model.addAttribute("destino", destino);
            
            return "solicitacao/resultado-frete";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao calcular frete: " + e.getMessage());
            // Adicionar os dados necessários para o formulário
            model.addAttribute("solicitacao", new AtualizacaoSolicitacao(null, null, null, null, 1, origem, destino, "", "PENDENTE"));
            model.addAttribute("produtos", produtoService.procurarTodos());
            model.addAttribute("caixas", caixaService.procurarTodos());
            model.addAttribute("caminhoes", caminhaoService.procurarTodos());
            return "solicitacao/formulario";
        }
    }
    
    
    private String determinarTipoCalculo(com.example.frota.produto.Produto produto) {
        double peso = produto.getPeso();
        double volume = produto.getVolume();
        
        // Lógica para determinar o tipo de cálculo (mesma do service)
        if (peso > 100) {
            return "peso"; // Produtos pesados
        } else if (volume > 0.5) {
            return "volume"; // Produtos volumosos
        } else {
            return "caixa"; // Produtos leves e pequenos
        }
    }
}
