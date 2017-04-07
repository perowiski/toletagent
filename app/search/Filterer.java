package search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;


/**
 * Created by seyi on 7/15/15.
 */
public class Filterer {
    private BoolQueryBuilder filter = QueryBuilders.boolQuery();
    public boolean has;

    public Field field(String name) {
        has = true;
        return new Field(name);
    }

    public Filterer field(String name, String value) {
        has = true;
        Field field = new Field(name);
        Filterer filterer;
        if(value.contains(",")) {
            filterer = field.in(Arrays.asList(value.split(",")));
        } else {
            filterer = field.is(value.trim());
        }
        return filterer;
    }

    public BoolQueryBuilder get() {
        return filter;
    }

    public class Field {
        private String key;
        public Field(String key) {
            this.key = key;
        }

        public Filterer is(Object value) {
            filter.must(termQuery(key, value));
            return Filterer.this;
        }
        public Filterer not(Object value) {
            filter.mustNot(termQuery(key, value));
            return Filterer.this;
        }
        public Filterer from(Object value) {
            filter.must(rangeQuery(key).from(value));
            return Filterer.this;
        }
        public Filterer to(Object value) {
            filter.must(rangeQuery(key).to(value));
            return Filterer.this;
        }
        public Filterer range(Object start, Object end) {
            filter.must(rangeQuery(key).from(start).to(end));
            return Filterer.this;
        }
        public Filterer in(Object[] array) {
            if(array.length > 0) {
                BoolQueryBuilder bool = boolQuery();
                for (Object value : array) {
                    bool.should(termQuery(key, value));
                }
                filter.must(bool);
            }
            return Filterer.this;
        }
        public Filterer in(List<String> array) {
            if(array.size() > 0) {
                BoolQueryBuilder bool = boolQuery();
                for (String value : array) {
                    bool.should(termQuery(key, value.trim()));
                }
                filter.must(bool);
            }
            return Filterer.this;
        }
        public Filterer empty() {
            filter.mustNot(existsQuery(key));
            return Filterer.this;
        }
        public Filterer notEmpty() {
            filter.must(existsQuery(key));
            return Filterer.this;
        }
    }

    private Filterer() {}

    public static Filterer start() {
        return new Filterer();
    }
}
