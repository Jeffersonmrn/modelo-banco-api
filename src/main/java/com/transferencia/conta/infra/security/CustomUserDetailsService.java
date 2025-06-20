package com.transferencia.conta.infra.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.UsuarioRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario u = usuarioRepository.findByCpf(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		var authorities = new ArrayList<SimpleGrantedAuthority>();

		authorities.add(new SimpleGrantedAuthority(u.getRole().name()));

		return new org.springframework.security.core.userdetails.User(u.getCpf(),

				u.getSenha(), authorities);
	}
}
