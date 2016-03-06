package com.tinyrtsp.rtsp.message;

import java.util.HashMap;
import java.util.Map;

public abstract class RtspMessage {
	private int sequenceNumber;
	private String protocol;
	private HashMap<String, String> options;
	private String payload;
	
	public RtspMessage(String protocol, int sequenceNumber, HashMap<String, String> options, String payload) {
		this.protocol = protocol;
		
		if (options == null) {
			this.options = new HashMap<String, String>();
		}
		else {
			this.options = options;
		}
		
		if (payload == null) {
			this.payload = "";
		}
		else {
			this.payload = payload;
		}
		
		setSequenceNumber(sequenceNumber);
	}
	
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		setOption("CSeq", sequenceNumber+"");
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getOption(String option) {
		return options.get(option);
	}
	
	public void setOption(String option, String value) {
		options.put(option, value);
	}
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String getPayload() {
		return payload;
	}
	
	public HashMap<String, String> getOptionMap() {
		return options;
	}
	
	protected void writeOptions(StringBuilder builder) {
		for (Map.Entry<String, String> entry : getOptionMap().entrySet()) {
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(entry.getValue());
			builder.append("\r\n");
		}
	}
	
	public abstract String toWireStringNoPayload();
	public abstract String toWireString();
	
	private byte[] stringToWireBytes(String string) {
		byte[] wireBytes = new byte[string.length()];
		
		for (int i = 0; i < wireBytes.length; i++) {
			wireBytes[i] = (byte)string.charAt(i);
		}
		
		return wireBytes;
	}
	
	public byte[] toWire() {
		return stringToWireBytes(toWireString());
	}
	
	public byte[] toWireNoPayload() {
		return stringToWireBytes(toWireStringNoPayload());
	}
	
	public byte[] toWirePayloadOnly() {
		String payload = getPayload();
		if (payload.isEmpty()) {
			return null;
		}
		return stringToWireBytes(payload);
	}
}
