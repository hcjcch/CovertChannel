/*Name: Steven Lee
 * UTEID: SCL346
 * Class: CS361
 * Assignment: HW2
 * Purpose: implement a covert channel within the constraints of the BLP security model
 */

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CovertChannel {
	
	
	static boolean verbose = false;
	public static void main (String [] args) throws IOException {
		final ReferenceMonitor rm = new ReferenceMonitor();
		CovertChannel sys = new CovertChannel();
		
		FileOutputStream out = null; 
		PrintWriter log = new PrintWriter("log"); //log if v is set
        ByteArrayInputStream in = null;
		
		//HIGH dominates LOW as defined by Bell LaPadula Model
		SecurityLevel low = SecurityLevel.LOW;
		SecurityLevel high = SecurityLevel.HIGH;
		
		//add two subjects, one high and one low.
		sys.createSubject("Lyle", low, rm);
		sys.createSubject("Hal", high, rm);
		
		//read in input file and convert to byte array
		byte[] data = null; //byte array
		String filename = null;
		long start = System.currentTimeMillis(); // begin timer
		try {
			//check if verbose argument is set, if it is then filename is second argument
			if (args[0].equals("v"))
			{
				verbose = true;
				filename = args[1];
				Path path = Paths.get(filename);
				data = Files.readAllBytes(path);
			}
			else
			{
				filename = args[0];
				Path path = Paths.get(filename);
				data = Files.readAllBytes(path);
			}
			out = new FileOutputStream(filename + ".out");
            for (int i = 0; i < data.length; i++) // loop through each byte in byte array
            {
            	for (int j = 0; j < 8; j++) // loop through each bit in specific byte
            	{
                	rm.executeRun("Hal", out, log); //HAL runs regardless of bit value
            		int mask = (1 << (7-j));
            		if ((mask & data[i]) == 0) //bit is 0 at this spot
            		{
            			rm.executeCreateObject("Hal", "Obj", log);
            		}
            		else
            		{
            			//no further action if bit is 1
            		}
            		//Lyle executes a set of actions to retrieve that bit and store it
            		rm.executeCreateObject("Lyle", "Obj", log);
            		rm.executeWrite("Lyle", "Obj", 1, log);
            		rm.executeRead("Lyle", "Obj", log);
            		rm.executeDestroy("Lyle", "Obj", log);
            		rm.executeRun("Lyle", out, log);
            	}
                
            }
		}
		
		catch (IOException e) {
			System.err.print("The file was not found.");
		}
		finally {
			long end = System.currentTimeMillis();
			System.out.println("Execution Time = " + (end - start) + "ms");
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
                log.close();
            }
		}
			
	}

	
	/*
	 * Instantiate subject and place in reference monitor
	 */
	private void createSubject(String name, SecurityLevel sl, ReferenceMonitor rm) {
		SecuritySubject subject = new SecuritySubject(name);
		rm.createNewSubject(subject, sl);
	}
	
//	public void printState(String filename, byte b) throws IOException {
//		
//		out.write(b);
//		out.close();
//	}
	
	
	/* pre: none
	 * post: output values of LObj, HObj, and TEMP variable for Lyle and Hal
	 * debugging method that prints out current values from the state
	 */
//	private void printState() {
//		//implement iteration over maps eventually to print out all subjects/objects
//		rm.printState();
//			
//	}
	
	/*
	 * Pass in string and check if it is an integer value
	 */
	private boolean isInteger (String s) {
		int size = s.length();
		for (int i = 0; i < size; i++)
		{
			if (!Character.isDigit(s.charAt(i)))
			{
				return false;
			}
		}
		return size > 0;
	}
}
