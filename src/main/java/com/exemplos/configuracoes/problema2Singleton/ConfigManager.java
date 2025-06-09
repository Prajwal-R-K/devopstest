/**
 * 
 */
package com.exemplos.configuracoes.problema2Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Classe que acessa configurações de arquivo properties
 */
public class ConfigManager {
	/**Instância única do Singleton*/
	private static ConfigManager instance;

	/**Objeto para armazenar as configurações*/
	private Properties properties;
	/**Construtor privado para impedir múltiplas instâncias*/
	private ConfigManager() {
		properties = new Properties();
		/**buscar as configurações no arquivo*/
		loadConfigurations();
	}
	
	/** Método responsável por carregar as configurações do arquivo
	 * @param String name, language, version, currency, timeout
	 * @return String conteúdo da chave
	 * */
	private void loadConfigurations() {

		/**leitor*/
		InputStream file =
				this.getClass().getResourceAsStream("config.properties"
						);
		try{
			properties.load(file);
			System.out.println("Configurações carregadas com sucesso.");
			file.close();
		}
		catch(IOException e){
			System.out.println("Arquivo de configuração não encontrado!");

			e.printStackTrace();
		}

	}
	/**Método público para obter a instância única*/
	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}
	/**Método para obter um valor da configuração*/
	public String getConfig(String key) {
		return properties.getProperty(key);
	}
}
