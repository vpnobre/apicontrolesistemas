package br.com.vicente.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Acesso")
public class Acesso {

	@XmlElement(name = "id")
	private Long id;
	
	@XmlElement(name = "usuario")
	private String usuario;
	
	@XmlElement(name = "senha")
	private String senha;
	
	@XmlElement(name = "token")
	private String token;
	
	@XmlElement(name = "macCliente")
	private String macCliente;
	
	@XmlElement(name = "dataInicioAcesso")
	private String dataInicioAcesso;
	
	@XmlElement(name = "dataFinalAcesso")
	private String dataFinalAcesso;	

	public Acesso() {
		super();
	}

	public Acesso(Long id, String usuario, String senha, String token,
			String macCliente, String dataInicioAcesso, String dataFinalAcesso) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.senha = senha;
		this.token = token;
		this.macCliente = macCliente;
		this.dataInicioAcesso = dataInicioAcesso;
		this.dataFinalAcesso = dataFinalAcesso;
	}
	
	public Acesso(String token){
		this.token = token;
	}

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

	public String getDataInicioAcesso() {
		return dataInicioAcesso;
	}

	public void setDataInicioAcesso(String dataInicioAcesso) {
		this.dataInicioAcesso = dataInicioAcesso;
	}

	public String getDataFinalAcesso() {
		return dataFinalAcesso;
	}

	public void setDataFinalAcesso(String dataFinalAcesso) {
		this.dataFinalAcesso = dataFinalAcesso;
	}

	

}
