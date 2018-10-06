package org.leolo.moneymanager.batch;

public class ConfigurationManager {
	
	private static ConfigurationManager instance = null;
	
	private Configuration config;
	
	public static ConfigurationManager getInstance(){
		if(instance==null){
			instance = new ConfigurationManager();
		}
		return instance;
	}
	
	private ConfigurationManager(){
		config = new Configuration();
	}

	public Configuration getConfig() {
		return config;
	}

	
	
}
