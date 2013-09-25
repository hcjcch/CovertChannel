
public class SecurityObject {
	//instance vars
	private String name;
	private int value;
	
	//Constructors
	public SecurityObject() {
		value = 0;
	}
	
	public SecurityObject(String name) {
		this();
		this.name = name;		
	}
	
	public SecurityObject(String name, int value) {
		this();
		this.name = name;
		this.value = value;
	}
	
	//accessor methods
	public String getName() {
		return name;
	}
	public int getValue() {
		return value;
	}
	
	//mutator methods
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public boolean equals (SecurityObject o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof SecurityObject)) return false;
		if (o.getName().equals(this.getName()))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityObject other = (SecurityObject) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}


}
