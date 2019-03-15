/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	// Create new file
	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try // open file for reading and writing
		{
			file = new RandomAccessFile(fileName, "rw");

		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		} // end catch

		finally {
			//Didnt need this code here
			closeFile();
		} 
	} 

	// Open file for adding or changing records
	public void openWriteFile(String fileName) {
		try // open file
		{
			output = new RandomAccessFile(fileName, "rw");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		} // end catch
	} // end method openFile

	// Close file for adding or changing records
	//Can now use this method for read and write files
	public void closeFile() {
		try // close file and exit
		{
			if (output != null)
				output.close();
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		} 
	} 

	// Add records to file
	public long addRecords(Employee employeeToAdd) 
	{
		//removed redundant local variable here
		
		long currentRecordStart = 0;

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try // output values to file
		{
			record = RandomAccessEmployeeRecord.getRandomAccessEmployeeRecord(employeeToAdd);
			
			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length();
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;// Return
																	// position
																	// where
																	// object
																	// starts in
																	// the file
	}
	
	
	// Change details for existing object
	public void changeRecords(Employee newDetails, long byteToStart) 
	{
		//removed redundant local variable
		
		RandomAccessEmployeeRecord record;
		try
		{
			record = new RandomAccessEmployeeRecord(newDetails.getEmployeeId(), newDetails.getPps(),
					newDetails.getSurname(), newDetails.getFirstName(), newDetails.getGender(),
					newDetails.getDepartment(), newDetails.getSalary(), newDetails.getFullTime());

			output.seek(byteToStart);
			record.write(output);
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 
	}

	// Delete existing object
	public void deleteRecords(long byteToStart) {
		
		//removed redundant local variable here

		RandomAccessEmployeeRecord record;
		try 
		{
			record = new RandomAccessEmployeeRecord();
			output.seek(byteToStart);
			record.write(output);
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 
	}

	// Open file for reading
	public void openReadFile(String fileName) {
		try // open file
		{
			input = new RandomAccessFile(fileName, "r");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		} // end catch
	} // end method openFile

	
	

	// Get position of first record in file
	public long getFirst() {
		long byteToStart = 0;

		try {// try to get file
			input.length();
		} 
		catch (IOException e) {
			//fixed catch to display error message
			System.out.println(e.getMessage());
		}
		
		return byteToStart;
	}

	// Get position of last record in file
	public long getLast() {
		long byteToStart = 0;

		try {// try to get position of last record
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}
		catch (IOException e) {
			//fixed catch to display error message
			System.out.println(e.getMessage());
		}

		return byteToStart;
	}

	// Get position of next record in file
	public long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if next position is end of file go to start of file, else get next position
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		} 
		catch (NumberFormatException | IOException e) {
			//fixed catch to display error message
			//and joined catch exceptions to shorten
			System.out.println(e.getMessage());
		} 
		return byteToStart;
	}

	// Get position of previous record in file
	public long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException | IOException e) {
		
			//fixed catch to display error message
			//and put the catch excpeptions together to shorten
			System.out.println(e.getMessage());
		} 
		
		return byteToStart;
	}

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) 
	{
		//removed redundant initializer
		Employee thisEmp;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read file and get record
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		} 
		catch (IOException e) {
			//fixed catch to display error message
			System.out.println(e.getMessage());
		}
		
		thisEmp = record;

		return thisEmp;
	}

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) 
	{
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		//removed redundant local variable here
		long currentByte = 0;

		try {
			
			while (currentByte != input.length() && !ppsExist) {
				
				if (currentByte != currentByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);// Get record from file
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}
				}
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		} 
		catch (IOException e) {
			//fixed catch to display error message
			System.out.println(e.getMessage());
		}

		return ppsExist;
	}// end isPpsExist

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read from file and look for ID
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);// Get record from file
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			//fixed catch to display error message
			System.out.println(e.getMessage());
		}

		return someoneToDisplay;
	}// end isSomeoneToDisplay
}// end class RandomFile
