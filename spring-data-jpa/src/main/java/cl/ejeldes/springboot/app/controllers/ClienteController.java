package cl.ejeldes.springboot.app.controllers;

import cl.ejeldes.springboot.app.models.entity.Cliente;
import cl.ejeldes.springboot.app.models.service.ClienteService;
import cl.ejeldes.springboot.app.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes("cliente")
public class ClienteController {


    private static final String UPLOADS_FOLDER = "uploads";
    private ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Cliente cliente = clienteService.findById(id);

        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe ne la base de datos");
            return "redirect:/listar";
        }

        model.put("cliente", cliente);
        model.put("titulo", "Detalle cliente " + cliente.getNombre());

        return "ver";
    }

    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public String listar(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {

        Pageable pageable = PageRequest.of(page, 6);
        Page<Cliente> clientes = clienteService.findAll(pageable);
        PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @GetMapping("/form")
    public String crear(Map<String, Object> model) {
        Cliente cliente = new Cliente();
        model.put("titulo", "Crear cliente");
        model.put("cliente", cliente);
        return "form";
    }

    @GetMapping("/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Cliente cliente = null;

        if (id > 0) {
            cliente = clienteService.findById(id);

            if (cliente == null) {
                flash.addFlashAttribute("error", "El id del cliente no existe en la base de datos");
                return "redirect:listar";
            }
        } else {
            flash.addFlashAttribute("error", "El id del cliente no puede ser 0");
            return "redirect:listar";
        }

        model.put("titulo", "Editar Cliente");
        model.put("cliente", cliente);

        return "form";
    }

    @PostMapping("/form")
    public String guardar(@Valid Cliente cliente, BindingResult result, @RequestParam("file") MultipartFile foto,
                          RedirectAttributes flash, Model model,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }

        if (!foto.isEmpty()) {

            if (cliente.getId() != null && cliente.getId() > 0
                    && cliente.getFoto() != null && cliente.getFoto().length() > 0) {

                Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(cliente.getFoto()).toAbsolutePath();
                File archivo = rootPath.toFile();

                if (archivo.exists() && archivo.canRead()) {
                    if (archivo.delete()) {
                        flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada correctamente");
                    }
                }
            }

            String uniqueFileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
            Path absoluteRootPath = Paths.get(UPLOADS_FOLDER).resolve(uniqueFileName).toAbsolutePath();
            try {
                Files.copy(foto.getInputStream(), absoluteRootPath);
                flash.addFlashAttribute("info", "Has subido correctamente" + uniqueFileName);

                cliente.setFoto(uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String msg = cliente.getId() != null ? "Cliente editado con exito" : "Cliente creado con exito";

        clienteService.save(cliente);
        status.setComplete();
        flash.addFlashAttribute("success", msg);
        return "redirect:listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {

        if (id > 0) {

            Cliente cliente = clienteService.findById(id);

            clienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con exito");

            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(cliente.getFoto()).toAbsolutePath();
            File archivo = rootPath.toFile();

            if (archivo.exists() || archivo.canRead()) {
                if (archivo.delete()) {
                    flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada correctamente");
                }
            }
        }


        return "redirect:/listar";
    }
}
