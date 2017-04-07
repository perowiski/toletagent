package controllers;

import authority.Secured;
import com.google.gson.JsonObject;
import models.Admin;
import models.Agent;
import models.AgentCredit;
import models.Subscription;
import models_enums.AgentProduct;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DBFilter;
import services.DBService;
import views.html.*;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.*;

/**
 * @author seyi
 */

@Security.Authenticated(Secured.class)
@Transactional
public class AdminCrudController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private static Form<Admin> aForm;
    private static Form<AgentCredit> cForm;

    @Inject
    public AdminCrudController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
        aForm = this.formFactory.form(Admin.class);
        cForm = this.formFactory.form(AgentCredit.class);
    }

    public Result single(Long id) {
        Admin admin = db.findOne(Admin.class, id);
        List<Admin> list = Arrays.asList(admin);
        return ok(administrators.render(list, 1L));
    }

    public Result index() {
        Param param = Utility.getParam();
        param.setSort("created DESC");

        DBFilter filter = DBFilter.get();

        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("email").like(search)
                    .brE();
        }

        List<Admin> list = db.find(Admin.class, filter, param);
        Long total = db.count(Admin.class, filter);

        return ok(administrators.render(list, total));
    }

    public Result add() {
        return ok(adminForm.render(aForm, new Admin()));
    }

    public Result create() {
        Form<Admin> filledForm = aForm.bindFromRequest();
        validate(db, filledForm, new Admin());

        if (filledForm.hasErrors()) {
            return badRequest(adminForm.render(filledForm, new Admin()));
        } else {
            Admin created = new Admin();
            fill(filledForm, created);
            created.setActive(true);
            db.save(created);
            return redirect(routes.AdminCrudController.single(created.getId()));
        }
    }

    public Result edit(Long id) {
        Admin admin = db.findOne(Admin.class, id);
        Form<Admin> filledForm = aForm.fill(admin);
        return ok(adminForm.render(filledForm, admin));
    }

    public Result update(Long id) {
        Admin admin = db.findOne(Admin.class, id);
        Form<Admin> filledForm = aForm.bindFromRequest();
        validate(db, filledForm, admin);
        if (filledForm.hasErrors()) {
            return badRequest(adminForm.render(filledForm, admin));
        } else {
            fill(filledForm, admin);
            db.save(admin);
            return redirect(routes.AdminCrudController.single(admin.getId()));
        }
    }

    public Result activate(Long id) {
        Admin admin = db.findOne(Admin.class, id);
        admin.setActive(!admin.isActive());
        db.save(admin);
        return redirect(routes.AdminCrudController.single(admin.getId()));
    }

    private static void validate(DBService db, Form<Admin> filledForm, Admin admin) {

        if(admin.getId() == null) {
            String password = filledForm.field("password").valueOr("");
            if(Utility.isEmpty(password)) {
                filledForm.reject("password", "Enter a password");
            }
            if(password.length() < 6) {
                filledForm.reject("password", "Your password must be at least 6 characters");
            }
        }

        // Check repeated password
        if (Utility.isNotEmpty(filledForm.field("password").value())) {
            if (!filledForm.field("password").valueOr("").equals(filledForm.field("confirmPass").value())) {
                filledForm.reject("confirmPass", "Passwords don't match");
            }
        }


        String email = filledForm.field("email").valueOr("").trim();

        Admin acct = db.findOne(Admin.class, "email", email);
        if(acct != null && !acct.getId().equals(admin.getId())) {
            filledForm.reject("email", "This email is already registered");
        }
    }

    private static void fill(Form<Admin> filledForm, Admin admin) {
        Admin form = filledForm.get();
        admin.setEmail(form.getEmail());
        admin.setName(form.getName());
        if(Utility.isNotEmpty(form.getPassword())) {
            admin.setPassword(form.getPassword());
        }
    }

    public Result agents (String search) {
        Param param = Utility.getParam(5);
        List<Agent> list = db.find(Agent.class, DBFilter.get().field("username").like(search), param);
        return ok(Json.toJson(list));
    }

    public Result addCredit() {
        Form<AgentCredit> filledForm = cForm.bindFromRequest();

        JsonObject jsonObject = new JsonObject();

        Agent agent = db.findOne(Agent.class, formFactory.form().bindFromRequest().get("agent_id"));

        if (filledForm.hasErrors()) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("error", filledForm.error("amount").message());
            return badRequest(Json.parse(jsonObject.toString()));
        }

        AgentCredit added = new AgentCredit();
        added.setAgent(agent);
        added.setAmount(filledForm.value().get().amount);
        db.save(added);
        agent.setBalance(agent.balance + filledForm.value().get().amount);
        db.J.em().persist(agent);
        jsonObject.addProperty("success", true);
        flash("success", filledForm.value().get().amount + " has been credited to " + agent.name);
        return ok(Json.parse(jsonObject.toString()));

    }

    public Result viewCredit() {
        Param param = Utility.getParam();
        String search = request().getQueryString("search");
        DBFilter filter = DBFilter.get();
        if(Utility.isNotEmpty(search)) {
            filter.field("ac.agent.name").like(search);
        }
        System.out.println(filter.getSql());
        Query jpql = db.J.em().createQuery("SELECT sum (ac.amount), ac.agent.name, ac.agent.id from AgentCredit ac " + filter.getSql() + " group by ac.agent order by ac.created desc");
        if(Utility.isNotEmpty(search)) {
            jpql.setParameter("acagentname", "%"+search+"%");
        }
        jpql.setFirstResult(param.getOffset()).setMaxResults(param.getSize());

        List<Object[]> agentCredits = jpql.getResultList();

        Integer total = agentCredits.size();
        return ok(allCredit.render(agentCredits, total.longValue()));
    }

    public Result viewCreditHistory(String id) {
        Agent agent = db.findOne(Agent.class, id);
        Param param = Utility.getParam();
        param.setSort("created DESC");
        DBFilter filter = DBFilter.get().field("agent", agent);
        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("created").like(search)
                    .brE();
        }
        List<AgentCredit> creditList = db.find(AgentCredit.class, DBFilter.get().field("agent", agent), param);
        Long total = db.count(AgentCredit.class, filter);
        System.out.println(total);
        return ok(creditHistory.render(creditList, total));
    }

    public static List<AgentProduct> products(){
        return Arrays.asList(AgentProduct.values());
    }

    public Result setProduct () {

        JsonObject jsonObject = new JsonObject();

        // get request value from submitted form
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        Optional<String[]> checkedVal = Optional.ofNullable( map.get("products") ); // get selected products

        Agent agent = db.findOne(Agent.class, formFactory.form().bindFromRequest().get("agent"));

        Set<AgentProduct> agentProducts = new HashSet<>();

        if (!checkedVal.isPresent()) {
            jsonObject.addProperty("success", false);
            return badRequest(Json.parse(jsonObject.toString()));
        }

        for (String value : checkedVal.get()) {
            AgentProduct product = AgentProduct.valueOf(value);
            agentProducts.add(product);
        }

        agent.products = agentProducts;
        db.J.em().merge(agent);
        jsonObject.addProperty("success", true);
        return ok(Json.parse(jsonObject.toString()));
    }

    public Result getProducts(Long id) {
        Agent agent = db.findOne(Agent.class, id);
        Set<AgentProduct> agentProducts = agent.products;
        return ok(Json.toJson(agentProducts));
    }


    }