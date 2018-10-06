package org.leolo.moneymanager.batch;

public abstract class Job extends Thread {
	
	
	private JobManager manager;
	private JobStatus status = JobStatus.PENDING;
	
	public Job(JobManager manager){
		this.manager = manager;
		manager.registerJob(this);
	}
	
	@Override
	public final void run(){
		setStatus(JobStatus.RUNNING);
		content();
		setStatus(JobStatus.FINISHED);
		synchronized(manager){
			manager.notifyAll();
		}
	}
	
	public abstract void content();

	public JobStatus getStatus() {
		return status;
	}

	private void setStatus(JobStatus status) {
		this.status = status;
	}
}
