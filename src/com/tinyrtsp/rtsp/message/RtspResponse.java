package com.tinyrtsp.rtsp.message;

import java.util.HashMap;

public class RtspResponse extends RtspMessage {
	private int statusCode;
	private String statusString;
	
	public RtspResponse(String protocol, int statusCode, String statusString, int sequenceNumber, HashMap<String, String> options, String payload) {
		super(protocol, sequenceNumber, options, payload);
		
		this.statusCode = statusCode;
		this.statusString = statusString;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getStatusString() {
		return statusString;
	}
	
	public void setStatus(int code, String message) {
		this.statusCode = code;
		this.statusString = message;
	}
	
	@Override
	public String toWireStringNoPayload() {
		StringBuilder toWire = new StringBuilder();
		
		// Header
		toWire.append(getProtocol());
		toWire.append(' ');
		toWire.append(getStatusCode());
		toWire.append(' ');
		toWire.append(getStatusString());
		toWire.append("\r\n");
		
		// Options
		writeOptions(toWire);
		
		// End of packet
		toWire.append("\r\n");
		
		return toWire.toString();
	}

	@Override
	public String toWireString() {		
		return toWireStringNoPayload() + getPayload();
	}
}
