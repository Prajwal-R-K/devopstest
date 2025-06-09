package com.exemplos.configuracoes.problema2Singleton;

/**
 * @author deboracarvalho
 * @version 2.0
 * since fev/2025
 */
public class App {
	public static void main(String[] args) {
		/**Obtendo a instância única do gerenciador de configurações*/ 
		ConfigManager config = ConfigManager.getInstance(); 
		/**Acessando configurações*/ 
		System.out.println("Nome do Aplicativo: " + 
				config.getConfig("app.name")); 
		System.out.println("Versão: " + 
				config.getConfig("app.version")); 
		System.out.println("Idioma: " + 
				config.getConfig("app.language")); 
		System.out.println("Moeda: " + 
				config.getConfig("currency")); 
		System.out.println("Tempo limite: " + 
				config.getConfig("timeout") + " segundos");  

	}
}
