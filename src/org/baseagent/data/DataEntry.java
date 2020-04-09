package org.baseagent.data;

import java.util.Map;
import java.util.UUID;

public class DataEntry {
	private long time;
//	private UUID componentUuid;
	private Object value;
	
	public DataEntry(long time, Object value) {
		this.time = time;
		this.value = value;
	}
	
	public long getTime() {
		return this.time;
	}

	public Object getValue() {
		return this.value;
	}
}
