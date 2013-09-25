import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * ReferenceMonitor maintains labels and validates subjects and objects and reads/writes
 */
public class ReferenceMonitor {
	private Map<SecuritySubject, SecurityLevel> subjectMap = new HashMap<SecuritySubject, SecurityLevel>();
	private Map<SecurityObject, SecurityLevel> objectMap= new HashMap<SecurityObject, SecurityLevel>();
	String newLine = System.getProperty("line.separator");
	public ReferenceMonitor() throws FileNotFoundException 
	{
	}
	ObjectManager om = new ObjectManager();
	/*
	 * ObjectManager class performs simple accesses (reads and writes objects by name)
	 */
	private class ObjectManager {
		public void objectRead (SecuritySubject s, int temp) {		
			s.setTEMP(temp);
			
		}
		public void objectWrite(SecurityObject o, int value) {
			o.setValue(value);
		}
	}
	
	public void createNewObject(String name, SecurityLevel sl) {
		//check for the existence of this object
		SecurityObject object = new SecurityObject(name);
		if (objectMap.containsKey(object)) {
			System.err.println("Error: object already exists");
			return;
		}
		
		//if object key does not yet exist, add it to mapping
		objectMap.put(object, sl);
	}
	
	public void createNewSubject(SecuritySubject subject, SecurityLevel sl) {
		//check for the existence of this subject
		if (subjectMap.containsKey(subject))
		{
			System.err.println("Error: subject already exists");
			return;
		}
		subjectMap.put(subject, sl);	
	}
	
	/*
	 * Validate if SecurityLevel of subject is >= than SecurityLevel of object
	 */
	public void executeRead(String s, String o, PrintWriter log) {
		if (CovertChannel.verbose)
			log.write("READ " + s.toUpperCase() + " " + o.toUpperCase() + newLine);

		int temp = 0;
		for (SecuritySubject sKey: subjectMap.keySet())
		{
			if (sKey.getName().equalsIgnoreCase(s))
			{
				for (SecurityObject oKey: objectMap.keySet())
				{
					if (oKey.getName().equalsIgnoreCase(o))
					{
						//both the subject and object exist in our mapping, now check security
						if (subjectMap.get(sKey).compareTo(objectMap.get(oKey)) >= 0)
						{
							//subject securitylevel > object securitylevel so execute
							temp = oKey.getValue();
							om.objectRead(sKey, temp);
							return;
						}
						else {
							//System.err.println("Access denied: security level does not match.");
							om.objectRead(sKey, temp);
							return;
						}
					}
				}
				//System.err.println("Not a valid object entry."); don't output anything to avoid covert channel
				return;
			}
		}
		//System.err.println("Not a valid subject entry.");
		return;
	}
	
	/*
	 * Validate if SecurityLevel of subject is <= SecurityLevel of object
	 */
	public void executeWrite(String s, String o, int value, PrintWriter log) {
		if (CovertChannel.verbose)
			log.write("WRITE " + s.toUpperCase() + " " + o.toUpperCase() + newLine);

		for (SecuritySubject sKey: subjectMap.keySet())
		{
			if (sKey.getName().equalsIgnoreCase(s))
			{
				for (SecurityObject oKey: objectMap.keySet())
				{
					if (oKey.getName().equalsIgnoreCase(o))
					{
						//both the subject and object exist in ousr mapping, now check security
						if (subjectMap.get(sKey).compareTo(objectMap.get(oKey)) <= 0)
						{
							//subject securitylevel < object securitylevel so execute
							//System.out.println(s + " writes value " + value + " to " + o);
							om.objectWrite(oKey, value);
							return;
						}
						else{
							//System.err.println("Access denied: security level does not match.");
							//System.out.println(s + " writes value " + value + " to " + o);
							return;
						}
					}
				}
				//System.err.println("Not a valid object entry.");
				return;
			}
		}
		//System.err.println("Not a valid subject entry.");
		return;
	}
	
