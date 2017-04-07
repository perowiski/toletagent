package search;

import controllers.StaticAssets;
import controllers.Utility;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


public class AgentIndex implements Serializable, Comparable<AgentIndex> {
	private static final long serialVersionUID = -42143673419833925L;
	
	@Getter @Setter public Long id;

	@Getter @Setter public String username;
	@Getter @Setter public String name;
	@Getter @Setter public String company;
	@Getter @Setter public String phone;
	@Getter @Setter public Date registeredOn;

    public String name() {
        return Utility.isNotEmpty(company) ? company : name;
    }

	@Override
	public int compareTo(@NotNull AgentIndex o) {
		return id.compareTo(o.id);
	}
}
