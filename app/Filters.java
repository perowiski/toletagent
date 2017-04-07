import play.mvc.EssentialFilter;
import play.http.HttpFilters;

import javax.inject.Inject;

public class Filters implements HttpFilters {

    private EssentialFilter[] filters;

    @Inject
    public Filters() {
        filters = new EssentialFilter[] {
                //gzipFilter.asJava(),
        };
    }

    public EssentialFilter[] filters() {
        return filters;
    }
}