package com.example.frota.solicitacao;

import com.example.frota.caminhao.AtualizacaoCaminhao;
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
            dto = new AtualizacaoSolicitacao(null, null, null, null, 0.0, 0.0);
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
            SolicitacaoTransporte solSalva = solicitacaoService.salvarOuAtualizar(dto);
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

    @PutMapping
    @Transactional
    public String atualizar (AtualizacaoSolicitacao dados) {
        solicitacaoService.salvarOuAtualizar(dados);
        return "redirect:marca";
    }
}
