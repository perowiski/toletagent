package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seyi on 2/12/15.
 */
public class DBFilter {
    private StringBuilder sql = new StringBuilder();
    private Map<String, Object> params = new HashMap<>();

    public Field field(String name) {
        return new Field(name);
    }

    public DBFilter field(String name, Object value) {
        return new Field(name).eq(value);
    }

    public DBFilter where() {
        sql.append(" WHERE ");
        return this;
    }

    public DBFilter join(String join) {
        sql.append(" JOIN ").append(join).append(" ");
        return this;
    }

    public DBFilter or() {
        sql.append(" OR ");
        return this;
    }

    public DBFilter brS() {
        sql.append(" ( ");
        return this;
    }

    public DBFilter brE() {
        sql.append(" ) AND ");
        return this;
    }

    public String getSql() {
        String s = sql.toString();
        s = s.trim();
        if(s.endsWith("AND"))
            s = s.substring(0, s.length()-3);
        if(s.endsWith("OR"))
            s = s.substring(0, s.length()-2);
        if(s.endsWith("WHERE"))
            s = s.substring(0, s.length()-5);
        s = s.replaceAll("AND +OR", "OR")
                .replaceAll("AND +\\)", ")")
                .replaceAll("OR +\\)", ")");
        return s;
    }

    public Map<String, Object> getParams() {
        return params;
    }


    private DBFilter() {}

    private DBFilter(StringBuilder sql, Map<String, Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public DBFilter copy() {
        return new DBFilter(new StringBuilder(sql.toString()), new HashMap<>(params));
    }

    public class Field {
        private String name;
        public Field(String name) {
            this.name = name;
        }

        public DBFilter eq(Object value) {
            sql.append(name).append(" = :").append(key()).append(" AND ");
            params.put(key(), value);
            return DBFilter.this;
        }
        public DBFilter ne(Object value) {
            String key = key() + value.toString().replaceAll("[^A-Za-z]", "");
            sql.append(name).append(" != :").append(key).append(" AND ");
            params.put(key, value);
            return DBFilter.this;
        }
        public DBFilter like(String value) {
            sql.append(name).append(" LIKE :").append(key()).append(" AND ");
            params.put(key(), "%"+value+"%");
            return DBFilter.this;
        }
        public DBFilter gt(Object value) {
            sql.append(name).append(" > :").append(key()).append(" AND ");
            params.put(key(), value);
            return DBFilter.this;
        }
        public DBFilter lt(Object value) {
            sql.append(name).append(" < :").append(key()).append(" AND ");
            params.put(key(), value);
            return DBFilter.this;
        }
        public DBFilter gte(Object value) {
            sql.append(name).append(" >= :").append(key()).append(" AND ");
            params.put(key(), value);
            return DBFilter.this;
        }
        public DBFilter lte(Object value) {
            sql.append(name).append(" <= :").append(key()).append(" AND ");
            params.put(key(), value);
            return DBFilter.this;
        }
        public DBFilter isNull() {
            sql.append(name).append(" IS NULL AND ");
            return DBFilter.this;
        }
        public DBFilter notNull() {
            sql.append(name).append(" IS NOT NULL AND ");
            return DBFilter.this;
        }
        public DBFilter contains(Object value) {
            String val = value.toString().replaceAll("[^A-Za-z]", "");
            sql.append(" :").append(val).append(" MEMBER OF ").append(name).append(" AND ");
            params.put(val, value);

            return DBFilter.this;
        }
        public DBFilter between(Object start, Object end) {
            String startKey = key()+"Start";
            String endKey = key()+"End";
            sql.append(name).append(" BETWEEN :").append(startKey).append(" AND :").append(endKey).append(" AND ");
            params.put(startKey, start);
            params.put(endKey, end);
            return DBFilter.this;
        }
        public DBFilter in(List<Object> array) {
            String key = key()+"Array";
            sql.append(name).append(" IN :").append(key).append(" AND ");
            params.put(key, array);
            return DBFilter.this;
        }
        public DBFilter nin(List<Object> array) {
            String key = key()+"Array";
            sql.append(name).append(" NOT IN :").append(key).append(" AND ");
            params.put(key, array);
            return DBFilter.this;
        }
        private String key() {
            return name.replaceAll("[^A-Za-z]", "");
        }
    }

    public static DBFilter get() {
        DBFilter filter = new DBFilter();
        return filter.where();
    }

    public static DBFilter instance() {
        DBFilter filter = new DBFilter();
        return filter;
    }
}
