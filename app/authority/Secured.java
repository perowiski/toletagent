package authority;

import controllers.Auth;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * @author seyi
 */

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get(Auth.ADMIN);
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
    	ctx.session().put("lastUrl", ctx.request().uri());
        return redirect(controllers.routes.AuthController.index());
    }   
}