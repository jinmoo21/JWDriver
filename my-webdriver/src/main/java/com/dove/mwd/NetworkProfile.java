package com.dove.mwd;

// Download Speed(Kbps), Upload Speed(Kbps), Latency(ms), Packet Loss(%)
public enum NetworkProfile {
	OFFLINE("offline", 0, 0, 0),
	// 2g-gprs-good: 50, 30, 500, 1
	GOOD_2G_GPRS("good_2g_gprs", 50 * 1000 * .99, 30 * 1000 * .99, 500),
	// 2g-gprs-lossy: 30, 20, 650, 2
	LOSSY_2G_GPRS("lossy_2g_gprs", 30 * 1000 * .98, 20 * 100 * .98, 650),
	// edge-good: 250, 150, 300, 0
	GOOD_EDGE("good_edge", 250 * 1000, 150 * 1000, 300),
	// edge-lossy: 150, 100, 500, 1
	LOSSY_EDGE("lossy_edge", 150 * 1000 * .99, 100 * 1000 * .99, 500),
	// 3g-umts-good: 400, 100, 100, 0
	GOOD_3G_UMTS("good_3g_umts", 400 * 1000, 100 * 1000, 100),
	// 3g-umts-lossy: 200, 50, 200, 1
	LOSSY_3G_UMTS("lossy_3g_umts", 200 * 1000 * .99, 50 * 1000 * .99, 200),
	// 3.5g-hspa-good: 1800, 400, 100, 0
	GOOD_3_5G_HSPA("good_3.5g_hspa", 1800 * 1000, 400 * 1000, 100),
	// 3.5g-hspa-lossy: 900, 200, 190, 1
	LOSSY_3_5G_HSPA("lossy_3.5g_hspa", 900 * 1000 * .99, 200 * 1000 * .99, 190),
	// 3.5g-hspa-plus-good: 7000, 1500, 100, 0
	GOOD_3_5G_HSPA_PLUS("good_3.5g_hspa_plus", 7000 * 1000, 1500 * 1000, 100),
	// 3.5g-hspa-plus-lossy: 2000, 600, 130, 1
	LOSSY_3_5G_HSPA_PLUS("lossy_3.5g_hspa_plus", 2000 * 1000 * .99, 600 * 1000 * .99, 130),
	// 4g-lte-good: 18000, 9000, 100, 0
	GOOD_4G_LTE("good_4g_lte", 18000 * 1000, 9000 * 1000, 100),
	// 4g-lte-high-latency: 18000, 9000, 3000, 0
	HIGH_LATENCY_4G_LTE("high_latency_4g_lte", 18000 * 1000, 9000 * 1000, 3000),
	// 4g-lte-lossy: 18000, 9000, 100, 0
	LOSSY_4G_LTE("lossy_4g_lte", 7000 * 1000 * .99, 3000 * 1000 * .99, 120),
	// 4g-lte-advanced-good: 25000, 18000, 80, 0
	GOOD_4G_ADVANCED_LTE("good_4g_lte_advanced", 25000 * 1000, 18000 * 1000, 80),
	// 4g-lte-advanced-good: 25000, 18000, 80, 0
	LOSSY_4G_ADVANCED_LTE("lossy_4g_lte_advanced", 15000 * 1000 * .99, 10000 * 1000 * .99, 70);
	
	private String value;
	private Number download;
	private Number upload;
	private Number latency;
	
	NetworkProfile(String value, Number download, Number upload, Number latency) {
		this.value = value;
		this.download = download;
		this.upload = upload;
		this.latency = latency;
	}
	
	public boolean isOffine() {
		return value.equalsIgnoreCase("offline");
	}
	
	public boolean isGood() {
		return value.startsWith("good");
	}
	
	public boolean isHighLatency() {
		return value.startsWith("high_latency");
	}
	
	public boolean isLossy() {
		return value.startsWith("lossy");
	}
	
	public boolean is2G() {
		return value.endsWith("2g_gprs");
	}
	
	public boolean isEdge() {
		return value.endsWith("edge");
	}

	public boolean is3G() {
		return value.endsWith("3g_umts");
	}

	public boolean is3_5G() {
		return value.endsWith("3.5g_hspa") || value.endsWith("3.5g_hspa_plus");
	}
	
	public boolean is4G() {
		return value.endsWith("4g_lte") || value.endsWith("4g_lte_advanced");
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
