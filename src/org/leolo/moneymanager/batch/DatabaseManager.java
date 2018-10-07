package org.leolo.moneymanager.batch;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		datasource = new BasicDataSource();
		datasource.setUrl("jdbc:mysql://"+conf.get("DB_PATH", "localhost")+"/"+conf.get("DB_NAME","mm"));
		datasource.setUsername(conf.get("DB_USER", ""));
		datasource.setPassword(conf.get("DB_PASS", ""));
		datasource.setMinIdle(2);
		datasource.setMaxIdle(5);
	}
	
	public Connection getConnection() throws SQLException{
		Connection conn = datasource.getConnection();
		conn.setAutoCommit(false);
		return conn;
	}

	public boolean getCacheState() {
		return datasource.getCacheState();
	}

	public Driver getDriver() {
		return datasource.getDriver();
	}

	public String getDriverClassName() {
		return datasource.getDriverClassName();
	}

	public ClassLoader getDriverClassLoader() {
		return datasource.getDriverClassLoader();
	}

	public int getNumIdle() {
		return datasource.getNumIdle();
	}

	public boolean isClosed() {
		return datasource.isClosed();
	}
	
	public boolean testConnection(){
		try{
			Connection conn = this.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM DUAL");
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				log.debug("Data is {}", rs.getInt(1));
			}
		}catch(SQLException e){
			log.warn("{}:{}",e.getErrorCode(), e.getMessage());
			return false;
		}
		return true;
		
	}
}
