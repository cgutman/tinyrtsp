package com.tinyrtsp.rtsp.message;

import java.util.HashMap;

public class RtspRequest extends RtspMessage {
	private String command;
	private String target;
	
	public RtspRequest(String command, String target, String protocol,
			int sequenceNumber, HashMap<String, String> options, String payload) {
		super(protocol, sequenceNumber, options, payload);
		this.command = command;
		this.target = target;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getTarget() {
		return target;
	}
	
	@Override
	public String toWireStringNoPayload() {
		StringBuilder toWire = new StringBuilder();
		
		// Header
		toWire.append(getCommand());
		toWire.append(' ');
		toWire.append(getTarget());
		toWire.append(' ');
		toWire.append(getProtocol());
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
