package search;

import controllers.Utility;
import lombok.Getter;
import lombok.Setter;
import models.Price;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyIndex implements Serializable, Comparable<PropertyIndex> {
	private static final long serialVersionUID = -42143673419833925L;
	
	@Getter @Setter public Long id;

	@Getter @Setter public String mode;
	@Getter @Setter public String caption;
	@Getter @Setter public String search;
	@Getter @Setter public String allSearch;

	@Getter @Setter public Long poster;

	@Getter @Setter public String pid;
	@Getter @Setter public String status;
	@Getter @Setter public boolean disposed;
	@Getter @Setter public boolean managed;
	@Getter @Setter public String brief;

	@Getter @Setter public Double amount;
	@Getter @Setter public boolean discounted;

	@Getter @Setter public String use;

	@Getter @Setter public String type;

	@Getter @Setter public String state;
	@Getter @Setter public Long stateId;

	@Getter @Setter public String axis;
	@Getter @Setter public Long axisId;

	@Getter @Setter public String area;
	@Getter @Setter public Long areaId;

	@Getter @Setter public Long postedOn;

	@Getter @Setter public Integer beds;
	@Getter @Setter public Integer baths;
	@Getter @Setter public Integer toilets;
	@Getter @Setter public Integer rooms;
	@Getter @Setter public Double sqm;
	@Getter @Setter public Double sqf;
	@Getter @Setter public String nature;

	@Getter @Setter public String subArea;
	@Getter @Setter public String street;
	@Getter @Setter public String title;
	@Getter @Setter public String summary;
	@Getter @Setter public Price price;

	@Getter @Setter public String image;
	@Getter @Setter public String imageAlt = "";
	@Getter @Setter public Integer imageCount;

	@Getter @Setter public List<String> faci = new ArrayList<>();

	@Getter @Setter public boolean internal;
	@Getter @Setter public boolean crawled;

	public String address(){
		return (Utility.isNotEmpty(subArea) ? subArea + " ": "")
				+ (Utility.isNotEmpty(area) ? Utility.capitalize(area) + " ": "")
				+ (Utility.isNotEmpty(axis) ? Utility.capitalize(axis) + " ": "")
				+ Utility.capitalize(state);
	}
	
	public static String clean(String text) {
		if(Utility.isEmpty(text)) return "";
		return text.replace(" ", "-").replace("_", "-").replaceAll("/", "-").replaceAll("-+","-").toLowerCase();
	}

	@Override
	public int compareTo(@NotNull PropertyIndex o) {
		return pid.compareTo(o.pid);
	}

}
