package org.baseagent.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.baseagent.comms.MessageListener;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.util.Pair;

public class DataCollector {
	private long index;
	private Map<Long, List<Long>> timestampToDataIndexes;
	private Map<Long, Long> indexToTimestamp;
	private Map<SimulationComponent, List<Long>> componentToDataIndexes;
	private Map<UUID, List<Long>> uuidToDataIndexes;
	private Map<UuidAtTime, Long> uatToDataIndexes;
	private Map<Long, Map<String, Object>> indexToData;
	
	public DataCollector() {
		timestampToDataIndexes = new HashMap<>();
		componentToDataIndexes = new HashMap<>();
		uuidToDataIndexes = new HashMap<>();
		indexToData = new HashMap<>();
	}
	
	public void add(SimulationComponent component, String key, Object value) {
		Map<String, Object> existingData = getDataForSimulationComponentAtThisTime(component);
		existingData.put(key, value);
	}
	
	public void add(SimulationComponent component, Map<String, Object> data) {
		Map<String, Object> existingData = getDataForSimulationComponentAtThisTime(component);
		existingData.putAll(data);
	}

	private Map<String, Object> getDataForSimulationComponentAtThisTime(SimulationComponent component) {
		index = getIndexForSimulationComponentAtThisTime(component);
		if (!indexToData.containsKey(index)) {
			indexToData.put(index, new HashMap<String, Object>());
		}
		return indexToData.get(index);
	}
	
	private Long getIndexForSimulationComponentAtThisTime(SimulationComponent component) {
		UuidAtTime uat = new UuidAtTime(component.getUUID(), component.getSimulation().getStepTime());
		if (!uatToDataIndexes.containsKey(uat)) {
			long index = createIndex();
			uatToDataIndexes.put(uat, index);
			addIndexToMaps(component, index);
		}
		return uatToDataIndexes.get(uat);
	}
	
	private long createIndex() {
		return index++;
	}
	
	private void addIndexToMaps(SimulationComponent component, long index) {
		// Add the index to the Time map
		long time = component.getSimulation().getStepTime();
		getIndexesForTime(time).add(index);
		
		// Add the index to the UUID map
		UUID uuid = component.getUUID();
		getIndexesForUUID(uuid).add(index);
		
		// Add the index to the SimulationComponentIdentifier map
//		SimulationComponentIdentifier id = component.getIdentifier();
//		getIndexesForIdentifier(id).add(index);
		
		indexToTimestamp.put(index, time);
	}
	
	private List<Long> getIndexesForTime(long time) {
		if (!timestampToDataIndexes.containsKey(time)) {
			timestampToDataIndexes.put(time, new ArrayList<Long>());
		}
		return timestampToDataIndexes.get(time);
	}

	private List<Long> getIndexesForUUID(UUID uuid) {
		if (!uuidToDataIndexes.containsKey(uuid)) {
			uuidToDataIndexes.put(uuid, new ArrayList<Long>());
		}
		return uuidToDataIndexes.get(uuid);
	}

	//
	//  Retrievers
	//
	
//	get(ENVIRONMENT, "Population");
	public List<DataEntry> get(SimulationComponent id, String key) {
		List<DataEntry> retVal = new ArrayList<>();
		List<Long> indexes = componentToDataIndexes.get(id);
		for (long index : indexes) {
			Map<String, Object> data = indexToData.get(index);
			if (data.containsKey(key)) {
				retVal.add(new DataEntry(indexToTimestamp.get(index), data.get(key)));
			}
		}
		retVal.sort((DataEntry de1, DataEntry de2) -> de2.getTime() > de1.getTime() ? 1 : de2.getTime() < de1.getTime() ? -1 : 0);
		return retVal;
	}
	

	//
	// Helper Classes
	//
	
	class UuidAtTime {
		private UUID uuid;
		private long time;
		
		public UuidAtTime(UUID uuid, long time) {
			this.uuid = uuid;
			this.time = time;
		}
		
		public int hashCode() {
			return uuid.hashCode() * Long.hashCode(time);
		}
		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof UuidAtTime)) return false;
			UuidAtTime uat = (UuidAtTime)o;
			return (uat.uuid.equals(uuid) && uat.time == time);
		}
	}
}
