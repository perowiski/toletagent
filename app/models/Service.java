package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Created by Onuche Idoko on 2/23/17.
 */
@Entity
@Table(name="services")
public class Service extends Model {

    @Constraints.Required(message = "Service name is required!")
    @Getter
    @Setter
    public String name;

    @Constraints.Required(message = "Say something about this service!")
    @Getter @Setter
    public String description;

    @Getter @Setter
    @ManyToOne
    public Agent agent;

}
