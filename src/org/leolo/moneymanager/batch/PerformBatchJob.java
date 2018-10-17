package org.leolo.moneymanager.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.leolo.util.jobcontrol.JobController;
import org.leolo.util.jobcontrol.JobDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformBatchJob {
	
	private static final Logger log = LoggerFactory.getLogger(PerformBatchJob.class);
	
	public static void main(String [] args){
		new PerformBatchJob().run(args);
	}
	
	private void run(String [] args){
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
		mkConf(args[0]);
		if(DatabaseManager.getInstance().testConnection()){
			log.info("Connection OK");
		}else{
			log.error("FATAL ERROR: Connection error.");
			System.exit(1);
		}
		//Start the jobs
		JobController controller = new JobController(10);
		JobDetailsImpl jdi = new JobDetailsImpl();
		jdi.setJob(new CurrencyUpdater());
		jdi.setJobName("Currency Updater");
		jdi.setJobId("cu-00");
		controller.addJob(jdi);
		controller.start();
		log.info("Done!");
	}
	
	private void mkConf(String base){
		//TODO: Call the php binary to build the java version of conf.
		try {
			log.info("Calling php to get the updated version of configuration.");
			long start = System.currentTimeMillis();
			Process p = Runtime.getRuntime().exec("php "+base+"/mkConf.xphp");
			long end = System.currentTimeMillis();
			log.info("Finished calling php program. Time used {}ms", end-start);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder sb = new StringBuilder();
			while(true){
				String line = stdInput.readLine();
				if(line==null)
					break;
				sb.append(line);
			}
			log.info("JSON size is {} bytes",sb.length());
			log.debug(sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			
			int count = 0;
			for(String key:json.keySet()){
				ConfigurationManager.getInstance().getConfig().add(key, json.get(key));
				log.debug("{}: key={};value={}",++count, key, json.get(key));
			}
		} catch (IOException|JSONException e) {
			log.error(e.getMessage(), e);
		}
	}
}
