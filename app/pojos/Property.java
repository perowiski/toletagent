package pojos;

import controllers.Utility;
import lombok.Getter;
import lombok.Setter;
import models.Agent;
import services.DBService;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Property implements Serializable {
	@Id
    @Getter
    @Setter
	public Long id;

    @Getter
    @Setter
    public String mode;

    @Getter
    @Setter
	public String link;

    @Getter
    @Setter
    public String imageLink;

    @Getter
    @Setter
    public List<String> images;

    @Getter
    @Setter
    public String description;

    @Getter
    @Setter
    public String caption;

    @Getter
    @Setter
    public Price price;

    @Getter
    @Setter
    public String title;

    @Getter
    @Setter
    public String summary;

    @Getter
    @Setter
    public String pid;

    @Getter
    @Setter
    public Long agentId;

    @Getter
    @Setter
    public String street;

    @Getter
    @Setter
    public String subArea;

    @Getter
    @Setter
    public Long areaId;

    @Getter
    @Setter
    public String area;

    @Getter
    @Setter
    public Long axisId;

    @Getter
    @Setter
    public String axis;

    @Getter
    @Setter
    public Long stateId;

    @Getter
    @Setter
    public String state;

    @Getter
    @Setter
    public Integer beds;

    @Getter
    @Setter
    public Integer baths;

    @Getter
    @Setter
    public Integer toilets;

    @Getter
    @Setter
    public String brief;

    @Getter
    @Setter
    public boolean managed;

    @Getter
    @Setter
    public boolean disposed;

    @Getter
    @Setter
    public String type;

    @Getter
    @Setter
    public Long created;

    public List<String> features;

    public String address(){
        return (Utility.isNotEmpty(subArea) ? subArea + " ": "")
                + (area != null ? area + " ": "")
                + (axis != null ? axis + " ": "")
                + state;
    }


    public Agent agent;

    public Agent agent() {
        if(agent == null) {
            agent = DBService.Q.findOne(Agent.class, agentId);
        }
        return agent;
    }

    public Date created() {
        return new Date(created);
    }
}
