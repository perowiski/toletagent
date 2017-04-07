package search;

import lombok.Getter;
import lombok.Setter;
import pojos.Page;

import java.util.*;

public class SearchRes {
	@Getter @Setter public Page<PropertyIndex> results;
	@Getter @Setter public Map<String, List<Aggregate>> aggregates = new HashMap<>();
	public boolean isEmpty() {
		return results.isEmpty();
	}
}
