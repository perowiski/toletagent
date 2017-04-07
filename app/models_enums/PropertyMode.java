package models_enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum PropertyMode {
	rent("For Rent"), sale("For Sale"), shortlet("Shortlet");

	String rep;

	PropertyMode(String rep) {
		this.rep = rep;
	}

	public String getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public static Map<String, String> select() {
		Map<String, String> map = new LinkedHashMap<>();
		for(PropertyMode value: PropertyMode.values()) {
			map.put(value.toString(), value.rep);
		}
		return map;
	}
}