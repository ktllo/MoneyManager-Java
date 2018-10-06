package org.leolo.moneymanager.batch;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
	private static DatabaseManager instance = null;
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
	
	private BasicDataSource datasource;
	
	public static DatabaseManager getInstance(){
		if(instance==null){
			instance = new DatabaseManager();
		}
		return instance;
	}
	
	private DatabaseManager(){
		Configuration conf = ConfigurationManager.getInstance().getConfig();
	}
	
	public Connection getConnection() throws SQLException{
		return datasource.getConnection();
	}
}
