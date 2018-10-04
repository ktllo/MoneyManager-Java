package org.leolo.moneymanager.batch;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformBatchJob {
	
	private static final Logger log = LoggerFactory.getLogger(PerformBatchJob.class);
	
	public static void main(String [] args){
		log.info("Batch job started.");
		final long start = System.currentTimeMillis();
		if(args.length<1){
			//Invalid run
			log.error("FATAL ERROR: Base directory for the php version is required.");
			System.exit(1);
		}
		//Check is the PHP setting folder writable
		File f = new File(args[0]);
		if(!f.exists()){
			log.error("FATAL ERROR: Base directory for the php version does not exists");
			System.exit(1);
		}
		if(!f.canRead()||!f.canWrite()){
			log.error("FATAL ERROR: Base directory for the php version cannot be read or write");
			System.exit(1);
		}
		File phpConf = new File(args[0]+"/"+"config.php");
		if(!phpConf.exists()){
			log.error("FATAL ERROR: PHP version of config file does not exists");
			System.exit(1);
		}
		File javaConf = new File(args[0]+"/"+"config.dat");
		if(!javaConf.exists()){
			log.info("Config file does not exists. Will build a new one.");
			mkConf();
		}
		
	}
	
	private static void mkConf(){
		//TODO: Call the php binary to build the java version of conf.
	}
}
