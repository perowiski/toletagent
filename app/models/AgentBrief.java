package models;

import formatters.AmtFormat;
import formatters.ModelFormat;
import lombok.Getter;
import lombok.Setter;
import models_enums.PropertyMode;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name="agent_briefs")
public class AgentBrief extends Model implements Comparable<AgentBrief> {

    @Constraints.Required(message = "This field is required")
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    public PropertyMode mode;

    @Getter
    @Setter
    @AmtFormat
    @Column
    public Double price;

    @Getter
    @Setter
    @Column
    public Integer beds;

    @Getter
    @Setter
    @Lob
    @Column
    public String comment;

    @Constraints.Required(message = "This field is required")
    @Getter
    @Setter
    @ModelFormat(EType.class)
    @ManyToOne(optional=false)
    @JoinColumn(name="type_id")
    public EType type;

    @Constraints.Required(message = "This field is required")
    @Getter
    @Setter
    @ModelFormat(EAxis.class)
    @ManyToOne
    @JoinColumn(name="axis_id")
    public EAxis axis;

    @Constraints.Required(message = "This field is required")
    @Getter
    @Setter
    @ModelFormat(EState.class)
    @ManyToOne
    @JoinColumn(name="state_id")
    public EState state;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

    public String address() {
        return ((axis != null) ? axis.name + ", " : "")
                + ((state != null) ? state.name : "");
    }

	@Override
	public int compareTo(AgentBrief o) {
		return o.id.compareTo(this.id);
	}
}
