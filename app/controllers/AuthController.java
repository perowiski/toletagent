package controllers;

import models.Admin;
import models.Agent;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import pojos.Credentials;
import services.ApiPull;
import services.DBFilter;
import services.DBService;
import views.html.login;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * @author seyi
 */

@Transactional
public class AuthController extends Controller {
	private final DBService db;
	private final FormFactory formFactory;
	private static Form<Credentials> loginForm;

	@Inject
	public AuthController(FormFactory formFactory, DBService db) {
		this.formFactory = formFactory;
		this.db = db;
		loginForm = this.formFactory.form(Credentials.class);
	}

    public Result index() {
        /*
		Admin admin = new Admin();
		admin.name = "Admin";
		admin.active = true;
		admin.email = "admin@tolet.com.ng";
		admin.password = "admin";
		db.save(admin);
		*/

		if(Auth.isAdmin()) {
			return redirect(routes.IndexController.index());
		}
		return ok(login.render(loginForm));
    }

	public Result login() {
		Form<Credentials> filledForm = loginForm.bindFromRequest();
		if (filledForm.hasErrors()) {
            flash("message", "Invalid email/password combination.");
            return badRequest(login.render(filledForm));
		} else {
			Credentials credentials = filledForm.get();
			String email = credentials.getEmail().trim();
			String password = credentials.getPassword().trim();

			DBFilter filter = DBFilter.get();
			filter.field("email", email);

			boolean isLogin = false;
			
			Admin admin = db.findOne(Admin.class, filter);
			if(admin != null && password.equals(admin.getPassword())) {
				if(!admin.isActive()) {
					filledForm.reject("Admin is not active, please contact the administrator.");
					return badRequest(login.render(filledForm));
				}
				Auth.setAdmin(admin);
				isLogin = true;
			}

			if(!isLogin) {
                Agent agent = db.findOne(Agent.class, filter);
                if(agent != null) {
                    boolean basicAuth = basicAuth(email, password);
                    if(basicAuth) {
                        Auth.setAgent(agent);
                        isLogin = true;
                    }
                }
            }

			if(!isLogin) {
				filledForm.reject("Invalid email/password combination.");
				return badRequest(login.render(filledForm));
			}

			String lastUrl = session().get("lastUrl");
			if(Utility.isNotEmpty(lastUrl)) {
				return redirect(lastUrl);
			}
			return redirect(routes.IndexController.index());
		}
	}

	public static final String BAU = "BASIC_AUTH_UUID";

    public static boolean basicAuth(String email, String password) {
        CompletionStage<WSResponse> resp = Singletons.ws.url(ApiPull.WEB_URL+"/agentLogin")
                .setAuth(email, password).get();
        try {
            String ret = resp.toCompletableFuture().get().getBody();
            if(Utility.isNotEmpty(ret) && ret.startsWith("success-")) {
                String uuid = ret.replace("success-", "");
                Cookies.set(BAU, uuid);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

	public Result logout() {
		session().clear();
		flash("message", "You've been logged out");
		return redirect(routes.AuthController.index());
	}

	public Result firstAccount() {
		Admin admin = new Admin();
		admin.setEmail("admin@tolet.com.ng");
		admin.setPassword("admin");
		admin.setName("Admin");
		admin.setActive(true);
		db.save(admin);
		return redirect(routes.AuthController.index());
	}

}
