package edu.purdue.cs.tornado.performance;

public class SpoutInfoAggrecate {
	String id;
	Long transferred;
	Long emitted;
	Long failed;
	Long acked;
	Double completeLatency;
	int numOfExecuters;
	String uptime;
	
	public SpoutInfoAggrecate(String id){
		this.id = id;
		transferred=(long) 0;
		emitted=(long) 0;
		failed=(long) 0;
		completeLatency =0.0;
		numOfExecuters=0;
		acked=(long) 0;
		uptime="";
	}

	public void addSpoutStats(Long transferred,Long emitted,Long acked,	Long failed, Double completeLatency, int upTime){
		this.numOfExecuters ++;
		this.transferred+=(transferred==null?0:transferred);
		this.emitted+=(emitted==null?0:emitted);
		this.failed += (failed==null?0:failed);
		this.acked +=(acked==null?0:acked);
		this.completeLatency +=(completeLatency==null?0:completeLatency);
		this.uptime +=(upTime+"_");
	}
	public Long getAcked() {
		return acked;
	}

	public void setAcked(Long acked) {
		this.acked = acked;
	}

	@Override
	public String toString()
	{
		return "Spid,"+id+" , emitt,"+emitted+" , ack,"+acked+" , failed,"+failed+" , completeLatency,"+completeLatency/numOfExecuters+", upTime,"+uptime+",";
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getTransferred() {
		return transferred;
	}

	public void setTransferred(Long transferred) {
		this.transferred = transferred;
	}


	public Long getEmitted() {
		return emitted;
	}

	public void setEmitted(Long emitted) {
		this.emitted = emitted;
	}

	public Long getFailed() {
		return failed;
	}

	public void setFailed(Long failed) {
		this.failed = failed;
	}



	public int getNumOfExecuters() {
		return numOfExecuters;
	}

	public void setNumOfExecuters(int numOfExecuters) {
		this.numOfExecuters = numOfExecuters;
	}

}