	public void executeDestroy(String s, String o, PrintWriter log)
	{
		if (CovertChannel.verbose)
			log.write("DESTROY " + s.toUpperCase() + " " + o.toUpperCase() + newLine);
		
		for (SecuritySubject sKey: subjectMap.keySet())
		{
			if (sKey.getName().equalsIgnoreCase(s))
			{
				for (SecurityObject oKey: objectMap.keySet())
				{
					if (oKey.getName().equalsIgnoreCase(o))
					{
						//if subject security level is lower than object, then destroy access allowed
						if (subjectMap.get(sKey).compareTo(objectMap.get(oKey)) <= 0)
						{
							objectMap.remove(oKey);
						}
						else {}//don't destroy
					}
				}
			}
		}
	}
	
	//execute run from securitysubject class
	public void executeRun(String s, FileOutputStream out, PrintWriter log) throws IOException
	{
		if (CovertChannel.verbose)
			log.write("RUN " + s.toUpperCase() + newLine);
		
		for (SecuritySubject sKey: subjectMap.keySet())
		{
			if (sKey.getName().equalsIgnoreCase(s))
			{
				if (s.equalsIgnoreCase("hal"))
				{
					//Hal doesn't need to do any processing for Run		
				}
				else if (s.equalsIgnoreCase("lyle"))
				{
					//if Lyle is performing read access, then store covert channel information
					if (sKey.getName().equalsIgnoreCase("Lyle"))
					{
						//accumulate bit to signal byte and increment read count
						sKey.setSignal(sKey.getSignal() | (sKey.getTEMP() << (7-sKey.getReadCount())));
						sKey.setReadCount(sKey.getReadCount() + 1);
					}
					//if readCount is 8 already then you can output to file, and reset to 0
					if (sKey.getReadCount() >= 8)
					{
						//System.out.println("Lyle Runs and transfers signal: " + sKey.getSignal());
						out.write(sKey.getSignal());
						sKey.setReadCount(0);
						sKey.setSignal(0);
					}
				}
			}
		}
	}
	
	//execute create object if 
	public void executeCreateObject(String s, String o, PrintWriter log)
	{
		if (CovertChannel.verbose)
			log.write("CREATE " + s.toUpperCase() + " " + o.toUpperCase() + newLine);
		for (SecurityObject oKey: objectMap.keySet())
		{
			if (oKey.getName().equalsIgnoreCase(o))
			{
				//if object exists already, then do not create
				return;
			}
		}
		for (SecuritySubject sKey: subjectMap.keySet())
		{
			if (sKey.getName().equalsIgnoreCase(s))
			{
				//create a new object at same security level as subject
				createNewObject(o, subjectMap.get(sKey));
				return;
			}
		}
	}

	public void validateInstruction(InstructionObject instruction) throws IOException {
		if (instruction.getInstruction().equals("BAD")) {
			System.out.println("Bad Instruction");
		}
		else {
			if (instruction.getInstruction().equalsIgnoreCase("read")) {
				executeRead(instruction.getSubject(), instruction.getObject(), null);
			} 
			else if (instruction.getInstruction().equalsIgnoreCase("write")) {
				executeWrite(instruction.getSubject(), instruction.getObject(), instruction.getValue(), null);
			}
			//create object with same level as subject (if object at level already exists, noop)
			else if (instruction.getInstruction().equalsIgnoreCase("create")) {
				executeCreateObject(instruction.getSubject(), instruction.getObject(), null);
			}
			else if (instruction.getInstruction().equalsIgnoreCase("destroy")) {
				executeDestroy(instruction.getSubject(), instruction.getObject(), null);
			}
			else if (instruction.getInstruction().equalsIgnoreCase("run")) {
				executeRun(instruction.getSubject(), null, null);
			}
					
		}
		
	}
	
//	//iterates through all objects, then subjects, printing their values
//	public void printState() {
//		System.out.println("The current state is:");
//		for (SecurityObject oKey: objectMap.keySet())
//		{
//			System.out.println("\t" + oKey.getName() + " has value: " + oKey.getValue());
//		}
//		for (SecuritySubject sKey: subjectMap.keySet())
//		{
//			System.out.println("\t" + sKey.getName() + " has recently read: " + sKey.getTEMP());
//		}	
//		System.out.println();
//	}
}
