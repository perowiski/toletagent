package pojos;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

/**
 * Created by seyi on 12/15/16.
 */
public class Smtp {
    @Getter
    @Setter
    @Constraints.Required
    public String name;

    @Getter @Setter
    @Constraints.Required
    public String username;

    @Getter @Setter
    @Constraints.Required
    public String password;

    @Getter @Setter
    @Constraints.Required
    public String host;

    @Getter @Setter
    @Constraints.Required
    public Integer port;

    @Override
    public String toString() {
        return username + "<br/>" + password + "<br/>" + host + "<br/>" + port;
    }
}
