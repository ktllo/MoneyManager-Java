package org.leolo.moneymanager.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyUpdater extends Job{
	private JobManager parent;
	private static final Logger log = LoggerFactory.getLogger(CurrencyUpdater.class);
	
	public CurrencyUpdater(JobManager parent){
		super(parent);
		this.parent = parent;
	}
	
	public void content(){
		String baseCurrency = ConfigurationManager.getInstance().getConfig().get("BASE_CURRENCY", "USD");
		log.info("Obtaining exchange rate from api.exchangeratesapi.io with base currency {}",baseCurrency);
		try{
			String url = "https://api.exchangeratesapi.io/latest?base="+baseCurrency;
			URL apiLink = new URL(url);
			log.info("Connecting to {}", url);
	        URLConnection urlConn = apiLink.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	        StringBuilder sb =  new StringBuilder();
	        while(true){
	        	String line = in.readLine();
	        	if(line == null)
	        		break;
	        	sb.append(line);
	        }
	        in.close();
	        log.info("Finish reading. {} bytes read.", sb.length());
	        JSONObject json = new JSONObject(sb.toString());
	        if(json.has("rates")){
	        	JSONArray rates = (JSONArray) json.get("rates");
	        }else{
	        	log.warn("Rates not available");
	        }
		}catch(IOException|JSONException e){
			log.error(e.getMessage(), e);
		}
	}
}
