package pojos;

public class Param {
	private int page;
	private int size;
	private String sort;
    private String order = "ASC";

	public Param(int page, int size) {
		this.page = page;
		this.size = size;
	}

	public Param(int page, int size, String sort) {
		this.page = page;
		this.size = size;
		this.sort = sort;
	}

	public int getOffset(){
		return this.page * this.size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
