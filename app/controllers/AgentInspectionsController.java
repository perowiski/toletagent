package controllers;

import authority.SecuredAgent;
import models.Agent;
import models.AgentClient;
import models.AgentInspection;
import models.PropertyLead;
import models_enums.NotificationStatus;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DBFilter;
import services.DBService;
import views.html.profile.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentInspectionsController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private final Form<AgentInspection> rForm;

    @Inject
    public AgentInspectionsController(DBService db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
        this.rForm = formFactory.form(AgentInspection.class);
    }

    public Result index() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        filter.field("agent.id", agent.id);

        Utility.dateRange("dateOfInspection", filter);


        List<AgentInspection> list = db.find(AgentInspection.class, filter, param);
        Long total = db.count(AgentInspection.class, filter);

        map.put("inspections", list);
        map.put("total", total);
        return ok(inspections.render(map));
    }

    public Result schedule() {
        Agent agent = Auth.getAgent();

        DynamicForm form = DynamicForm.form().bindFromRequest();

        String type = form.get("type");
        String date = form.get("date");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String message = "success";

        if(Utility.isNotEmpty(date)) {
            try {
                Long id = Long.valueOf(form.get("id"));
                Date d = format.parse(date);
                AgentInspection inspection = new AgentInspection();
                inspection.dateOfInspection = d;
                inspection.setNotificationStatus(NotificationStatus.WAITING);
                if("request".equals(type)) {
                    PropertyLead propertyLead = db.findOne(PropertyLead.class, id);
                    inspection.propertyLead = propertyLead;
                } else {
                    AgentClient client = db.findOne(AgentClient.class, id);
                    inspection.client = client;
                }
                inspection.agent = agent;
                db.save(inspection);
            } catch (Exception e) {
                message = e.getMessage();
            }
        }
        return ok(message);
    }


    public Result add() {
        return ok(addInspection.render(rForm, new AgentInspection()));
    }

    public Result create() {
        Agent agent = Auth.getAgent();

        Form<AgentInspection> filledForm = rForm.bindFromRequest();
        AgentInspection inspection = new AgentInspection();
        if (filledForm.hasErrors()) {
            return badRequest(addInspection.render(filledForm, inspection));
        } else {
            AgentInspection form = filledForm.get();
            fill(form, inspection);

            inspection.agent = agent;
            
            db.save(inspection);

            flash("flashed", "Your inspection has been scheduled successfully");
            flash("message", "Your inspection has been scheduled successfully");

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public Result edit(Long id) {
        AgentInspection inspection = DBService.Q.findOne(AgentInspection.class, id);
        Form<AgentInspection> filledForm = rForm.fill(inspection);
        return ok(addInspection.render(filledForm, inspection));
    }

    public Result update(Long id) {
        AgentInspection inspection = DBService.Q.findOne(AgentInspection.class, id);
        Form<AgentInspection> filledForm = rForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(addInspection.render(filledForm, inspection));
        } else {
            AgentInspection form = filledForm.get();
            fill(form, inspection);
            db.save(inspection);

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public static void fill(AgentInspection form, AgentInspection inspection) {
        inspection.dateOfInspection = form.dateOfInspection;
    }
}