package com.example.frota.caminhao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.frota.marca.MarcaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/caminhao")
public class CaminhaoController {
	
	@Autowired
	private CaminhaoService caminhaoService;
	
	@Autowired
    private CaminhaoMapper caminhaoMapper;
	
	@Autowired
	private MarcaService marcaService;
	
	@GetMapping                 
	public String carregaPaginaFormulario ( Model model){ 
		model.addAttribute("listaVeiculos", caminhaoService.procurarTodos());
	    return "caminhao/listagem";              
	} 
	////////////////////////
	//Novo GetMapping com DTO e Mapper
	@GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
		AtualizacaoCaminhao dto;
        if (id != null) {
            //edição: Carrega dados existentes
            Caminhao caminhao = caminhaoService.procurarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado"));
            dto = caminhaoMapper.toAtualizacaoDto(caminhao);
        } else {
            // criação: DTO vazio
            dto = new AtualizacaoCaminhao(null, "", "", null, null, null);
        }
        model.addAttribute("caminhao", dto);
        model.addAttribute("marcas", marcaService.procurarTodos());
        return "caminhao/formulario";
    }
	
//	// Para criação sem passar o ID
//	@GetMapping("/formulario")
//	public String novoCaminhao(Model model  ) {
//		model.addAttribute("caminhao", new Caminhao());
//		model.addAttribute("marcas", marcaService.procurarTodos());
//		return "caminhao/formulario";
//	}
	
	@GetMapping ("/formulario/{id}")    
	public String carregaPaginaFormulario (@PathVariable("id") Long id, Model model,
			RedirectAttributes redirectAttributes) {
		AtualizacaoCaminhao dto;
		try {
			if(id != null) {
				Caminhao caminhao = caminhaoService.procurarPorId(id)
						.orElseThrow(() -> new EntityNotFoundException("Caminhao não encontrado"));
				model.addAttribute("marcas", marcaService.procurarTodos());
				//mapear caminhão para AtualizacaoCaminhao
				dto = caminhaoMapper.toAtualizacaoDto(caminhao);
				model.addAttribute("caminhao", dto);
			}
			return "caminhao/formulario";
		} catch (EntityNotFoundException e) {
			//resolver erros
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/caminhao";
		}
	}
	

	@PostMapping("/salvar")
    public String salvar(@ModelAttribute("caminhao") @Valid AtualizacaoCaminhao dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
		if (result.hasErrors()) {
	        // Recarrega dados necessários para mostrar erros
	        model.addAttribute("marcas", marcaService.procurarTodos());
	        return "caminhao/formulario";
	    }
	    try {
	        Caminhao caminhaoSalvo = caminhaoService.salvarOuAtualizar(dto);
	        String mensagem = dto.id() != null 
	            ? "Caminhão '" + caminhaoSalvo.getPlaca() + "' atualizado com sucesso!"
	            : "Caminhão '" + caminhaoSalvo.getPlaca() + "' criado com sucesso!";
	        redirectAttributes.addFlashAttribute("message", mensagem);
	        return "redirect:/caminhao";
	    } catch (EntityNotFoundException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/caminhao/formulario" + (dto.id() != null ? "?id=" + dto.id() : "");
	    }
	}
	
	@GetMapping("/delete/{id}")
	@Transactional
	public String deleteTutorial(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		try {
			caminhaoService.apagarPorId(id);
			redirectAttributes.addFlashAttribute("message", "O caminhao " + id + " foi apagado!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/caminhao";
	}
	
	
}
