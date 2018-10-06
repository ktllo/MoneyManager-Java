package org.leolo.moneymanager.batch;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
	
	private Map<String, Object> config = new HashMap<>();
	
	public void addAll(Map<String, ? extends Object> map){
		config.putAll(map);
	}
	
	public void add(String key, Object value){
		config.put(key, value);
	}
	
	public String get(String key){
		return get(key, null);
	}
	
	public String get(String key, String defaultValue){
		Object obj = config.get(key);
		return obj==defaultValue?null:obj.toString();
	}
	
	
}
