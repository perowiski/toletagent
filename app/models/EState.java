package models;

import javax.persistence.*;

@Entity
@Table(name="states")
public class EState extends EModel implements Comparable<EState>{

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(EState o) {
		return this.id.compareTo(o.id);
	}
}