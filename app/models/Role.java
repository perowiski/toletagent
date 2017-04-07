package models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author seyi
 */

@Entity
@Table(name="roles")
public class Role extends Model {

    @Getter @Setter
	@Column(nullable=false)
	public String role;

    @Getter @Setter
	@ElementCollection(fetch=FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="role_permissions", joinColumns=@JoinColumn(name="role_id"))
    @Column(name="permission")
	public List<Permission> permissions = new ArrayList<>();

	public Role() {}

	public Role(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return this.role;
	}
}
