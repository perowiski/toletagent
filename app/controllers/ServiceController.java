package controllers;

import authority.SecuredAgent;
import models.Service;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DBFilter;
import services.DBService;
import views.html.profile.addServiceForm;
import views.html.profile.service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Onuche Idoko on 2/24/17.
 */
@Security.Authenticated(SecuredAgent.class)
@Transactional
public class ServiceController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;
    private static Form<Service> sForm;

    @Inject
    public ServiceController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
        sForm = this.formFactory.form(Service.class);
    }

    public Result service() {
        Param param = Utility.getParam();
        param.setSort("created DESC");

        DBFilter filter = DBFilter.get().field("agent", Auth.getAgent());

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .brE();
        }

        List<Service> services = db.find(Service.class, filter, param);
        Long total = db.count(Service.class, filter);
        return ok(service.render(services, total));
    }

    public Result add() {
        return ok(addServiceForm.render(sForm, new Service()));
    }

    public Result create() {
        Form<Service> filledForm = sForm.bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(addServiceForm.render(filledForm, new Service()));
        } else {
            filledForm.get().setAgent(Auth.getAgent());
            db.save(filledForm.get());
            flash("success", "Service successfully added!");
            return redirect(routes.ServiceController.service());
        }
    }

    public Result edit(Long id) {
        Service admin = db.findOne(Service.class, id);
        Form<Service> filledForm = sForm.fill(admin);
        return ok(addServiceForm.render(filledForm, admin));
    }

    public Result update(Long id) {
        Service service = db.findOne(Service.class, id);
        Form<Service> filledForm = sForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(addServiceForm.render(filledForm, service));
        } else {
            service.setDescription(filledForm.get().description);
            service.setName(filledForm.get().name);
            db.save(service);
            flash("success", "Service successfully updated!");
            return redirect(routes.ServiceController.service());
        }
    }

}
