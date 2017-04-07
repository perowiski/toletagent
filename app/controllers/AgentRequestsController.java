package controllers;

import authority.SecuredAgent;
import models.Agent;
import models.AgentConnect;
import models.AgentRequest;
import models.EAxis;
import models_enums.AgentConnectType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentRequestsController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private final Form<AgentRequest> rForm;

    @Inject
    public AgentRequestsController(DBService db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
        this.rForm = formFactory.form(AgentRequest.class);
    }

    public Result create() {
        return ok(sendRequest.render(rForm, new AgentRequest()));
    }

    public Result send() {
        Agent agent = Auth.getAgent();

        Form<AgentRequest> filledForm = rForm.bindFromRequest();
        AgentRequest req = new AgentRequest();
        if (filledForm.hasErrors()) {
            return badRequest(sendRequest.render(filledForm, req));
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
                return badRequest(sendRequest.render(filledForm, req));
            }

            fill(filledForm, req);

            req.agent = agent;

            db.save(req);

            AgentConnect connect = new AgentConnect();
            connect.type = AgentConnectType.CLIR;
            connect.request = req;
            connect.message = req.comment;
            connect.fromAgent = agent;

            AgentConnectsController.sendConnects(connect, agentState, agentAxises);

            flash("flashed", "Your request has been sent successfully");
            flash("message", "Your request has been sent successfully");

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public Result edit(Long id) {
        AgentRequest req = DBService.Q.findOne(AgentRequest.class, id);
        Form<AgentRequest> filledForm = rForm.fill(req);

        for (EAxis axis : req.axises) {
            filledForm.data().put("axises[" + (axis.id.toString()) + "]",
                    axis.id.toString());
        }

        return ok(sendRequest.render(filledForm, req));
    }

    public Result update(Long id) {
        AgentRequest req = DBService.Q.findOne(AgentRequest.class, id);
        Form<AgentRequest> filledForm = rForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(sendRequest.render(filledForm, req));
        } else {
            fill(filledForm, req);
            db.save(req);

            return redirect(routes.AgentConnectsController.sent());
        }
    }

    public static void fill(Form<AgentRequest> filledForm, AgentRequest request) {
        AgentRequest form = filledForm.get();

        request.mode = form.mode;
        request.beds = form.beds;
        request.maxBudget = form.maxBudget;

        Set<EAxis> axisList = new HashSet<>();
        filledForm.data().forEach((k, v) -> {
            if (k.startsWith("axisList")) {
                axisList.add(DBService.Q.findOne(EAxis.class, v));
            }
        });
        request.axises = axisList;

        request.state = form.state;
        request.comment = form.comment;
    }
}