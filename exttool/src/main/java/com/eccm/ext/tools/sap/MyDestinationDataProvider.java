package com.eccm.ext.tools.sap;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class MyDestinationDataProvider implements DestinationDataProvider {

	private DestinationDataEventListener eL;
	private HashMap<String, Properties> destinations;

	public MyDestinationDataProvider() {
//			System.out.println("Creating MyDestinationDataProvider ... ");
			destinations = new HashMap();
	}

	public Properties getDestinationProperties(String destinationName) {
		if (destinations.containsKey(destinationName)) {
			return destinations.get(destinationName);
		} else {
			throw new RuntimeException("Destination " + destinationName + " is not available");
		}
	}

	public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
		this.eL = eventListener;
	}

	public boolean supportsEvents() {
		return true;
	}

	public void addDestination(String destinationName, Properties properties) {
		synchronized (destinations) {
			destinations.put(destinationName, properties);
		}
	}
}
