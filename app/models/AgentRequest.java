package models;

import formatters.AmtFormat;
import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;
import models_enums.PropertyMode;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="agent_requests")
public class AgentRequest extends Model implements Comparable<AgentRequest> {

    @Constraints.Required(message = "Please choose a category")
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    public PropertyMode mode;

    @Getter
    @Setter
    @Column
    public Integer beds;

    @Getter
    @Setter
    @AmtFormat
    @Column
    public Double maxBudget;

    @Getter
    @Setter
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="agent_request_axises",
            joinColumns=@JoinColumn(name="request_id"),
            inverseJoinColumns=@JoinColumn(name="axis_id"))
    public Set<EAxis> axises = new HashSet<>();

    @Constraints.Required(message = "Please choose preferred state")
    @Getter
    @Setter
    @ModelFormat(EState.class)
    @ManyToOne
    @JoinColumn(name="state_id")
    public EState state;

    @Getter
    @Setter
    @Lob
    @Column
    public String comment;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

	@Override
	public int compareTo(AgentRequest o) {
		return o.id.compareTo(this.id);
	}
}
