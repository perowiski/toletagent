package models;

import controllers.Misc;
import lombok.Getter;
import lombok.Setter;
import pojos.Property;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="property_leads")
public class PropertyLead implements Comparable<PropertyLead> {

	@Getter @Setter
	@Id
	public Long id;

	@Getter @Setter
	public String pid;

    @Getter @Setter
    public String requestTerm;

    @Getter @Setter
    public String propertyType;

    @Getter @Setter
    public String propertyUsage;

	@Getter @Setter
	public String agentId;

    @Getter @Setter
    public String agentName;

    @Getter @Setter
    public String agentPhone;

	@Getter @Setter
	public String userType;

	@Getter @Setter
	public String userName;

	@Getter @Setter
	public String userEmail;

	@Getter @Setter
	public String userCountry;

	@Getter @Setter
	public String userPhone;

    @Getter @Setter
    public Double maxBudget;

	@Getter @Setter
    public Long willingDate ;

	@Getter @Setter
	public String smsStatus;

	@Getter @Setter
	public String smsPhone;

    @Getter @Setter
	public String source;

    @Getter @Setter
    public Long areaId;

    @Getter @Setter
    public Long axisId;

    @Getter @Setter
    public Long stateId;

    @Getter @Setter
	public Long created;

    public Date created() {
        return new Date(created);
    }

	public String name() {
		String[] arr = this.userName.split(" ");
		if(arr.length >= 2) {
			if (arr[0].equalsIgnoreCase("mr") || arr[0].equalsIgnoreCase("mrs")) {
				return arr[1];
			} else {
				return arr[0];
			}
		}
		return this.userName;
	}

	@Transient
	private Property property;

    public Property getProperty() {
        if(property == null) {
            property = Misc.findProperty(pid);
        }
        return property;
    }

    @Override
	public int compareTo(PropertyLead o) {
		return o.id.compareTo(this.id);
	}
}
