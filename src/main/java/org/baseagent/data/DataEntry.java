package org.baseagent.data;

public class DataEntry {
    private long time;
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
