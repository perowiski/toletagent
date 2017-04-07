package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Aggregate implements Comparable<Aggregate>, Serializable {
	private static final long serialVersionUID = 3412589441261399674L;
	
	private String key;
	private Long count;

	public List<Aggregate> subAggregates = new ArrayList<>();

	public Aggregate(String key, long count){
		this.key = key;
		this.count = count;		
	}
	@Override
	public int compareTo(Aggregate o) {
		return key.compareTo(o.key);
		//return o.count.compareTo(count);
	}
	public String getTerm() {
		return key;
	}
	public void setTerm(String key) {
		this.key = key;
	}
	public long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	@Override
	public String toString(){
		return key.replace("_", " ")+"("+count+")";
	}	
	
}
