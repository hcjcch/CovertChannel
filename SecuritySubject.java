
public class SecuritySubject {
	//instance vars
	private String name;
	private int TEMP;  //variable contains most recently read value
	private int signal;   //covert channel
	private int readCount;  //when number of reads by an object hits 8, output to file and reset to 0
	
	public SecuritySubject () {
		TEMP = 0;
		readCount = 0;
		signal = 0;
	}
	
	public SecuritySubject (String name) {
		this();
		this.name = name;
		
	}
	
	//accessor methods
	public String getName()
	{
		return name;
	}
	public int getTEMP ()
	{
		return TEMP;
	}
	public int getSignal()
	{
		return signal;
	}
	public int getReadCount()
	{
		return readCount;
	}
	
	//mutator methods
	public void setTEMP (int temp) 
	{
		TEMP = temp;
	}
	public void setSignal(int signal)
	{
		this.signal = signal;
	}
	public void setReadCount(int readCount)
	{
		this.readCount = readCount;
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
		SecuritySubject other = (SecuritySubject) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}
	
}
