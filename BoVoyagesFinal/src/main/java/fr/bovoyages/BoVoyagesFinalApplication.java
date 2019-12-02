package fr.bovoyages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Controller
@SpringBootApplication
@EnableSwagger2
public class BoVoyagesFinalApplication implements ErrorController{

	public static void main(String[] args) {
		SpringApplication.run(BoVoyagesFinalApplication.class, args);
	}
private static final String PATH="/error";
	@RequestMapping(value=PATH)
	public String error() {
		return "forward:/index.html";
	}
	@Override
	public String getErrorPath() {
	return PATH;
	}

	
	@Bean
	public Docket api() {
		//http://localhost:7070/swagger-ui.html pour la doc
		return new Docket(DocumentationType.SWAGGER_2)
					.select()
					.apis(RequestHandlerSelectors.basePackage("fr.bovoyages.restFront"))
					.build()
					.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("REST API de gestion de films")
				.contact(new Contact("Commercial", "http://bovoyages.net", "noreply@bovoyages.net"))
				.version("0.alpha")
				.build();
	}

}
