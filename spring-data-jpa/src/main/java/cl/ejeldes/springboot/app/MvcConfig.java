package cl.ejeldes.springboot.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * OUTSIDE THE PROJECT
         */
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + System.getenv("HOME") + "/shared/uploads/");
        /**
         * Inside the project
         */
        String absolutePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
