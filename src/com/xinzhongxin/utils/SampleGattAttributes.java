package com.xinzhongxin.utils;

import java.util.HashMap;

public class SampleGattAttributes {
	private static HashMap<String, String> attributes = new HashMap();
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	static {
		// Sample Services.
		attributes.put("0000180d-0000-1000-8000-00805f9b34fb",
				"Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb",
				"Device Information Service");
		attributes.put("00001800-0000-1000-8000-00805f9b34fb",
				"General Access Service");
		attributes.put("00001801-0000-1000-8000-00805f9b34fb",
				"General Attribute Service");
		attributes.put("00001802-0000-1000-8000-00805f9b34fb",
				"Immediate Alert Service");
		attributes.put("00001803-0000-1000-8000-00805f9b34fb",
				"Link Loss Service");
		attributes.put("00001804-0000-1000-8000-00805f9b34fb",
				"Tx Power Service");
		attributes.put("0000180f-0000-1000-8000-00805f9b34fb",
				"Battery Service");
		// Sample Characteristics.
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb",
				"Manufacturer Name String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
