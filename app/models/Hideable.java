package models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Hideable extends Model {
	@Getter @Setter
	@Column(nullable=false)
	public boolean hide;
}
