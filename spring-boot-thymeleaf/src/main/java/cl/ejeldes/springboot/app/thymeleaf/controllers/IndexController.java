package cl.ejeldes.springboot.app.thymeleaf.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Value("${application.controllers.mensaje}")
    private String mensaje;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("mensaje", this.mensaje);
        return "hola";
    }

}
