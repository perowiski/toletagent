package models;

import controllers.Auth;
import controllers.Utility;
import lombok.Getter;
import lombok.Setter;
import models_enums.AgentConnectType;
import play.data.validation.Constraints;
import services.DBFilter;
import services.DBService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seyi on 12/9/16.
 */

@Entity
@Table(name="agent_connects")
public class AgentConnect extends Model {
    @Getter
    @Setter
    @Constraints.Required
    @Enumerated(EnumType.STRING)
    @Column
    public AgentConnectType type;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name="request_id")
    public AgentRequest request;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="brief_id")
    public AgentBrief brief;

    @Getter
    @Setter
    public String pid;

    @Getter
    @Setter
    @Lob
    @Column
    public String message;

    @Constraints.Required
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="from_agent_id")
    public Agent fromAgent;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="to_agent_id")
    public Agent toAgent;

    @ElementCollection
    @CollectionTable(name="agent_connect_to_axises", joinColumns = @JoinColumn(name="agent_connect_id"))
    @Column(name="axis")
    public List<Long> axises = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="agent_connect_to_states", joinColumns = @JoinColumn(name="agent_connect_id"))
    @Column(name="state")
    public List<Long> states = new ArrayList<>();

    @Getter
    @Setter
    @Column(nullable=false)
    public boolean success;

    @Getter
    @Setter
    @Column(nullable=false)
    public boolean seen;


    @PrePersist
    @PreUpdate
    public void init() {
        if(brief == null && request == null && Utility.isNotEmpty(pid)) {
            throw new RuntimeException("No detail chosen");
        }
    }


    public boolean responded() {
        return DBService.Q.find(AgentConnectReply.class,
                DBFilter.get().field("agent.id").eq(Auth.getAgent().id).field("connect.id").eq(this.id)).size() > 0;
    }
}