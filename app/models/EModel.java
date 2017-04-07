package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;

@MappedSuperclass
public class EModel {
    @Id
    @Getter @Setter
    public Long id;

	@Getter @Setter
	@Constraints.Required
	public String name;

	@Getter @Setter
	@Column(nullable=false)
	public boolean hide;
}