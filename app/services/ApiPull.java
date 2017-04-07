package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import controllers.Singletons;
import controllers.Utility;
import models.Agent;
import models.AgentLead;
import models.Nameable;
import models.PropertyLead;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import pojos.AgentData;
import pojos.Location;
import pojos.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Created by seyi on 8/31/15.
 */
public class ApiPull {
    public static final String WEB_URL = "https://api.tolet.com.ng/api";
    public static final String AG_URL = "https://ag.tolet.com.ng/api";
    public static final String USERNAME = "tech@tolet";
    public static final String PASSWORD = "iWnMqbj@0OnYiXvHOb9H8";

    protected static WSClient ws = Singletons.ws;

    public static Agent[] getAgents(String startDate, String endDate) {
        try {
            String param = "?token="+PASSWORD;
            if(Utility.isNotEmpty(startDate)) {
                param += "&start_date="+startDate;
            }
            if(Utility.isNotEmpty(endDate)) {
                param += "&end_date="+endDate;
            }

            WSResponse response = ws.url(WEB_URL+"/agentList"+param)
                    .setAuth(USERNAME, PASSWORD)
                    .setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            Agent[] agents = gson.fromJson(jsonInput, Agent[].class);
            return agents;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static AgentData[] getAgentDatas() {
        try {
            WSResponse response = ws.url(AG_URL+"/agentDataList").get().toCompletableFuture().get();
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            AgentData[] datas = gson.fromJson(jsonInput, AgentData[].class);
            return datas;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static PropertyLead[] getPropertyLeads(String startDate, String endDate) {
        try {
            String param = "?token="+PASSWORD;
            if(Utility.isNotEmpty(startDate)) {
                param += "&start_date="+startDate;
            }
            if(Utility.isNotEmpty(endDate)) {
                param += "&end_date="+endDate;
            }

            WSResponse response = ws.url(WEB_URL+"/userRequestList"+param)
                    .setAuth(USERNAME, PASSWORD)
                    .setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            System.out.println(jsonInput);
            PropertyLead[] propertyLeads = gson.fromJson(jsonInput, PropertyLead[].class);
            return propertyLeads;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static AgentLead[] getAgentLeads(String startDate, String endDate) {
        try {
            String param = "?token="+PASSWORD;
            if(Utility.isNotEmpty(startDate)) {
                param += "&start_date="+startDate;
            }
            if(Utility.isNotEmpty(endDate)) {
                param += "&end_date="+endDate;
            }

            WSResponse response = ws.url(WEB_URL+"/agentLeadList"+param)
                    .setAuth(USERNAME, PASSWORD)
                    .setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            System.out.println(jsonInput);
            AgentLead[] agentLeads = gson.fromJson(jsonInput, AgentLead[].class);
            return agentLeads;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static Location.State[] getLocation() {
        try {
            WSResponse response = ws.url(WEB_URL+"/location")
                    //.setAuth(USERNAME, PASSWORD)
                    .setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            System.out.println(jsonInput);
            Location.State[] locations = gson.fromJson(jsonInput, Location.State[].class);
            return locations;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static Property getProperty(String pid) {
        try {
            WSResponse response = ws.url(WEB_URL+"/single-property/"+pid).setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            Property property = gson.fromJson(jsonInput, Property.class);
            return property;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }


    public static void postName(Nameable nameable, String type) {
        Exec.Ex.execute(() -> {
            JsonNode json = Json.newObject()
                    .put("id", nameable.id)
                    .put("name", nameable.name)
                    .put("hidden", nameable.hide);
            CompletionStage<WSResponse> resp = ws.url(WEB_URL+"/post"+type).post(json);
            try {
                System.out.println(resp.toCompletableFuture().get().getBody());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
