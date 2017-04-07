package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Created by seyi on 12/9/16.
 */

@Entity
@Table(name="agent_connect_replies")
public class AgentConnectReply extends Model {
    @Constraints.Required
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="connect_id")
    public AgentConnect connect;

    @Getter
    @Setter
    @Lob
    @Column
    public String message;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="agent_id")
    public Agent agent;

    @Getter
    @Setter
    @Column(nullable=false)
    public boolean pulled;
}