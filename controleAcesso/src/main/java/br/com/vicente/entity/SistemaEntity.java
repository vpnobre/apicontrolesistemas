package br.com.vicente.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SISTEMA")
public class SistemaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3487239864576193417L;

	@Id
	@SequenceGenerator(name = "sequencia_sistema", sequenceName = "sequencia_sistema")
	@GeneratedValue(generator = "sequencia_sistema")
	@Column(name = "CD_SISTEMA", unique = true, nullable = false)
	private Long id;

	@Column(name = "NOME", nullable = false)
	private String nome;
	
	@Column(name = "SIGLA", nullable = false, length = 20)
	private String sigla;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SistemaEntity other = (SistemaEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
