package controllers;

import authority.SecuredAgent;
import models.Agent;
import models.AgentBrief;
import models.AgentConnect;
import models_enums.AgentConnectType;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.DBService;
import views.html.profile.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentBriefsController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private final Form<AgentBrief> rForm;
    @Inject
    public AgentBriefsController(DBService db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
        this.rForm = formFactory.form(AgentBrief.class);

    }

    public Result create() {
        return ok(sendBrief.render(rForm, new AgentBrief()));
    }

    public Result send() {
        Agent agent = Auth.getAgent();

        Form<AgentBrief> filledForm = rForm.bindFromRequest();
        AgentBrief brief = new AgentBrief();
        if (filledForm.hasErrors()) {
            return badRequest(sendBrief.render(filledForm, brief));
        } else {
            List<String> agentAxises = new ArrayList<>();
            filledForm.data().forEach((k, v) -> {
                if (k.startsWith("agentAxises")) {
                    agentAxises.add(v);
                }
            });
            String agentState = filledForm.data().get("agentState");

            if (agentAxises.isEmpty()) {
                filledForm.reject("agentAxises[]", "empty.");
                return badRequest(sendBrief.render(filledForm, brief));
            }

            AgentBrief form = filledForm.get();
            fill(form, brief);

            brief.agent = agent;
            
            db.save(brief);

            AgentConnect connect = new AgentConnect();
            connect.type = AgentConnectType.BRIR;
            connect.brief = brief;
            connect.message = brief.comment;
            connect.fromAgent = agent;

            AgentConnectsController.sendConnects(connect, agentState, agentAxises);

            flash("flashed", "Your brief has been sent successfully");
            flash("message", "Your brief has been sent successfully");

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public Result edit(Long id) {
        AgentBrief brief = DBService.Q.findOne(AgentBrief.class, id);
        Form<AgentBrief> filledForm = rForm.fill(brief);
        return ok(sendBrief.render(filledForm, brief));
    }

    public Result update(Long id) {
        AgentBrief brief = DBService.Q.findOne(AgentBrief.class, id);
        Form<AgentBrief> filledForm = rForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(sendBrief.render(filledForm, brief));
        } else {
            AgentBrief form = filledForm.get();
            fill(form, brief);
            db.save(brief);

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public static void fill(AgentBrief form, AgentBrief brief) {
        brief.mode = form.mode;
        brief.price = form.price;
        brief.type = form.type;
        brief.beds = form.beds;
        brief.axis = form.axis;
        brief.state = form.state;
        brief.comment = form.comment;
    }

    public Result sendProperty(String pid) {
        DynamicForm form = DynamicForm.form();
        return ok(sendProperty.render(form, pid));
    }

    public Result postProperty(String pid) {
        Agent agent = Auth.getAgent();
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String message = form.get("message");

        List<String> agentAxises = new ArrayList<>();
        form.data().forEach((k, v) -> {
            if (k.startsWith("agentAxises")) {
                agentAxises.add(v);
            }
        });

        if (agentAxises.isEmpty()) {
            form.reject("agentAxises[]", "empty.");
            return badRequest(sendProperty.render(form, pid));
        }

        String agentState = form.data().get("agentState");

        AgentConnect connect = new AgentConnect();
        connect.type = AgentConnectType.PROP;
        connect.fromAgent = agent;
        connect.pid = pid;
        connect.message = message;

        AgentConnectsController.sendConnects(connect, agentState, agentAxises);

        return redirect(routes.AgentConnectsController.sent());
    }
}