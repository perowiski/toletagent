package models;

import javax.persistence.*;

@Entity
@Table(name="types")
public class EType extends EModel implements Comparable<EType>{
    @Override
    public String toString() {
        return this.name;
    }

	@Override
	public int compareTo(EType o) {
		return this.name.compareToIgnoreCase(o.name);
	}

}