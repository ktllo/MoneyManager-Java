package org.leolo.moneymanager.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyUpdater extends Job{
	private static final Logger log = LoggerFactory.getLogger(CurrencyUpdater.class);
	
	public CurrencyUpdater(JobManager parent){
		super(parent);
	}
	
	public void content(){
		String baseCurrency = ConfigurationManager.getInstance().getConfig().get("BASE_CURRENCY", "USD");
		log.info("Obtaining exchange rate from api.exchangeratesapi.io with base currency {}",baseCurrency);
		HashMap<String, Double> rateMap = new HashMap<>();
		SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
		dateOnly.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date rateDate = new Date();
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
	        	JSONObject rates = (JSONObject) json.get("rates");
	        	for(String currency:rates.keySet()){
	        		rateMap.put(currency, rates.getDouble(currency));
	        	}
	        }else{
	        	log.warn("Rates not available. Unable to update.");
	        	return;
	        }
	        if(json.has("date")){
	        	try{
	        		rateDate = dateOnly.parse(json.getString("date"));
	        	}catch(ParseException e){
	        		log.warn(e.getMessage(), e);
	        		log.warn("Using today's date as rate date");
	        	}
	        }else{
	        	log.warn("Date not found.");
	        	log.warn("Using today's date as rate date");
	        }
		}catch(IOException|JSONException e){
			log.error(e.getMessage(), e);
		}
        log.info("{} currency downloaded.", rateMap.size());
        log.info("Rate date is {}", getDate(rateDate));
        final String SQL1 = "SELECT 1 FROM currencyRate WHERE currencyCode = ? AND updatedDate = ?";
        final String SQL2 = "INSERT INTO currencyRate VALUES (?,?,?)";
        Connection conn=null;
        PreparedStatement pstmt1=null;
        PreparedStatement pstmt2=null;
        ResultSet rs = null;
        try{
        	conn = DatabaseManager.getInstance().getConnection();
        	pstmt1 = conn.prepareStatement(SQL1);
        	pstmt1.setTimestamp(2, getDate(rateDate));
        	pstmt2 = conn.prepareStatement(SQL2);
        	pstmt2.setTimestamp(2, getDate(rateDate));
        	for(String code:rateMap.keySet()){
        		pstmt1.setString(1, code);
        		rs = pstmt1.executeQuery();
        		if(!rs.next()){
        			log.debug("Loading rate for {} @ {} on {}", code, rateMap.get(code), rateDate);
	        		pstmt2.setString(1, code);
	        		pstmt2.setDouble(3, rateMap.get(code));
	        		pstmt2.executeUpdate();
        		}else{
        			log.debug("Rate for {}  on {} already exists", code, rateDate);
        		}
        		if(rs!=null){
        			rs.close();
        			rs = null;
        		}
        	}
        	conn.commit();
        }catch(SQLException e){
        	log.error(e.getMessage(), e);
        }finally{
			try {
				if(rs != null)
					rs.close();
				if(pstmt1!=null)
					pstmt1.close();
				if(pstmt2!=null)
						pstmt2.close();
				if(conn!=null)
	        		conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
        }
        
	}
	
	private Timestamp getDate(Date d){
		log.debug("Input  : {}",d);
		long lg = d.getTime();
		lg = lg / 86400000;
		lg = lg * 86400000;
		lg = lg + 14*3600000 + 1800000;
		Timestamp ts = new java.sql.Timestamp(lg);
		log.debug("Output : {}",ts);
		return ts;
	}
}
