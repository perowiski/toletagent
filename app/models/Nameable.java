package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Nameable extends Hideable {
	@Getter @Setter
	@Constraints.Required
	public String name;
}
