package models;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author seyi
 */

@Entity
@Table(name="admins")
public class Admin extends Model {
    @Constraints.Required(message = "This field is required")
    @Getter @Setter
    public String name;

    @Constraints.Required(message = "This field is required")
    @Getter @Setter
	@Column(unique=true, nullable=false)
    public String email;

    @Getter @Setter
	@Column(nullable=false)
    public String password;

    @Getter @Setter
    @Column(nullable=false)
    public boolean active;

    @Getter @Setter
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="admin_roles",
            joinColumns=@JoinColumn(name="admin_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    public Set<Role> roles = new HashSet<>();

    @Override
	public String toString() {
		return this.name;
	}

    public Set<Permission> permissions() {
        Set<Permission> perms = new HashSet<>();
        roles.forEach(role -> {
            role.getPermissions().forEach(perm -> {
                perms.add(perm);
            });
        });
        return perms;
    }
}
