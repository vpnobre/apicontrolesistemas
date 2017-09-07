package br.com.vicente.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TB_ACESSO")
@NamedQueries({
		@NamedQuery(name = "listaTodosAcesso", query = "select c from AcessoEntity c order by c.id asc"),
		@NamedQuery(name = "getAcesso", query = "select c from AcessoEntity c where c.id = :id"),
		@NamedQuery(name = "retornarAcessoSistema", query = "select c from AcessoEntity c "
				+ " where c.usuario = :usuario and c.senha = :senha and c.macCliente= :mac"),
        @NamedQuery(name = "pesquisarAcessoSistema", query = "select c from AcessoEntity c "
		+ " where c.usuario = :usuario and c.senha = :senha and c.macCliente= :mac and c.token= :token") })

public class AcessoEntity {

	@Id
	@SequenceGenerator(name = "Generator_acesso", sequenceName = "sequence_acesso")
	@GeneratedValue(generator = "Generator_acesso")
	@Column(name = "CD_ACESSO", unique = true)
	private Long id;

	@Column(name = "USUARIO")
	private String usuario;

	@Column(name = "SENHA")
	private String senha;

	@Column(name = "TOKEN", unique = true)
	private String token;
	
	@Column(name = "CHAVE_TOKEN", unique = true)
	private String chaveToken;

	@Column(name = "MAC_CLIENTE")
	private String macCliente;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO", nullable = false)
	private Date dataInicioAcesso;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FIM", nullable = false)
	private Date dataFinalAcesso;

	@ManyToOne
	@JoinColumn(name = "CD_SISTEMA")
	private SistemaEntity sistemaEntity;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMacCliente() {
		return macCliente;
	}

	public void setMacCliente(String macCliente) {
		this.macCliente = macCliente;
	}

	public Date getDataInicioAcesso() {
		return dataInicioAcesso;
	}

	public void setDataInicioAcesso(Date dataInicioAcesso) {
		this.dataInicioAcesso = dataInicioAcesso;
	}

	public Date getDataFinalAcesso() {
		return dataFinalAcesso;
	}

	public void setDataFinalAcesso(Date dataFinalAcesso) {
		this.dataFinalAcesso = dataFinalAcesso;
	}

	public String getChaveToken() {
		return chaveToken;
	}

	public void setChaveToken(String chaveToken) {
		this.chaveToken = chaveToken;
	}

	public SistemaEntity getSistemaEntity() {
		return sistemaEntity;
	}

	public void setSistemaEntity(SistemaEntity sistemaEntity) {
		this.sistemaEntity = sistemaEntity;
	}
}
