package com.example.algamoney.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	//Se não encontrar não precisa verificar se é diferente de nulo
	//busca na base Usuario pelo campo e-mail
	// neste caso findBy*** o final é variavel
	public Optional<Usuario> findByEmail(String email);
}
