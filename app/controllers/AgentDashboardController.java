package controllers;

import authority.SecuredAgent;
import com.google.gson.JsonObject;
import models.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import search.Searcher;
import services.DBFilter;
import services.DBService;
import services.S3Service;
import views.html.profile.*;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

/**
 * @author seyi
 */

@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentDashboardController extends Controller {
    private final DBService db;
    private final FormFactory formFactory;
    private static Form<Agent> aForm;
    private static Form<Service> sForm;

    @Inject
    public AgentDashboardController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
        aForm = this.formFactory.form(Agent.class);
        sForm = this.formFactory.form(Service.class);
    }

    public Result index() {
        Map<String, Object> map = new HashMap<>();
        Long client = (Long) db.J.em().createQuery("SELECT count (distinct al.email) from AgentLead al where al.agentId = :agentId")
                .setParameter("agentId", Auth.getAgent().id)
                .getSingleResult();
        //Long agentLeads = db.count(AgentLead.class, DBFilter.get().field("agent", Auth.getAgent()));

        map.put("client", client);
        map.put("balance", Auth.getAgent().balance);
        //map.put("lead", agentLeads);
        return ok(dashboard.render(map));
    }

    public Result listingPost() {
        return ok(listingPost.render());
    }
    public Result listings(String mode) {
        return ok(listings.render(mode));
    }

    public Result agentLeads() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        filter.field("agentId", agent.id);

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("phone").like(search)
                    .or().field("email").like(search)
                    .brE();
        }

        //Utility.dateRange("created", filter);


        List<AgentLead> leads = DBService.Q.find(AgentLead.class, filter, param);
        Long total = DBService.Q.count(AgentLead.class, filter);

        map.put("leads", leads);
        map.put("total", total);
        return ok(agentLeads.render(map));
    }

    public Result propertyLeads() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        filter.field("agentId", agent.id.toString());

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("userName").like(search)
                    .or().field("userPhone").like(search)
                    .or().field("userEmail").like(search)
                    .brE();
        }

        //Utility.dateRange("created", filter);

        List<PropertyLead> list;
        Long total;

        String id = request().getQueryString("id");
        if(Utility.isNumeric(id)) {
            filter.field("id", Long.valueOf(id));
            list = db.find(PropertyLead.class, filter);
            total = 1L;
        } else {
            list = db.find(PropertyLead.class, filter, param);
            total = db.count(PropertyLead.class, filter);
        }

        map.put("propertyLeads", list);
        map.put("total", total);
        return ok(propertyLeads.render(map));
    }

    public Result propertyCount() {
        Agent agent = Auth.getAgent();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rented", Searcher.getUniquePropertyRentedCountForAgent(agent.id));
        jsonObject.addProperty("sold",  Searcher.getUniquePropertySoldCountForAgent(agent.id));
        return ok(Json.parse(jsonObject.toString()));
    }

    public Result propertyRentedForAgent() {
        Agent agent = Auth.getAgent();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rented", Searcher.getPropertyRentedForAgent(agent.id));
        jsonObject.addProperty("available", Searcher.getPropertyAvailableForRentForAgent(agent.id));
        return ok(Json.parse(jsonObject.toString()));
    }

    public Result propertySoldForAgent() {
        Agent agent = Auth.getAgent();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sold", Searcher.getPropertySoldForAgent(agent.id));
        jsonObject.addProperty("available", Searcher.getPropertyAvailableForSaleForAgent(agent.id));
        return ok(Json.parse(jsonObject.toString()));
    }

    public Result propertyPerAreaForAgent() {
        Agent agent = Auth.getAgent();
        // For each entry
        Map<String, Long> obj = new HashMap<>();
        for (Terms.Bucket entry : Searcher.getPropertiesPerAreaForAgent(agent.id)) {
            entry.getKey();      // Term
            entry.getDocCount(); // Doc count
            obj.put(entry.getKey().toString(), entry.getDocCount());
        }
        return ok(Json.toJson(obj));
    }

    public Result mostRequestedAreasForAgent() {
        //Agent agent = Auth.getAgent();
        Map<String, String> a = new HashMap<>();

        //List propertyLead = db.J.em().createQuery("SELECT pl.axisId, count(pl.axisId) as ct from PropertyLead pl where pl.agentId = :agentId group by pl.axisId order by ct DESC").setParameter("agentId", Auth.getAgent().getId().toString()).setMaxResults(5).getResultList();
        List propertyLead = db.J.em().createQuery("SELECT pl.axisId, count(pl.axisId) as ct from PropertyLead pl group by pl.axisId order by ct DESC").setMaxResults(5).getResultList();

        for (Iterator i =  propertyLead.iterator(); i.hasNext();) {
            Object[] values = (Object[]) i.next();
            EAxis axis = db.findOne(EAxis.class, (Long)values[0]);
            a.put(axis.name, Long.toString((Long) values[1]));
            System.out.println(axis.name + " " + values[0] + " ----- " + (Long)values[1]);
        }

        return ok(Json.toJson(a));
    }

    public Result updateView() {
        Form<Agent> filledForm = aForm.fill(Auth.getAgent());
        Agent agent = Auth.getAgent();
        return ok(profileUpdate.render(filledForm, agent));
    }

    public Result update () {
        Form<Agent> filledForm = aForm.bindFromRequest();

        Agent agent = Auth.getAgent();

        if(filledForm.hasErrors()) {
            return badRequest(profileUpdate.render(filledForm, agent));
        }

        Agent form = filledForm.get();

        agent.setAbout(form.getAbout());
        saveAgent(agent);
        flash("success", "Successfully updated!");
        return redirect(routes.AgentDashboardController.updateView());
    }

    public Result addLogo () throws Exception {

        JsonObject jsonObject = new JsonObject();

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> logo = body.getFile("file");

        if (body.getFiles().size() == 0) {
            jsonObject.addProperty("success", false);
            return badRequest(Json.parse(jsonObject.toString()));
        }

        Agent agent = Auth.getAgent();
        String filename = agent.id + logo.getFilename();

        S3Service.storeLogo(logo.getFile(), filename);
        agent.setLogoName(filename);
        saveAgent(agent);
        jsonObject.addProperty("success", true);
        flash("success", "Logo successfully updated!");
        return ok(Json.parse(jsonObject.toString()));
    }

    public static void saveAgent(Agent agent) {
        DBService.Q.merge(agent);
        Auth.cacheAgent(agent);
    }

}
