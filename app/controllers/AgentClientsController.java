package controllers;

import authority.SecuredAgent;
import models.Agent;
import models.AgentClient;
import models.Subscription;
import models_enums.ProductType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentClientsController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private final Form<AgentClient> cForm;

    @Inject
    public AgentClientsController(DBService db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
        this.cForm = formFactory.form(AgentClient.class);
    }

    public Result index() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        filter.field("agent.id", agent.id);

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("phone").like(search)
                    .or().field("email").like(search)
                    .brE();
        }

        Utility.dateRange("created", filter);


        List<AgentClient> clients = db.find(AgentClient.class, filter, param);
        Long total = db.count(AgentClient.class, filter);

        map.put("clients", clients);
        map.put("total", total);
        return ok(agentClients.render(map));
    }

    public Result add() {
        return ok(addAgentClient.render(cForm, new AgentClient()));
    }

    public Result create() {
        Agent agent = Auth.getAgent();
        Form<AgentClient> formData = cForm.bindFromRequest();
        AgentClient client = new AgentClient();
        if (formData.hasErrors()) {
            return badRequest(addAgentClient.render(formData, client));
        }
        AgentClient form = formData.get();
        fill(client, form);
        client.agent = agent;
        db.save(client);

        Subscription subscription = db.findOne(Subscription.class, DBFilter.get().field("id").eq(agent.currentSubscription.id));

        for (Subscription.Product product : subscription.products) {
            if (product.productType == ProductType.CLIENT_DETAIL){
                product.setRemainder(product.remainder - 1);
                subscription.setProducts(subscription.products);
                db.save(subscription);
            }
        }

        agent.setCurrentSubscription(subscription);

        DBService.Q.merge(agent);
        Auth.cacheAgent(agent);

        return redirect(routes.AgentClientsController.index());
    }

    public Result edit(Long id) {
        AgentClient client = db.findOne(AgentClient.class, id);
        Form<AgentClient> formData = cForm.fill(client);
        return ok(addAgentClient.render(formData, client));
    }

    public Result update(Long id) {
        AgentClient client = db.findOne(AgentClient.class, id);
        Form<AgentClient> formData = cForm.bindFromRequest();
        if (formData.hasErrors()) {
            return badRequest(addAgentClient.render(formData, client));
        }
        AgentClient form = formData.get();
        fill(client, form);
        DBService.Q.save(client);
        return redirect(routes.AgentClientsController.index());
    }

    public static void fill(AgentClient client, AgentClient form) {
        client.name = form.name;
        client.email = form.email;
        client.phone = form.phone;
        client.maxBudget = form.maxBudget;
        client.minBudget = form.minBudget;
        client.pid = form.pid;
        client.source = form.source;
    }
}