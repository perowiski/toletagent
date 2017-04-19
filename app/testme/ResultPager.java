package testme;

import lombok.Getter;
import lombok.Setter;
import models.Agent;
import services.DBService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tolet on 4/10/17.
 */
public class ResultPager<T>{

    @Getter @Setter
    private int currentPage = 0;
    @Getter @Setter
    private int pageSize = 50;
    private String sql;
    private Class<T> entityType;

    public ResultPager(Class<T> entityType, String sql, int currentPage, int pageSize){
        this(entityType, sql);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public ResultPager(Class<T> entityType, String sql){
        this.entityType = entityType;
        this.sql = sql;
    }

    public List<T> find(){
        EntityManager entityManager = DBService.J.em();
        TypedQuery<T> typedQuery = entityManager.createQuery(sql, entityType)
                .setFirstResult(currentPage * pageSize)
                .setMaxResults(currentPage);
        return typedQuery.getResultList();
    }

    public <T> long getMaxResults(){
        EntityManager entityManager = DBService.J.em();
        return  entityManager.createQuery(sql, entityType).getResultList().size();
    }

    public static void main(String args[]){
        ResultPager resultPager = new ResultPager(Agent.class, "sql");
        List<Agent> agentList = resultPager.find();
        long total = resultPager.getMaxResults();
    }

    public void builder(){
        StringBuilder stringBuilder = new StringBuilder();
    }
    //select * from Employee where age = ? and name = ?

    private class QueryBuilder{

        private String[] columns;
        private  StringBuilder stringBuilder;

        public QueryBuilder(String... columns){
            this.columns = columns;
        }

        public QueryBuilder(){

        }

    }
}

