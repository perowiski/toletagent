package controllers;

import models.*;
import pojos.Param;
import services.CacheService;
import services.DBFilter;
import services.DBService;

import java.util.*;

/**
 * Created by seyi on 1/30/15.
 */
public class Selectors {
    private final static CacheService Cache = CacheService.C;

    public static final String SELECT_AGENTS = "selectAgents";
    public static Map<Long, Agent> selectAgents() {
        Map<Long, Agent> map = (Map<Long, Agent>) Cache.get(SELECT_AGENTS);
        if(map == null) {
            System.out.println(SELECT_AGENTS);
            map = new LinkedHashMap<>();
            DBFilter filter = DBFilter.get();
            filter.field("name").ne("");
            filter.field("active").ne(false);

            List<Agent> agents = new ArrayList<>();
            for(int i=0; i<Integer.MAX_VALUE; i++) {
                Param param = new Param(i, 200, "name");
                List<Agent> list = DBService.Q.find(Agent.class, filter, param);
                if(list.size() == 0) {
                    break;
                } else {
                    agents.addAll(list);
                }
            }
            for (Agent agent : agents) {
                map.put(agent.id, agent);
            }
            Cache.set(SELECT_AGENTS, map);
        }
        return map;
    }

    public static Map<Integer, String> selectNums() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for(int i = 1; i<11; i++) {
            if(i==10) {
                map.put(i, "10+");
            } else {
                map.put(i, String.valueOf(i));
            }
        }
        return map;
    }

    public static TreeSet<EState> STATES;
    public static TreeSet<EAxis> AXISES;
    public static TreeSet<EArea> AREAS;
    public static TreeSet<EType> TYPES;

    public static TreeSet<EState> listStates() {
        if(STATES == null) {
            STATES = new TreeSet<>(DBService.Q.find(EState.class, DBFilter.get().field("hide", false)));
        }
        return STATES;
    }

    public static TreeSet<EAxis> listAxises() {
        if(AXISES == null) {
            AXISES = new TreeSet<>(DBService.Q.find(EAxis.class, DBFilter.get().field("hide", false)));
        }
        return AXISES;
    }

    public static TreeSet<EArea> listAreas() {
        if(AREAS == null) {
            AREAS = new TreeSet<>(DBService.Q.find(EArea.class, DBFilter.get().field("hide", false)));
        }
        return AREAS;
    }

    public static TreeSet<EType> listTypes() {
        if(TYPES == null) {
            TYPES = new TreeSet<>(DBService.Q.find(EType.class, DBFilter.get().field("hide", false)));
        }
        return TYPES;
    }

    public static Map<String, String> selectStates() {
        Map<String, String> map = new LinkedHashMap<>();
        for(EState state: listStates()) {
            map.put(state.id.toString(), state.name);
        }
        return map;
    }

    public static Map<String, String> selectAxises() {
        Map<String, String> map = new LinkedHashMap<>();
        for (EAxis axis : listAxises()) {
            map.put(axis.id.toString(), axis.name);
        }
        return map;
    }

    public static Map<String, String> selectAreas() {
        Map<String, String> map = new LinkedHashMap<>();
        for (EArea area : listAreas()) {
            map.put(area.id.toString(), area.name);
        }
        return map;
    }

    public static Map<String, String> selectTypes() {
        Map<String, String> map = new LinkedHashMap<>();
        for(EType type : listTypes()) {
            map.put(type.id.toString(), type.name);
        }
        return map;
    }

    public static Map<String, Map<String, String>> selectStateAxises;
    public static Map<String, Map<String, String>> selectStateAxises() {
        if(selectStateAxises == null) {
            System.out.println("selectStateAxises");
            selectStateAxises = new LinkedHashMap<>();
            for (EState state : listStates()) {
                Map<String, String> axises = new LinkedHashMap<>();
                listAxises().forEach(axis -> {
                    if(axis.state!=null && state.id.equals(axis.state.id)) {
                        axises.put(axis.id.toString(), axis.name);
                    }
                });
                selectStateAxises.put(state.id.toString(), axises);
            }
        }
        return selectStateAxises;
    }

    public static Map<String, Map<String, String>> selectAxisAreas;
    public static Map<String, Map<String, String>> selectAxisAreas() {
        if(selectAxisAreas == null) {
            System.out.println("selectAxisAreas");
            selectAxisAreas = new LinkedHashMap<>();
            for (EAxis axis : listAxises()) {
                Map<String, String> areas = new LinkedHashMap<>();
                listAreas().forEach(area -> {
                    if(area.axis!=null && axis.id.equals(area.axis.id)) {
                        areas.put(area.id.toString(), area.name);
                    }
                });
                selectAxisAreas.put(axis.id.toString(), areas);
            }
        }
        return selectAxisAreas;
    }
}
