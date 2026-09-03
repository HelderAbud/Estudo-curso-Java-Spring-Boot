package io.github.cursodsousa.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // adiciona esse import

@SpringBootApplication
@EnableJpaAuditing // adiciona essa linha aqui
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
