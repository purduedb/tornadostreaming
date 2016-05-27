//This code is adopted  from this URL 
//http://jayatiatblogs.blogspot.com/2013/05/extracting-storm-web-ui-parameter-values.html

package edu.purdue.cs.tornado.performance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.storm.generated.BoltStats;
import org.apache.storm.generated.ClusterSummary;
import org.apache.storm.generated.ErrorInfo;
import org.apache.storm.generated.ExecutorSpecificStats;
import org.apache.storm.generated.ExecutorStats;
import org.apache.storm.generated.ExecutorSummary;
import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.generated.Nimbus.Client;
import org.apache.storm.generated.SpoutStats;
import org.apache.storm.generated.TopologyInfo;
import org.apache.storm.generated.TopologySummary;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.protocol.TBinaryProtocol;
import org.apache.storm.thrift.transport.TFramedTransport;
import org.apache.storm.thrift.transport.TSocket;
import org.apache.storm.thrift.transport.TTransportException;

/*
 * Library to extract Storm Web UI Parameter Values
 */
public class ClusterInformationExtractor {
	public static String getStats(String[] args) {
		String toReturn = "";
		String uiHost = "";
		Integer uiPort = 0;
		String toplogyName ="";

		if (args.length < 2) {
			System.err.println("Missing UI host and port");
			return toReturn;
		} else {
			uiHost = args[0];
			uiPort = Integer.parseInt(args[1]);
			toplogyName = args[2];
		}
		TSocket socket = new TSocket(uiHost, uiPort);
		TFramedTransport transport = new TFramedTransport(socket);
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		Client client = new Client(protocol);
		
		try {
			transport.open();
			ClusterSummary summary = client.getClusterInfo();

			// Cluster Details
			/*
			 * System.out.println("**** Storm UI Home Page ****");
			 * System.out.println(" ****Cluster Summary**** "); int nimbusUpTime
			 * = summary.get_nimbus_uptime_secs();// getNimbus_uptime_secs();
			 * System.out.println("Nimbus Up Time: " + nimbusUpTime);
			 * System.out.println("Number of Supervisors: " +
			 * summary.get_supervisors_size());// getSupervisorsSize());
			 * System.out.println("Number of Topologies: " +
			 * summary.get_topologies_size());// gettopologiesSize());
			 */
			// Topology stats
			//		System.out.println(" ****Topology summary**** ");
			Map<String, String> topologyConfigurationParamValues = new HashMap<String, String>();
			List<TopologySummary> topologies = summary.get_topologies(); // getTopologies();
			Iterator<TopologySummary> topologiesIterator = summary.get_topologies_iterator(); // getTopologiesIterator();
			//			while (topologiesIterator.hasNext()) {
			//				TopologySummary topology = topologiesIterator.next();
			//				System.out.print("Topology ID: " + topology.get_id());// getId());
			//				System.out.print("Topology Name: " + topology.get_name());
			//				System.out.print("Number of Executors: "
			//						+ topology.get_num_executors());
			//				System.out.println("Number of Tasks: "
			//						+ topology.get_num_tasks());
			//				System.out.println("Number of Workers: "
			//						+ topology.get_num_workers());
			//				System.out.println("Status : " + topology.get_status());
			//				System.out.println("UpTime in Seconds: "
			//						+ topology.get_uptime_secs());
			//			}

			// Supervisor stats
			/*
			 * System.out.println("**** Supervisor summary ****");
			 * List<SupervisorSummary> supervisors = summary.get_supervisors();
			 * Iterator<SupervisorSummary> supervisorsIterator = summary
			 * .get_supervisors_iterator(); while
			 * (supervisorsIterator.hasNext()) { SupervisorSummary supervisor =
			 * supervisorsIterator.next(); System.out.println("Supervisor ID: "
			 * + supervisor.get_supervisor_id()); System.out.println("Host: " +
			 * supervisor.get_host()); System.out.println(
			 * "Number of used workers: " + supervisor.get_num_used_workers());
			 * System.out.println("Number of workers: " +
			 * supervisor.get_num_workers()); System.out.println(
			 * "Supervisor uptime: " + supervisor.get_uptime_secs()); }
			 */
			// Nimbus config parameter-values
			/*
			 * System.out.println("****Nimbus Configuration****"); Map<String,
			 * String> nimbusConfigurationParamValues = new HashMap<String,
			 * String>(); String nimbusConfigString = client.getNimbusConf();
			 * nimbusConfigString = nimbusConfigString.substring(1,
			 * nimbusConfigString.length() - 1); String[] nimbusConfParameters =
			 * nimbusConfigString.split(",\""); for (String nimbusConfParamValue
			 * : nimbusConfParameters) { String[] paramValue =
			 * nimbusConfParamValue.split(":"); String parameter =
			 * paramValue[0].substring(0, paramValue[0].length() - 1); String
			 * parameterValue = paramValue[1]; if
			 * (paramValue[1].startsWith("\"")) { parameterValue =
			 * paramValue[1].substring(1, paramValue[1].length() - 1); }
			 * nimbusConfigurationParamValues.put(parameter, parameterValue); }
			 * 
			 * Set<String> nimbusConfigurationParameters =
			 * nimbusConfigurationParamValues .keySet(); Iterator<String>
			 * parameters = nimbusConfigurationParameters .iterator(); while
			 * (parameters.hasNext()) { String key = parameters.next();
			 * System.out.println("Parameter : " + key + " Value : " +
			 * nimbusConfigurationParamValues.get(key)); }
			 * 
			 * System.out.println(" **** End of Storm UI Home Page Details**** "
			 * ) ;
			 */
			// Topology stats
			//System.out.println(" **** Topology Home Page Details **** ");
			topologiesIterator = summary.get_topologies_iterator();
			while (topologiesIterator.hasNext()) {
				TopologySummary topology = topologiesIterator.next();
				//	System.out.println("**** Topology summary ****");
				System.out.print("Topology Name, " + topology.get_name() + ",");
				System.out.print("Number of Workers," + topology.get_num_workers() + ",");
				System.out.print("Toplopgy UpTime in Seconds: " + topology.get_uptime_secs() + ",");

				toReturn += ("Topology Id, " + topology.get_id() + ",");
			//	toReturn += ("Topology Name, " + topology.get_name() + ",");
				//toReturn += ("Number of Executors, " + topology.get_num_executors() + ",");
				//toReturn += ("Number of Tasks, " + topology.get_num_tasks() + ",");
				toReturn += ("Number of Workers," + topology.get_num_workers() + ",");
				//toReturn += ("Toplopgy Status, " + topology.get_status() + ",");
				//toReturn += ("Toplopgy UpTime in Seconds: " + topology.get_uptime_secs() + ",");

				// Spouts (All time)
				//	System.out.println("**** Spouts (All time) ****");
				TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
				Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
				HashMap<String, SpoutInfoAggrecate> spoutStateAggreactes600 = new HashMap<String, SpoutInfoAggrecate>();
				HashMap<String, SpoutInfoAggrecate> spoutStateAggreactesTotal = new HashMap<String, SpoutInfoAggrecate>();
				while (executorStatusItr.hasNext()) {
					// get the executor
					ExecutorSummary executor_summary = executorStatusItr.next();
					ExecutorStats execStats = executor_summary.get_stats();
					if (execStats != null) {
						ExecutorSpecificStats execSpecStats = execStats.get_specific();
						String componentId = executor_summary.get_component_id();
						// if the executor is a spout
						if (execSpecStats.is_set_spout()) {
							//spoutSpecificStats(topology_info, topology, executor_summary, componentId);
							SpoutStats spoutStats = execSpecStats.get_spout();
							//System.out.println("Spout Id: " + componentId);
							if (!spoutStateAggreactes600.containsKey(componentId))
								spoutStateAggreactes600.put(componentId, new SpoutInfoAggrecate(componentId));

							if (!spoutStateAggreactesTotal.containsKey(componentId))
								spoutStateAggreactesTotal.put(componentId, new SpoutInfoAggrecate(componentId));
							SpoutInfoAggrecate overAllSpoutStats600 = spoutStateAggreactes600.get(componentId);
							SpoutInfoAggrecate overAllSpoutStatsTotal = spoutStateAggreactesTotal.get(componentId);

							Long transfered600 = (getStatValueFromMap(execStats.get_transferred(), "600") == null) ? 0 : getStatValueFromMap(execStats.get_transferred(), "600") / 600;
							Long emitted600 = (getStatValueFromMap(execStats.get_emitted(), "600") == null) ? 0 : getStatValueFromMap(execStats.get_emitted(), "600") / 600;
							Long acked600 = (getStatValueFromMap(spoutStats.get_acked(), "600") == null) ? 0 : getStatValueFromMap(spoutStats.get_acked(), "600") / 600;
							Long failed600 = (getStatValueFromMap(spoutStats.get_failed(), "600") == null) ? 0 : getStatValueFromMap(spoutStats.get_failed(), "600") / 600;
							Double avgCompleteLatency600 = (getStatValueFromMapDouble(spoutStats.get_complete_ms_avg(), "600") == null) ? 0 : getStatValueFromMapDouble(spoutStats.get_complete_ms_avg(), "600") ;
							overAllSpoutStats600.addSpoutStats(transfered600, emitted600, acked600, failed600, avgCompleteLatency600, executor_summary.get_uptime_secs());


							Long transfered = (getStatValueFromMap(execStats.get_transferred(), ":all-time") == null) ? 0 : getStatValueFromMap(execStats.get_transferred(), ":all-time");
							Long emitted = (getStatValueFromMap(execStats.get_emitted(), ":all-time") == null) ? 0 : getStatValueFromMap(execStats.get_emitted(), ":all-time") ;
							Long acked = (getStatValueFromMap(spoutStats.get_acked(), ":all-time") == null) ? 0 : getStatValueFromMap(spoutStats.get_acked(), ":all-time") ;
							Long failed = (getStatValueFromMap(spoutStats.get_failed(), ":all-time") == null) ? 0 : getStatValueFromMap(spoutStats.get_failed(), ":all-time") ;
							Double avgCompleteLatency = (getStatValueFromMapDouble(spoutStats.get_complete_ms_avg(), ":all-time") == null) ? 0 : getStatValueFromMapDouble(spoutStats.get_complete_ms_avg(), ":all-time");
							overAllSpoutStatsTotal.addSpoutStats(transfered, emitted, acked, failed, avgCompleteLatency, executor_summary.get_uptime_secs());

							
							// System.out.print("Transferred: "
							// + getStatValueFromMap(
							// execStats.get_transferred(),
							// ":all-time") + "\t");
							// System.out.print("Emitted: "
							// + getStatValueFromMap(
							// execStats.get_emitted(),
							// ":all-time") + "\t");
							// System.out
							// .print("Acked: "
							// + getStatValueFromMap(
							// spoutStats.get_acked(),
							// ":all-time") + "\t");
							// System.out.println("Failed: "
							// + getStatValueFromMap(
							// spoutStats.get_failed(),
							// ":all-time") + "\t");
						}
					}
				}
				Iterator<Entry<String, SpoutInfoAggrecate>> it = spoutStateAggreactes600.entrySet().iterator();
				toReturn+="600,";
				while (it.hasNext()) {
					String toPrint = it.next().getValue().toString();
					toReturn += toPrint;
					System.out.print(toPrint);
				}
				Iterator<Entry<String, SpoutInfoAggrecate>> it2 = spoutStateAggreactesTotal.entrySet().iterator();
				toReturn+="totall,";
				while (it2.hasNext()) {
					String toPrint = it2.next().getValue().toString();
					toReturn += toPrint;
					System.out.print(toPrint);
				}
				// Bolts (All time)
				//	System.out.println("****Bolts (All time)****");
				HashMap<String, BoltInfoAggrecate> boltStateAggreactes600 = new HashMap<String, BoltInfoAggrecate>();
				HashMap<String, BoltInfoAggrecate> boltStateAggreactesTotal = new HashMap<String, BoltInfoAggrecate>();
				executorStatusItr = topology_info.get_executors_iterator();
				while (executorStatusItr.hasNext()) {
					// get the executor
					ExecutorSummary executor_summary = executorStatusItr.next();
					ExecutorStats execStats = executor_summary.get_stats();
					if(execStats==null) continue;
					ExecutorSpecificStats execSpecStats = execStats.get_specific();
					String componentId = executor_summary.get_component_id();
					if (execSpecStats.is_set_bolt()) {
						BoltStats boltStats = execSpecStats.get_bolt();

						if (!boltStateAggreactes600.containsKey(componentId))
							boltStateAggreactes600.put(componentId, new BoltInfoAggrecate(componentId,toplogyName));
						if (!boltStateAggreactesTotal.containsKey(componentId))
							boltStateAggreactesTotal.put(componentId, new BoltInfoAggrecate(componentId,toplogyName));

						BoltInfoAggrecate overAllboltStats600 = boltStateAggreactes600.get(componentId);
						overAllboltStats600.addBoltStats(
								getAllStatValueFromMap(execStats.get_transferred(), "600"),
								getAllStatValueFromMap(execStats.get_emitted(), "600") ,
								getAllBoltStatLongValueFromMap(boltStats.get_acked(), "600")  ,
								getAllBoltStatLongValueFromMap(boltStats.get_failed(), "600") ,
								getAllBoltStatLongValueFromMap(boltStats.get_executed(), "600") ,
								getAllBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "600"),
										executor_summary.get_uptime_secs());
						
						BoltInfoAggrecate overAllboltStatsTotal = boltStateAggreactesTotal.get(componentId);
						overAllboltStatsTotal.addBoltStats(
								getAllStatValueFromMap(execStats.get_transferred(), ":all-time"),
								getAllStatValueFromMap(execStats.get_emitted(), ":all-time") ,
								getAllBoltStatLongValueFromMap(boltStats.get_acked(), ":all-time")  ,
								getAllBoltStatLongValueFromMap(boltStats.get_failed(), ":all-time") ,
								getAllBoltStatLongValueFromMap(boltStats.get_executed(), ":all-time") ,
								getAllBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"),
							//	getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time") == null ? 0.0 : getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"), 
								executor_summary.get_uptime_secs());

						//						System.out.println("Bolt Id: " + componentId);
						//						System.out.print("Transferred: "
						//								+ getStatValueFromMap(
						//										execStats.get_transferred(),
						//										":all-time") + "\t");
						//						System.out.print("Emitted: "
						//								+ getStatValueFromMap(execStats.get_emitted(),
						//										":all-time") + "\t");
						//						System.out.print("Acked: "
						//								+ getBoltStatLongValueFromMap(
						//										boltStats.get_acked(), ":all-time")
						//								+ "\t");
						//						System.out.print("Failed: "
						//								+ getBoltStatLongValueFromMap(
						//										boltStats.get_failed(), ":all-time")
						//								+ "\t");
						//						System.out.print("Executed : "
						//								+ getBoltStatLongValueFromMap(
						//										boltStats.get_executed(), ":all-time")
						//								+ "\t");
						//						System.out.print("Execute Latency (ms): "
						//								+ getBoltStatDoubleValueFromMap(
						//										boltStats.get_execute_ms_avg(),
						//										":all-time") + "\t");
						//						System.out.println("Process Latency (ms): "
						//								+ getBoltStatDoubleValueFromMap(
						//										boltStats.get_process_ms_avg(),
						//										":all-time") + "\t");
					}
				}
				Iterator<Entry<String, BoltInfoAggrecate>> it3 = boltStateAggreactes600.entrySet().iterator();
				toReturn += "600,";
				while (it3.hasNext()) {
					String toPrint = ((BoltInfoAggrecate)it3.next().getValue()).toString(600);
					toReturn += toPrint;
					System.out.print(toPrint);
				}
				Iterator<Entry<String, BoltInfoAggrecate>> it4 = boltStateAggreactesTotal.entrySet().iterator();
				toReturn += "total,";
				while (it4.hasNext()) {
					String toPrint = it4.next().getValue().toString();
					toReturn += toPrint;
					System.out.print(toPrint);
				}
				// Topology Configuration
				/*
				 * System.out.println("**** Topology Configuration ****");
				 * String topologyConfigString = client.getTopologyConf(topology
				 * .get_id()); topologyConfigString =
				 * topologyConfigString.substring(1,
				 * topologyConfigString.length() - 1); String[]
				 * topologyConfParameters = topologyConfigString .split(",\"");
				 * 
				 * for (String topologyConfParamValue : topologyConfParameters)
				 * { String[] paramValue = topologyConfParamValue.split(":");
				 * String parameter = paramValue[0].substring(0,
				 * paramValue[0].length() - 1); String parameterValue =
				 * paramValue[1]; if (paramValue[1].startsWith("\"")) {
				 * parameterValue = paramValue[1].substring(1,
				 * paramValue[1].length() - 1); }
				 * topologyConfigurationParamValues.put(parameter,
				 * parameterValue); } Set<String>
				 * topologyConfigurationParameters =
				 * topologyConfigurationParamValues .keySet(); Iterator<String>
				 * topologyParameters = topologyConfigurationParameters
				 * .iterator(); while (topologyParameters.hasNext()) { String
				 * key = topologyParameters.next(); System.out.println(
				 * "Parameter: " + key + " Value : " +
				 * topologyConfigurationParamValues.get(key)); }
				 */
			}
			System.out.println(" ****  End of Topology Home Page Details ****");

			// Spout Home Page Details

			System.out.println(" **** Spout Home Page Details ****");
			topologiesIterator = summary.get_topologies_iterator();
		//	while (topologiesIterator.hasNext()) {
				//				TopologySummary topology = topologiesIterator.next();
				//				TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
				//				Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
				//				while (executorStatusItr.hasNext()) {
				//					// get the executor 
				//					ExecutorSummary executor_summary = executorStatusItr.next();
				//					ExecutorStats execStats = executor_summary.get_stats();
				//					ExecutorSpecificStats execSpecStats = execStats.get_specific();
				//					String componentId = executor_summary.get_component_id();
				//					if (execSpecStats.is_set_spout()) {
				//						spoutSpecificStats(topology_info, topology, executor_summary, componentId);
				//					}
				//				}
		//	}
			System.out.println(" **** End of Spout Home Page Details**** ");

			// Bolt Home Page Details
			/*
			 * System.out.println(" **** Bolt Home Page Details ****");
			 * topologiesIterator = summary.get_topologies_iterator(); while
			 * (topologiesIterator.hasNext()) { TopologySummary topology =
			 * topologiesIterator.next(); TopologyInfo topology_info =
			 * client.getTopologyInfo(topology .get_id());
			 * Iterator<ExecutorSummary> executorStatusItr = topology_info
			 * .get_executors_iterator(); while (executorStatusItr.hasNext()) {
			 * // get the executor ExecutorSummary executor_summary =
			 * executorStatusItr.next(); ExecutorStats execStats =
			 * executor_summary.get_stats(); ExecutorSpecificStats execSpecStats
			 * = execStats .get_specific(); String componentId =
			 * executor_summary.get_component_id(); // if the executor is a bolt
			 * if (execSpecStats.is_set_bolt()) {
			 * boltSpecificStats(topology_info, topology, executor_summary,
			 * componentId); } } } System.out.println(
			 * " **** End of Bolt Home Page Details **** ");
			 */
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public static void main(String[] args) {
		getStats(args);
	}

	/*
	 * Calculate spout specific stats
	 */
	private static void spoutSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary, String componentId) {
		ExecutorStats execStats = executorSummary.get_stats();
		ExecutorSpecificStats execSpecStats = execStats.get_specific();
		SpoutStats spoutStats = execSpecStats.get_spout();
		//System.out.println("**** Component summary ****");
		System.out.print("Spout Id , " + componentId);
		//		System.out.print("Topology Name  : " + topology.get_name());

		//		System.out.println("**** Spout stats ****");
		//		System.out.println("**** Window Size ****  " + "600");
		Long transfered600 = (getStatValueFromMap(execStats.get_transferred(), "600") == null) ? 0 : getStatValueFromMap(execStats.get_transferred(), "600") / 600;
		Long emitted600 = (getStatValueFromMap(execStats.get_emitted(), "600") == null) ? 0 : getStatValueFromMap(execStats.get_emitted(), "600") / 600;

		System.out.println(", 600 Transferred, " + getStatValueFromMap(execStats.get_transferred(), "600") == null ? 0 : getStatValueFromMap(execStats.get_transferred(), "600") / 600);
		System.out.println(",600 Emitted, " + getStatValueFromMap(execStats.get_emitted(), "600") == null ? 0 : getStatValueFromMap(execStats.get_emitted(), "600") / 600);
		System.out.println(",600 Acked, " + getStatValueFromMap(spoutStats.get_acked(), "600") == null ? 0 : getStatValueFromMap(spoutStats.get_acked(), "600") / 600);
		System.out.println(",600 Failed, " + getStatValueFromMap(spoutStats.get_failed(), "600") == null ? 0 : getStatValueFromMap(spoutStats.get_failed(), "600") / 600);

		System.out.println("**** Window Size ****  " + "10800");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), "10800"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), "10800"));
		System.out.println("Acked : " + getStatValueFromMap(spoutStats.get_acked(), "10800"));
		System.out.println("Failed : " + getStatValueFromMap(spoutStats.get_failed(), "10800"));

		System.out.println("**** Window Size ****  " + "86400");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), "86400"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), "86400"));
		System.out.println("Acked : " + getStatValueFromMap(spoutStats.get_acked(), "86400"));
		System.out.println("Failed : " + getStatValueFromMap(spoutStats.get_failed(), "86400"));
		System.out.println("**** Window Size ****  " + "all-time");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getStatValueFromMap(spoutStats.get_failed(), ":all-time"));

		System.out.println("**** Output stats (All time) ****");
		System.out.println("Stream : " + "default");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getStatValueFromMap(spoutStats.get_failed(), ":all-time"));

		System.out.println("**** Executors (All time) ****");
		System.out.println("Host : " + executorSummary.get_host());
		System.out.println("Port : " + executorSummary.get_port());
		System.out.println("Up-Time : " + executorSummary.get_uptime_secs());
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getStatValueFromMap(spoutStats.get_failed(), ":all-time"));

		System.out.println("**** Errors ****");
		Map<String, List<ErrorInfo>> errors = topologyInfo.get_errors();
		List<ErrorInfo> spoutErrors = errors.get(componentId);
		for (ErrorInfo errorInfo : spoutErrors) {
			System.out.println("Spout Error : " + errorInfo.get_error());
		}
	}

	/*
	 * Calculate bolt specific stats
	 */
	private static void boltSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary, String componentId) {
		ExecutorStats execStats = executorSummary.get_stats();
		ExecutorSpecificStats execSpecStats = execStats.get_specific();
		BoltStats boltStats = execSpecStats.get_bolt();
		System.out.println(":::::::::: Component summary ::::::::::");
		System.out.println("Id : " + componentId);
		System.out.println("Topology Name  : " + topology.get_name());
		System.out.println("Executors : " + "1");
		System.out.println("Tasks : " + "1");
		System.out.println(":::::::::: Bolt stats ::::::::::");
		System.out.println("::::::::::: Window Size :::::::::::  " + "600");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), "600"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), "600"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), "600"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), "600"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), "600"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "600"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "600"));
		System.out.println("::::::::::: Window Size :::::::::::  " + "10800");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), "10800"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), "10800"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), "10800"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), "10800"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), "10800"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "10800"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "10800"));
		System.out.println("::::::::::: Window Size :::::::::::  " + "86400");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), "86400"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), "86400"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), "86400"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), "86400"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), "86400"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "86400"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "86400"));
		System.out.println("::::::::::: Window Size :::::::::::  " + "all-time");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));

		System.out.println(":::::::::: Output stats (All time) ::::::::::");
		System.out.println("Stream : " + "default");
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));

		System.out.println(":::::::::: Executors (All time) ::::::::::");
		System.out.println("Host : " + executorSummary.get_host());
		System.out.println("Port : " + executorSummary.get_port());
		System.out.println("Up-Time : " + executorSummary.get_uptime_secs());
		System.out.println("Transferred : " + getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		System.out.println("Emitted : " + getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));

		System.out.println(":::::::::: Errors ::::::::::");
		Map<String, List<ErrorInfo>> errors = topologyInfo.get_errors();
		System.out.println(errors.keySet());
		List<ErrorInfo> boltErrors = errors.get(componentId);
		for (ErrorInfo errorInfo : boltErrors) {
			System.out.println("Bolt Error : " + errorInfo.get_error());
		}
	}

	/*
	 * Utility method to parse a Map<>
	 */
	public static Long getStatValueFromMap(Map<String, Map<String, Long>> map, String statName) {
		Long statValue = null;
		Map<String, Long> intermediateMap = map.get(statName);
		if(intermediateMap!=null)
		statValue = intermediateMap.get("default");
		return statValue;
	}

	/*
	 * Utility method to parse a Map<>
	 */
	public static Map<String, Long> getAllStatValueFromMap(Map<String, Map<String, Long>> map, String statName) {
		Map<String, Long> intermediateMap = map.get(statName);
		return intermediateMap;
	}
	public static Double getStatValueFromMapDouble(Map<String, Map<String, Double>> map, String statName) {
		Double statValue = null;
		Map<String, Double> intermediateMap = map.get(statName);
		if(intermediateMap!=null)
		statValue = intermediateMap.get("default");
		return statValue;
	}

	/*
	 * Utility method to parse a Map<> as a special case for Bolts
	 */
	public static Double getBoltStatDoubleValueFromMap(Map<String, Map<GlobalStreamId, Double>> map, String statName) {
		Double statValue = 0.0;
		Map<GlobalStreamId, Double> intermediateMap = map.get(statName);
		Set<GlobalStreamId> key = intermediateMap.keySet();
		if (key.size() > 0) {
			Iterator<GlobalStreamId> itr = key.iterator();
			statValue = intermediateMap.get(itr.next());
		}
		return statValue;
	}

	/*
	 * Utility method for Bolts
	 */
	public static Long getBoltStatLongValueFromMap(Map<String, Map<GlobalStreamId, Long>> map, String statName) {
		Long statValue = null;
		Map<GlobalStreamId, Long> intermediateMap = map.get(statName);
		Set<GlobalStreamId> key = intermediateMap.keySet();
		if (key.size() > 0) {
			Iterator<GlobalStreamId> itr = key.iterator();
			statValue = intermediateMap.get(itr.next());
		}
		return statValue;
	}
	public static Map<GlobalStreamId, Long> getAllBoltStatLongValueFromMap(Map<String, Map<GlobalStreamId, Long>> map, String statName) {
		Map<GlobalStreamId, Long> intermediateMap = map.get(statName);		
		return intermediateMap;
	}
	public static Map<GlobalStreamId, Double> getAllBoltStatDoubleValueFromMap(Map<String, Map<GlobalStreamId, Double>> map, String statName) {
		Map<GlobalStreamId, Double> intermediateMap = map.get(statName);		
		return intermediateMap;
	}
}
