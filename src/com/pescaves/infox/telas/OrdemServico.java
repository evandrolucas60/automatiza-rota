/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pescaves.infox.telas;

/**
 *
 * @author Pichau
 */
public class OrdemServico {

    private String roteiro;
    private String rota;
    private String veiculo;
    private String motorista;
    private String cliente;
    private String uf;
    private String bairro;
    private String cidade;
    private String endereco;
    private String qtdNotas;
    private String telMotorista;
    private String telAjudante;
    
    
	public OrdemServico(String roteiro, String rota, String veiculo, String motorista, String cliente, String uf,
			String bairro, String cidade, String endereco, String qtdNotas, String telMotorista, String telAjudante) {
		this.roteiro = roteiro;
		this.rota = rota;
		this.veiculo = veiculo;
		this.motorista = motorista;
		this.cliente = cliente;
		this.uf = uf;
		this.bairro = bairro;
		this.cidade = cidade;
		this.endereco = endereco;
		this.qtdNotas = qtdNotas;
		this.telMotorista = telMotorista;
		this.telAjudante = telAjudante;
	}


	public String getRoteiro() {
		return roteiro;
	}


	public void setRoteiro(String roteiro) {
		this.roteiro = roteiro;
	}


	public String getRota() {
		return rota;
	}


	public void setRota(String rota) {
		this.rota = rota;
	}


	public String getVeiculo() {
		return veiculo;
	}


	public void setVeiculo(String veiculo) {
		this.veiculo = veiculo;
	}


	public String getMotorista() {
		return motorista;
	}


	public void setMotorista(String motorista) {
		this.motorista = motorista;
	}


	public String getCliente() {
		return cliente;
	}


	public void setCliente(String cliente) {
		this.cliente = cliente;
	}


	public String getUf() {
		return uf;
	}


	public void setUf(String uf) {
		this.uf = uf;
	}


	public String getBairro() {
		return bairro;
	}


	public void setBairro(String bairro) {
		this.bairro = bairro;
	}


	public String getCidade() {
		return cidade;
	}


	public void setCidade(String cidade) {
		this.cidade = cidade;
	}


	public String getEndereco() {
		return endereco;
	}


	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}


	public String getQtdNotas() {
		return qtdNotas;
	}


	public void setQtdNotas(String qtdNotas) {
		this.qtdNotas = qtdNotas;
	}


	public String getTelMotorista() {
		return telMotorista;
	}


	public void setTelMotorista(String telMotorista) {
		this.telMotorista = telMotorista;
	}


	public String getTelAjudante() {
		return telAjudante;
	}


	public void setTelAjudante(String telAjudante) {
		this.telAjudante = telAjudante;
	}
	
    

}
