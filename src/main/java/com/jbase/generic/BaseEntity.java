package com.jbase.generic;

import java.util.Calendar;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * @author michel.pech
 */

@MappedSuperclass
public abstract class BaseEntity {
	
	private Calendar dataInclusao;
	private Calendar dataAtualizacao;
	private String usuarioInclusao;
	private String usuarioAtualizacao;
	
	@Column(name="DATA_INCLUSAO")
	public Calendar getDataInclusao() {
		return dataInclusao;
	}
	
	public void setDataInclusao(Calendar dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	
	@Column(name="DATA_ATUALIZACAO")
	public Calendar getDataAtualizacao() {
		return dataAtualizacao;
	}
	
	public void setDataAtualizacao(Calendar dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	
	@Column(name="USUARIO_INCLUSAO")
	public String getUsuarioInclusao() {
		return usuarioInclusao;
	}
	
	public void setUsuarioInclusao(String usuarioInclusao) {
		this.usuarioInclusao = usuarioInclusao;
	}
	
	@Column(name="USUARIO_ATUALIZACAO")
	public String getUsuarioAtualizacao() {
		return usuarioAtualizacao;
	}
	
	public void setUsuarioAtualizacao(String usuarioAtualizacao) {
		this.usuarioAtualizacao = usuarioAtualizacao;
	}		
	
}
