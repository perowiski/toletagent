package models;

import formatters.AmtFormat;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name="agent_credits")
public class AgentCredit extends Model implements Comparable<AgentCredit> {
    @Constraints.Required(message = "The amount is required")
    @Getter
    @Setter
    @AmtFormat
    @Column
    public Double amount;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

    @Transient
    public String search;

	@Override
	public int compareTo(AgentCredit o) {
		return o.id.compareTo(this.id);
	}
}
