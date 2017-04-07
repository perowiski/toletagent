package models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="agent_leads")
public class AgentLead implements Comparable<AgentLead> {

    @Getter @Setter
    @Id
    public Long id;

    @Getter
    @Setter
    @Column
    public String name;

    @Getter
    @Setter
    @Column
    public String email;

    @Getter
    @Setter
    @Column
    public String phone;

    @Getter
    @Setter
    @Column
    public String smsPhone;

    @Getter
    @Setter
    @Column(name="agent_id")
    public Long agentId;

    @Getter
    @Setter
    @Column(nullable=false)
    public double deducted;

    @Getter @Setter
    @Column(name="property_lead_id")
    public Long propertyLead;

    @Getter @Setter
    public Long created;

    public Date created() {
        return new Date(created);
    }

	@Override
	public int compareTo(AgentLead o) {
		return o.id.compareTo(this.id);
	}
}
