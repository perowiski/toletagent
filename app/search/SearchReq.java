package search;


import controllers.Utility;
import lombok.*;
import org.apache.commons.lang3.SerializationUtils;
import org.mongodb.morphia.annotations.Indexed;
import services.DBService;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchReq implements Serializable {
	@Getter @Setter public String search;
	@Getter @Setter public String mode;// = "rent";
	@Getter @Setter public String status;
	@Getter @Setter public Boolean disposed;
	@Getter @Setter public Boolean discounted;
	@Getter @Setter public Boolean similar;
	@Getter @Setter public Boolean managed;
	@Getter @Setter public String brief;
	@Getter @Setter public String state;
	@Indexed
	@Getter @Setter public String area;
	@Getter @Setter public String use;
	@Indexed
	@Getter @Setter public String type;
	@Indexed
	@Getter @Setter public Double min_price;
	@Indexed
	@Getter @Setter public Double max_price;
	@Indexed
	@Getter @Setter public Integer beds;
	@Getter @Setter public Integer baths;

	@Getter @Setter public String start_date;
	@Getter @Setter public String end_date;

	@Getter @Setter public String axis;

	@Indexed
	@Getter @Setter public Integer agent;
	@Indexed
	@Getter @Setter public Integer poster;

	@Getter @Setter public Boolean nodup;

	@Getter @Setter public Boolean internal;
	@Getter @Setter public Boolean crawled;


	@Getter @Setter public List<String> faci = new ArrayList<>();

	@Getter @Setter public boolean has = true;

	public SearchReq copy() {
		return SerializationUtils.clone(this);
	}

	public SearchReq() {

	}

	public SearchReq(boolean has) {
		this.has = has;
	}


    public String url(String key, String value) {
        String url  = "";

        if (Utility.isNotEmpty(axis) && !"axis".equals(key)) {
            url += "/" + axis;
        }

        if (Utility.isNotEmpty(axis) && "axis".equals(key)) {
            //url += "-" + ((area.contains(value)) ? area : area + "," + value);
            url += "/" + value;
        }

        if (Utility.isEmpty(axis) && "axis".equals(key)) {
            url += "/" + value;
        }

        if (Utility.isNotEmpty(type) && !"type".equals(key)) {
            url += "/" + type;
        }

        if (Utility.isNotEmpty(type) && "type".equals(key)) {
            //url += "/" + ((type.contains(value)) ? type : type + "," + value);
            url += "/" + value;
        }

        if (Utility.isEmpty(type) && "type".equals(key)) {
            url += "/" + value;
        }

        return url;
    }


	public String url2(String key, String value) {
		String url  = "";

		if (Utility.isNotEmpty(area) && !"area".equals(key)) {
			url += "-" + area;
		}

		if (Utility.isNotEmpty(area) && "area".equals(key)) {
			//url += "-" + ((area.contains(value)) ? area : area + "," + value);
			url += "-" + value;
		}

		if (Utility.isEmpty(area) && "area".equals(key)) {
			url += "-" + value;
		}

		if (Utility.isNotEmpty(type) && !"type".equals(key)) {
			url += "/" + type;
		}

		if (Utility.isNotEmpty(type) && "type".equals(key)) {
			//url += "/" + ((type.contains(value)) ? type : type + "," + value);
			url += "/" + value;
		}

		if (Utility.isEmpty(type) && "type".equals(key)) {
			url += "/" + value;
		}



		return url;
	}

	public String message() {
		StringBuilder b = new StringBuilder();

		if(Utility.isNotEmpty(search)) {
			b.append("search term: ").append(search).append(" | ");
		}

		if(Utility.isNotEmpty(status)) {
			b.append("status: ").append(status).append(" | ");
		}

		if(disposed != null) {
			if(disposed) {
				b.append(Utility.isNotEmpty(mode) && mode.equals("rent")?"rented":"sold").append(" | ");
			} else {
				b.append("available ").append(" | ");
			}
		}

		if(similar != null) {
			if(similar) {
				b.append("sim available").append(" | ");
			}
		}

		if(Utility.isNotEmpty(state)) {
			b.append("state: ").append(Utility.capitalize(state)).append(" | ");
		}

		if(Utility.isNotEmpty(axis)) {
			b.append("axis: ").append(Utility.capitalize(axis)).append(" | ");
		}

		if(Utility.isNotEmpty(area)) {
			b.append("area: ").append(Utility.capitalize(area)).append(" | ");
		}

		if(Utility.isNotEmpty(type)) {
			b.append("type: ").append(Utility.capitalize(type)).append(" | ");
		}

		if(beds != null) {
			b.append("beds: ").append(beds).append(" | ");
		}
		if(baths != null) {
			b.append("baths: ").append(baths).append(" | ");
		}

		if(min_price != null) {
			b.append("min_price: ").append(min_price).append(" | ");
		}
		if(max_price != null) {
			b.append("max_price: ").append(max_price).append(" | ");
		}

		if(start_date != null) {
			b.append("start_date: ").append(start_date).append(" | ");
		}
		if(end_date != null) {
			b.append("end_date: ").append(end_date).append(" | ");
		}

		return b.toString();
	}

	public String stateAxis(){
		return state + "/" + axis;
	}

	public String stateAxisArea(){
		return stateAxis() + "/" + area;
	}

	public String stateAxisAreaType(){
		return stateAxisArea() + "/" + type;
	}


	public String getCaption() {
		return getHeader() + " " + address();
	}

	private String getHeader(){
		String header = "";

		if(Utility.isNotEmpty(search)) {
			header = "(" + search + ") ";
		}

		if(Utility.isNotEmpty(type)) {
			header += type;
		} else {
			header += "Property";
		}

		if(beds != null && beds > 0){
			header =  beds + " bedroom " + header;
		}

		header += " for " + mode;

		if(min_price != null && min_price > 0) {
			header += " from " + toPrice(min_price);
		}

		if(max_price != null && max_price > 0) {
			header += " to " + toPrice(max_price);
		}

		return header.replace('_', ' ').replace('-', ' ').trim();

	}

	private String toPrice(Double price) {
		DecimalFormat df = new DecimalFormat("#,###");
		return "N" + df.format(price);
	}

	private String address(){
		String address = (Utility.isNotEmpty(area) ? area + " ": "")
				+ (Utility.isNotEmpty(axis) ? axis + " ": "")
				+ (Utility.isNotEmpty(state) ? state + " ": "");
		return (Utility.isNotEmpty(address)) ? "in " + address : "";
	}
}
