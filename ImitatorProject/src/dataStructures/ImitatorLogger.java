package dataStructures;

import java.text.*;
import java.util.*;

public class ImitatorLogger {
	
	protected DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	
	/**
	 * Set to "true" while debugging, to print every sentence to System.out while inserting it to the db.
	 */
	public boolean printToOut = true;
	
	private ArrayList<ArrayList<String>> local_db = new ArrayList<ArrayList<String>>();
	
	private ImitatorLogger()
	{
	}
	
	private static class Holder
	{
		private static final ImitatorLogger sing = new ImitatorLogger();
	}
	
	public static ImitatorLogger getInstance()
	{
		return Holder.sing;
	}
	
	public synchronized void clear()
	{
		int len = local_db.size();
		for (int i = 0; i < len; i++) 
		{
			local_db.get(0).clear();	
		}
		local_db.clear();
		
		System.out.println("Size of local DB in Logger: "+local_db.size());
	}
	
	public synchronized boolean removeLast()
	{
		if (printToOut) System.out.println("removeLast");
		if (local_db.size() > 0)
		{
			local_db.get(local_db.size()-1).clear();
			local_db.remove(local_db.size()-1);
			return true;
		}
		return false;
	}
	
	public synchronized void addToLastSentence(String str)
	{
		if (printToOut) System.out.println("   "+str);
		if (local_db.size() > 0)
			local_db.get(local_db.size()-1).add(dateFormat.format(new Date())+": "+str);
	}
	
	public synchronized void addToPreviousSentence(String str)
	{
		if (printToOut) System.out.println("addToPreviousSentence "+str);
		if (local_db.size() > 1)
		{
			local_db.get(local_db.size()-2).add(str);
		}
	}
	
	public synchronized void createNewSentence()
	{
		if (printToOut) System.out.println("createNewSentence");
		local_db.add(new ArrayList<String>());
	}
	
	public synchronized String getLogForLastSentence() {
		if (local_db.isEmpty()) 
			return null;
		List<String> lastSentenceLog = local_db.get(local_db.size()-1);
		int sizeOfSent = lastSentenceLog.size();
		StringBuffer logBuffer = new StringBuffer();
		for (int j = 0; j < sizeOfSent; j++) {
			logBuffer.append(lastSentenceLog.get(j)).append("\n");
		}
		return logBuffer.toString();
	}
	
	public void printAll()
	{
		int numSent = local_db.size();
		for (int i = 0; i < numSent; i++) 
		{
			System.out.println("\n************** Page number "+(i+1)+" **************");
			int sizeOfSent = local_db.get(i).size();
			for (int j = 0; j < sizeOfSent; j++) 
			{
				System.out.println(local_db.get(i).get(j));
			}
			System.out.println("************** Page end **************\n");
		}
	}

}
