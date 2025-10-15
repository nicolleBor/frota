package com.example.frota.caixa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/caixa")
public class CaixaController {

    @Autowired
    private CaixaService caixaService;

    @Autowired
    private CaixaMapper caixaMapper;

    @GetMapping
    public String listaCaixas(Model model) {
        model.addAttribute("listaCaixas", caixaService.procurarTodos());
        return "caixa/listagem";
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
        AtualizacaoCaixa dto;
        if (id != null) {
            Caixa caixa = caixaService.procurarPorId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrada"));
            dto = caixaMapper.toAtualizacaoDto(caixa);
        } else {
            dto = new AtualizacaoCaixa(null, "", "", 0.0, 0.0, 0.0, 0.0);
        }
        model.addAttribute("caixa", dto);
        return "caixa/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("caixa") @Valid AtualizacaoCaixa dto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            return "caixa/formulario";
        }
        try {
            Caixa caixaSalva = caixaService.salvarOuAtualizar(dto);
            String mensagem = dto.id() != null
                    ? "Caixa '" + caixaSalva.getNome() + "' atualizada com sucesso!"
                    : "Caixa '" + caixaSalva.getNome() + "' criada com sucesso!";
            redirectAttributes.addFlashAttribute("message", mensagem);
            return "redirect:/caixa";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caixa/formulario" + (dto.id() != null ? "?id=" + dto.id() : "");
        }
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteCaixa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caixaService.apagarPorId(id);
            redirectAttributes.addFlashAttribute("message", "Caixa " + id + " foi apagada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/caixa";
    }
}
