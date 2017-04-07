package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import models.Agent;
import models.AgentLead;
import models.Model;
import models.PropertyLead;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.ApiSave;
import services.DBService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author seyi
 */

@Transactional
public class ApiController extends Controller {
    private final DBService db;
    private final FormFactory formFactory;

    @Inject
    public ApiController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
    }

    public Result sendModels() {
        List<Map<String, Object>> models = new ArrayList<>();
        JsonNode json = Json.toJson(models);
        return ok(json);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result postAgent() {
        JsonNode json = request().body().asJson();

        String j = json.toString();
        Gson gson = new Gson();

        Agent agent = gson.fromJson(j, Agent.class);
        ApiSave.saveAgent(agent);
        return ok("Successful AGT");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result postAgentLead() {
        JsonNode json = request().body().asJson();
        String j = json.toString();
        Gson gson = new Gson();
        AgentLead agentLead = gson.fromJson(j, AgentLead.class);
        ApiSave.saveAgentLead(agentLead);
        return ok("Successful AGT");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result postPropertyLead() {
        JsonNode json = request().body().asJson();
        String j = json.toString();
        Gson gson = new Gson();
        PropertyLead propertyLead = gson.fromJson(j, PropertyLead.class);
        ApiSave.savePropertyLead(propertyLead);
        return ok("Successful AGT");
    }

    public Result untrail(String path) {
        return movedPermanently("/" + path);
    }
}
