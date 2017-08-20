package br.com.vicente.dao;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import br.com.vicente.entity.AcessoEntity;
import br.com.vicente.facade.GenericDAO;



public class AcessoDAO extends GenericDAO<AcessoEntity>{
	
	public AcessoDAO(){
	}
	
	/**
	 * CRIA UM NOVO REGISTRO NO BANCO DE DADOS
	 * */
	public Boolean salvar(AcessoEntity acesso){
		return save(acesso);
	}
	
	/**
	 * ALTERA UM REGISTRO CADASTRADO
	 * @throws SQLException 
	 * */
	public AcessoEntity alterar(AcessoEntity acesso) throws SQLException{
		return createOrUpdate(acesso);
	}
	
	/**
	 * RETORNA TODOS ACESSOS
	 * */
	@SuppressWarnings("unchecked")
	public List<AcessoEntity> todosAcessos(HashMap <String, Object> map , String namedQuery){
		return createNamedQuery(namedQuery, map);
	}
	
	/**
	 * CONSULTA UM ACESSO CADASTRADO PELO CÓDIGO
	 * */
	public AcessoEntity getAcesso(HashMap <String, Object> map , String namedQuery){
	    return createNamedQuerySingle(namedQuery, map);
	}
	
	/**
	 * EXCLUINDO UM REGISTRO PELO CÓDIGO
	 * @throws SQLException 
	**/
	public Boolean excluir(HashMap <String, Object> map , String namedQuery) throws SQLException{
		AcessoEntity acesso = this.getAcesso(map, namedQuery);
		return remove(acesso);
	}
}
