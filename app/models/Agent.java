package models;

import lombok.*;
import models_enums.AgentProduct;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="agents")
public class Agent implements Comparable<Agent>, Serializable {
	@Id
    @Getter @Setter
	public Long id;

    @Getter @Setter
	@Column(nullable=false)
	public boolean active;

	@Getter @Setter
	@Column(unique=true, nullable=false)
	public String email;

	@Getter @Setter
	@Column(unique=true, nullable=false)
	public String username;

	@Getter @Setter
	public String name;

	@Getter @Setter
	public String phone;

    @Getter @Setter
    @Column(nullable = false)
    public double balance;

    @Getter @Setter
    public String type;

	@Getter @Setter
	public String logoUrl;

	@Override
	public int compareTo(Agent o) {
		return this.name.compareToIgnoreCase(o.name);
	}

	public Long created;

    public Date created() {
        return new Date(created);
    }

	@ElementCollection
	@CollectionTable(name="agent_areas", joinColumns = @JoinColumn(name="agent_id"))
	@Column(name="area")
	public List<Long> areas = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="agent_axises", joinColumns = @JoinColumn(name="agent_id"))
	@Column(name="axis")
	public List<Long> axises = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="agent_states", joinColumns = @JoinColumn(name="agent_id"))
	@Column(name="state")
	public List<Long> states = new ArrayList<>();

	@ElementCollection(fetch= FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="agent_products", joinColumns=@JoinColumn(name="agent_id"))
	@Column(name="product")
	public Set<AgentProduct> products = new HashSet<>();

	//added by Onuche Akor Idoko

	@Getter @Setter
	@Lob
	public String about;

	@Getter @Setter
	public String logoName;

	@Getter @Setter
	@OneToOne
	public Subscription currentSubscription;

}