package pojos;

import lombok.Data;
import play.data.validation.Constraints;

@Data
public class Credentials {
	@Constraints.Required(message = "Please enter your email address!!!")
	@Constraints.Email(message = "Please enter your a valid email address!!!")
	public String email;

	@Constraints.Required(message = "Please enter your password!!!")
	public String password;
}