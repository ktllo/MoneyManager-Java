package org.leolo.moneymanager.batch;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobManager {
	
	private List<Job> jobList = new Vector<>();
	private static final Logger log = LoggerFactory.getLogger(JobManager.class);
	
	protected void registerJob(Job job){
		jobList.add(job);
	}
	
	public void waitAllJobFinish(){
outer:	while(true){
			synchronized(this){
				try {
					this.wait();
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
			for(Job job:jobList){
				if(job.getStatus()!=JobStatus.FINISHED){
					continue outer;
				}
			}
			break;
		}
	}
	
}
