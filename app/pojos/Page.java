package pojos;

import java.util.List;

public class Page<T> {

	private final long totalRowCount;
	private final List<T> list;
	
	public Page(List<T> data, long total) {
		this.list = data;
		this.totalRowCount = total;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public List<T> getList() {
		return list;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
	
}

