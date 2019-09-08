package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.filter.PessoaFilter;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery{

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Pessoa> filtrar(PessoaFilter pessoaFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);
		Predicate[] predicates = criarRestricoes(pessoaFilter, builder, root);
		criteria.where(predicates);
		TypedQuery<Pessoa> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);		
		return new PageImpl<>(query.getResultList());
	}


	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	//
	private Predicate[] criarRestricoes(PessoaFilter pessoaFilter, CriteriaBuilder builder, Root<Pessoa> root) {
		List<Predicate> predicates = new ArrayList<>();
		if(!StringUtils.isEmpty(pessoaFilter.getNome())) {
			predicates.add(builder.like(builder.lower(root.get("nome")), "%" + pessoaFilter.getNome().toLowerCase() + "%"));
		}
		if(pessoaFilter.getAtivo() != null) {
			predicates.add(builder.equal(root.get("ativo"), pessoaFilter.getAtivo()));
		}
		if(!StringUtils.isEmpty(pessoaFilter.getLogradouro())) {
			predicates.add(builder.like(builder.lower(root.get("endereco").get("logradouro")), "%" + pessoaFilter.getLogradouro().toLowerCase() + "%"));
		}
		if(!StringUtils.isEmpty(pessoaFilter.getBairro())) {
			predicates.add(builder.like(builder.lower(root.get("endereco").get("bairro")), "%" + pessoaFilter.getBairro().toLowerCase() + "%"));
		}
		if(!StringUtils.isEmpty(pessoaFilter.getCidade())) {
			predicates.add(builder.like(builder.lower(root.get("endereco").get("cidade")), "%" + pessoaFilter.getCidade().toLowerCase() + "%"));
		}
		if(!StringUtils.isEmpty(pessoaFilter.getEstado())) {
			predicates.add(builder.like(builder.lower(root.get("endereco").get("estado")), "%" + pessoaFilter.getEstado().toLowerCase() + "%"));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
}