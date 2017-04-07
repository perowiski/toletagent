package models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * @author seyi
 */

@MappedSuperclass
public class Model implements Serializable {	
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;


	@Getter @Setter
	public Date created = new Date();
    
    public String getEntity() {
    	return this.getClass().getSimpleName().toLowerCase();
    }
 }
