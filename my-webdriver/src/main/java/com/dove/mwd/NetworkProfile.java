package com.dove.mwd;

public enum NetworkProfile {
	Offline("offline", 0, 0, 0),
	Edge("edge", 240 * 1024 / 8 * .9, 200 * 1024 / 8 * .9, 840),
	Regular3G("regular3g", 750 * 1024 / 8 * .9, 250 * 1024 / 8 * .9, 100),
	RegularLTE("regularlte", 4000 * 1024 / 8 * .9, 3000 * 1024 / 8 * .9, 20),
	WiFi("wifi", 30000 * 1024 / 8 * .9, 15000 * 1024 / 8 * .9, 2);

	private String value;
	private Number download;
	private Number upload;
	private Number latency;
	
	/**
	 * number x 1024(to kilo bits) / 8(to bytes) x 0.9(10% bandwidth loss)
	 */
	NetworkProfile(String value, Number download, Number upload, Number latency) {
		this.value = value;
		this.download = download;
		this.upload = upload;
		this.latency = latency;
	}
	
	public boolean isOffine() {
		return value.equalsIgnoreCase("offline");
	}
	
	public String getValue() {
		return value;
	}
	
	public Number getDownload() {
		return download;
	}
	
	public Number getUpload() {
		return upload;
	}
	
	public Number getLatency() {
		return latency;
	}
}
