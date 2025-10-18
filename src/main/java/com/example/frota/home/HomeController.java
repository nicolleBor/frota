package com.example.frota.home;

import com.example.frota.caixa.CaixaService;
import com.example.frota.caminhao.CaminhaoService;
import com.example.frota.produto.ProdutoService;
import com.example.frota.solicitacao.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private CaminhaoService caminhaoService;
    
    @Autowired
    private CaixaService caixaService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private SolicitacaoService solicitacaoService;

    @GetMapping("/")
    public String home() {
        return "redirect:/home/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estatísticas para o dashboard
        long totalCaminhoes = caminhaoService.contarTotal();
        long totalCaixas = caixaService.contarTotal();
        long totalProdutos = produtoService.contarTotal();
        long totalSolicitacoes = solicitacaoService.contarTotal();
        
        // Caminhões recentes
        var caminhoesRecentes = caminhaoService.procurarTodos();
        var caixasRecentes = caixaService.procurarTodos();
        var produtosRecentes = produtoService.procurarTodos();
        var solicitacoesRecentes = solicitacaoService.procurarTodos();
        
        model.addAttribute("totalCaminhoes", totalCaminhoes);
        model.addAttribute("totalCaixas", totalCaixas);
        model.addAttribute("totalProdutos", totalProdutos);
        model.addAttribute("totalSolicitacoes", totalSolicitacoes);
        
        model.addAttribute("caminhoesRecentes", caminhoesRecentes.size() > 5 ? 
            caminhoesRecentes.subList(0, 5) : caminhoesRecentes);
        model.addAttribute("caixasRecentes", caixasRecentes.size() > 5 ? 
            caixasRecentes.subList(0, 5) : caixasRecentes);
        model.addAttribute("produtosRecentes", produtosRecentes.size() > 5 ? 
            produtosRecentes.subList(0, 5) : produtosRecentes);
        model.addAttribute("solicitacoesRecentes", solicitacoesRecentes.size() > 5 ? 
            solicitacoesRecentes.subList(0, 5) : solicitacoesRecentes);
        
        return "home/dashboard";
    }
}
