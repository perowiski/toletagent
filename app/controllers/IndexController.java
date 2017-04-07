package controllers;

import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.*;
import services.*;

import javax.inject.Inject;

/**
 * @author seyi
 */

@Transactional
public class IndexController extends Controller {
    private final DBService db;
    private final FormFactory formFactory;

    @Inject
    public IndexController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
    }

    public Result index() {
        if(Auth.isAdmin()) {
            return redirect(routes.AdminDashboardController.index());
        }
        if(Auth.isAgent()) {
            return redirect(routes.AgentDashboardController.index());
        }
        return redirect(routes.AuthController.index());
    }

    public Result untrail(String path) {
        return movedPermanently("/" + path);
    }
}
