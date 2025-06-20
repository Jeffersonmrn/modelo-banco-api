package com.transferencia.conta;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.transferencia.conta.enums.Role;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "API - Modelo bancário para fins educativos", version = "1.0", description = "Por Jefferson Moreno - Documentação da API"))

@SpringBootApplication
public class ContaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.findByCpf("0000000000").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setEmail("admin@bank.com");
				admin.setNome("Admin");
				admin.setSenha(passwordEncoder.encode("123456"));
				admin.setCpf("0000000000");
				admin.setRole(Role.ROLE_ADMIN);

				usuarioRepository.save(admin);
				System.out.println("✅ Usuário ADMIN criado com sucesso!");
			} else {
				System.out.println("ℹ️ Usuário ADMIN já existe.");
			}
		};
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
