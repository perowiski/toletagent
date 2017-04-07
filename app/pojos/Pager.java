package pojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seyi on 1/27/15.
 */
public class Pager {
    private int size;
    private long totalRowCount;
    private int page;
    private List<Integer> list = new ArrayList<>();

    public Pager(long total, int page, int size) {
        this.totalRowCount = total;
        this.page = page;
        this.size = size;

        if(getTotalPages() > 6) {
            int current = page + 1;
            int offset = getTotalPages() - current;
            int upper = current + ((offset>3) ? 3 : offset);
            int lower = current - ((current>2) ? 2 : current);
            if(lower==0) lower = 1;
            for(int i = lower; i <= upper; i++ ) { list.add(i);}
        } else {
            for(int i = 1; i <= getTotalPages(); i++ ) { list.add(i);}
        }
    }

    public int getTotalPages() {
        Long total = ((this.totalRowCount % size == 0) ? this.totalRowCount / size : this.totalRowCount / size + 1);
        return total.intValue();
    }

    public boolean hasPrev() {
        return page > 0;
    }

    public boolean hasNext() {
        return getTotalPages() > (page + 1);
    }

    public List<Integer> getList() { return list; }

    public String getDisplayXtoYofZ() {
        int start = page * size + 1;
        //int end = start + Math.min(size, list.size());
        int end = page * size + size;
        if(totalRowCount < end) end = (int)totalRowCount;
        return start + " - " + end + " of <strong>" + totalRowCount + "</strong>";
    }

    public String getCurrentUrl(String uri, int index) {
        String url = uri.replaceAll("\\&page=\\d*", "").replaceAll("page=\\d*", "")
                .replaceAll("\\&limit=\\d*", "").replaceAll("limit=\\d*", "");
        if(url.contains("?")) {
            return url + "&limit=" + this.getSize() + "&page=" + index;
        } else {
            return url + "?limit=" + this.getSize() + "&page=" + index;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(long totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }
}
