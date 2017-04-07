package models;

import formatters.AmtFormat;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name="agent_clients")
public class AgentClient extends Model implements Comparable<AgentClient> {

	@Getter
    @Setter
	@Constraints.Required(message = "Please enter your full name")
	@Column
	public String name;

	@Getter
    @Setter
	@Constraints.Required(message = "Email address is required")
	@Constraints.Email(message = "Email not in proper format")
	@Column
	public String email;

	@Getter
    @Setter
	@Constraints.Required(message = "Phone number is required")
	@Column
	public String phone;

	@Constraints.Min(value=50000, message = "Max budget must be greater than N50,000")
    @Getter
    @Setter
    @AmtFormat
    @Column
    public Double maxBudget;

    @Getter
    @Setter
    @AmtFormat
    @Column
    public Double minBudget;

    @Getter
    @Setter
    @Column
    public String source;

	@Getter
    @Setter
	@Column
	public String pid;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

	public String name() {
		String[] arr = this.name.split(" ");
		if(arr.length >= 2) {
			if (arr[0].equalsIgnoreCase("mr") || arr[0].equalsIgnoreCase("mrs")) {
				return arr[1];
			} else {
				return arr[0];
			}
		}
		return this.name;
	}

	@Override
	public int compareTo(AgentClient o) {
		return o.id.compareTo(this.id);
	}
}
