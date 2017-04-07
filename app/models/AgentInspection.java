package models;

import lombok.Getter;
import lombok.Setter;
import models_enums.NotificationStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by seyi on 12/9/16.
 */

@Entity
@Table(name="agent_inspections")
public class AgentInspection extends Model {

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="property_lead_id")
    public PropertyLead propertyLead;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="client_id")
    public AgentClient client;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

    @Getter
    @Setter
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date dateOfInspection;

    @PrePersist
    @PreUpdate
    public void init() {
        if(client == null && propertyLead == null) {
            throw new RuntimeException("No client chosen");
        }
    }


    public static class Info{
        public String name;
        public String phone;
        public String email;
        public String type;
        public Date inspectionDate;

        public Info(String name, String phone, String email, String type, Date inspectionDate) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.type = type;
            this.inspectionDate = inspectionDate;
        }
    }

    @Transient
    private Info info;

    public Info info() {
        if(info == null) {
            if (propertyLead != null) {
                info = new Info(propertyLead.userName, propertyLead.userPhone, propertyLead.userEmail, "ToLet Lead", new Date(propertyLead.willingDate));
            } else {
                info = new Info(client.name, client.phone, client.email, "Other Client", null);
            }
        }
        return info;
    }

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
}