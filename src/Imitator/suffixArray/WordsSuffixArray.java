package Imitator.suffixArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import Imitator.common.FileUtils;

public class WordsSuffixArray {
    private int _corpusWordsCount;
    private int[] _suffixes;
    private WordList _corpusWords;
    private int counter1 = 0;
    private int counter2 = 0;
    private int counter3 = 0;
    private int counter4 = 0;
    private Comparator<Integer> _suffixComparator;
    
    public WordsSuffixArray(WordList list) {
		_corpusWords = list;
		_corpusWordsCount = list.size();
		
		System.out.println("Number of words in the corpus is: "+_corpusWordsCount);
		_suffixComparator = new SuffixesComparator(_corpusWords);
    }

    public void writeFileOfSuffixes(String outputFilePath) {
    	int MAX_SUFFIXES_PER_FILE = 1000000;
    	
    	File tempDir = new File("./temp");
    	if (!tempDir.isDirectory())
    		tempDir.mkdir();
 
    	try {
	    	for(int numFiles=1, i=0; i<_corpusWordsCount; numFiles++) {
	    		Integer[] suffixObjects = new Integer[(_corpusWordsCount-i) < MAX_SUFFIXES_PER_FILE? (_corpusWordsCount-i): MAX_SUFFIXES_PER_FILE]; 
	    		for (int j=0; j<suffixObjects.length; ++j, ++i)
	    			suffixObjects[j] = i; 

				// init the suffix array
				//for (; (i % MAX_SUFFIXES_PER_FILE != 0) && (i < _corpusWordsCount); i++) {
				//	suffixObjects[j++] = i; 
				//}

				// This sort actually creates the suffix array, using the comparator:
				Arrays.sort(suffixObjects, _suffixComparator);
				File tempFile = new File("./temp/suff"+numFiles);
				tempFile.createNewFile();
				int lengthInFile = (i<_corpusWordsCount? 0: _corpusWordsCount);
				writeSuffixesToFile(lengthInFile, suffixObjects, tempFile.getAbsolutePath());
	    	}
	    	
	    	//merge the files to one big file
    		File[] allFiles = tempDir.listFiles();
    		String mergedFile = mergeAllFiles(allFiles,tempDir);
    		copyMerged(mergedFile,outputFilePath);
    		
    		//delete the directory
    		allFiles = tempDir.listFiles();
    		for(int i = 0; i < allFiles.length ; i++)
    			allFiles[i].delete();
    		tempDir.delete();
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void copyMerged(String mergedFile, String file)
    {
    	//for reading the file
		FileInputStream from;
		try 
		{
			from = new FileInputStream(mergedFile);
		
		
			FileOutputStream to = new FileOutputStream(file);
			
			byte[] buffer = new byte[10000];
			int sum=0;
			//@SuppressWarnings("unused")
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1)
			{
				sum+=bytesRead;
				// write to file
				to.write(buffer, 0, bytesRead);
			}
		
			if (from != null)
		    	from.close();	
			
			if (to != null)
		    	to.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String mergeAllFiles(File[] allFiles, File tempDir) throws IOException 
    {
		int numberOfFiles = allFiles.length;
		
    	if (numberOfFiles < 2)
    		return allFiles[0].getAbsolutePath();
    	
    	File tempFile = null;// = new File("./temp/merged"+1);

    	boolean addLenght = false;
		if (2 == numberOfFiles)
			addLenght = true;
		
		int curNumOfFiles = numberOfFiles;
		int counter = 1;
		
		while (numberOfFiles > 2) {
			int i = 0;
			for(; i < numberOfFiles - 1 ; i = i+2) {
	    		tempFile = new File("./temp/merged"+(counter++));
	    		if (tempFile.isFile())
	    			tempFile.delete();
	    		tempFile.createNewFile();
	    		
	    		merge2Files(allFiles[i].getPath(), allFiles[i+1].getPath(), tempFile.getPath(), addLenght);
	    		allFiles[i].delete();
	    		allFiles[i+1].delete();
	    		curNumOfFiles -= 2;
	    	}
			if (curNumOfFiles == 1)
			{
				String oldFile = tempFile.getPath();
				File save = tempFile;
				
				File[] temp = tempDir.listFiles();
				int size = temp.length;
				
				tempFile = new File("./temp/merged"+(counter++));
	    		if (tempFile.isFile())
	    			tempFile.delete();
	    		tempFile.createNewFile();
	    		if ( 2 == size)
	    			addLenght = true;
				merge2Files( oldFile, allFiles[numberOfFiles-1].getPath(), tempFile.getPath(), addLenght);
				allFiles[numberOfFiles-1].delete();
				save.delete();
			}
			
			allFiles = tempDir.listFiles();
			numberOfFiles = allFiles.length;
			curNumOfFiles = numberOfFiles;
		}
		
		if ( 2 == numberOfFiles)
		{
			addLenght = true;
			tempFile = new File("./temp/merged"+(counter++));
			if (tempFile.isFile())
				tempFile.delete();
			tempFile.createNewFile();
			
			merge2Files( allFiles[0].getPath(), allFiles[1].getPath() , tempFile.getPath(), addLenght);
		}
		/*
    	for(int i = 2; i < allFiles.length ; i++)
    	{
    		String oldFile = tempFile.getPath();
    		
    		tempFile = new File("./temp/merged"+(i % 5));
    		if (tempFile.isFile())
    			tempFile.delete();
    		tempFile.createNewFile();
    		
    		if ( (i+1) == allFiles.length)
    			addLenght = true;
    		merge2Files(oldFile, allFiles[i].getPath(), tempFile.getPath(), addLenght);
    	}*/
    	
    	return tempFile.getPath();
	}

    private void merge2Files(String file1, String file2, String newFile, boolean addLenght) {
    	try {
	    	// open the first file
			FileInputStream fstreamFirst = new FileInputStream(file1);
			
			// get the object of DataInputStream
			DataInputStream in1 = new DataInputStream(fstreamFirst);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			
			// open the second file
			FileInputStream fstreamSecond = new FileInputStream(file2);
			
			// get the object of DataInputStream
			DataInputStream in2 = new DataInputStream(fstreamSecond);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
			
			//open the output file
			BufferedWriter output = new BufferedWriter(new FileWriter(newFile));
			
			String[] line1, line2;
			String st1 = br1.readLine();
			String st2 = br2.readLine();
			line1 = st1.split(" ");
			line2 = st2.split(" ");
			
			int i = 0, j = 0;
			int indexInRow = 0;
			
			if (addLenght)
				output.write(_corpusWordsCount + "\n");
			
			while ( ((st1) != null) || ((st2) != null) )
			{
				if ( (st1 != null) && (i >= line1.length))
				{
					i = 0;
					st1 = br1.readLine();
					if (st1 != null)
						line1 = st1.split(" ");
				}
				if  ( (st2 != null) && (j >= line2.length))
				{
					j = 0;
					st2 = br2.readLine();
					if (st2 != null)
						line2 = st2.split(" ");
				}
				
				if ( (st1 == null) && (st2 == null) )
				{
					in1.close();
					in2.close();
					output.close();
					return;
				}
				
				if (st1 == null)
				{
					for(; j < line2.length ; j++)
			    	{
						output.write(line2[j] + " ");
		    	    	counter1++;

						if(++indexInRow%1000 == 0)
			    		{
			    		    output.newLine();
			    		    indexInRow=0;
			    		}
			    	}
				}
				else if (st2 == null)
				{
					for(; i < line1.length ; i++)
			    	{
						output.write(line1[i] + " ");
		    	    	counter2++;

						if(++indexInRow%1000 == 0)
			    		{
			    		    output.newLine();
			    		    indexInRow=0;
			    		}
			    	}
				}
				else if (_suffixComparator.compare(new Integer(line1[i]), new Integer(line2[j])) <= 0)
				{
					output.write(line1[i] + " ");
	    	    	counter3++;

					i++;
					if(++indexInRow%1000 == 0)
		    		{
		    		    output.newLine();
		    		    indexInRow=0;
		    		}
				}
				else
				{
					output.write(line2[j] + " ");
	    	    	counter4++;

					j++;
					if(++indexInRow%1000 == 0)
		    		{
		    		    output.newLine();
		    		    indexInRow=0;
		    		}
				}
				
				
				
			}
			
			in1.close();
			in2.close();
			output.close();
		
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
	private void writeSuffixesToFile(int length, Integer[] suffixes, String file)
    {
    	try 
    	{

    	    FileWriter writer = new FileWriter(file);

    	    if (length > 0)
    	    	writer.write(length + "\n");

    	    int indexInRow = 0;

    	    for (int i = 0; i < suffixes.length; i++) 
    	    {
	    		writer.write(suffixes[i] + " ");
	
	    		if(++indexInRow%1000 == 0)
	    		{
	    		    writer.write("\n");
	    		    indexInRow=0;
	    		}
    	    }
    	    writer.close();

    	} catch (IOException e) {
    	    e.printStackTrace();
    	}

    }
	
    /**
     * Constructor for existing suffix array
     * @param list
     * @param suf
     */
    public WordsSuffixArray(WordList list, int[] suf)
    {
		_corpusWords = list;
		_suffixes = suf;
		_corpusWordsCount = list.size();
    }

    public void print()
    {
		Collection<String> recordsForFile = new ArrayList<String>();
		
	//	    for (int i = 0; i < _len; i++) {
	//		if (_suffixes[i] + 3 < _len)
	//		    recordsForFile.add(i + ":" + _suffixes[i] + "," + _corpusWords.get(_suffixes[i]) + " "
	//			    + _corpusWords.get(_suffixes[i] + 1) + " " + _corpusWords.get(_suffixes[i] + 2) + " "
	//			    + _corpusWords.get(_suffixes[i] + 3));
	//	    }
	
		    for (int i = 0; i < _corpusWordsCount; i++)
		    {
			if (_suffixes[i] + 3 < _corpusWordsCount)
			    recordsForFile.add(i + ":" + (_corpusWordsCount - 1 - _suffixes[i]) + "," + _corpusWords.get(_suffixes[i]) + " "
				    + _corpusWords.get(_suffixes[i] + 1) + " " + _corpusWords.get(_suffixes[i] + 2) + " "
				    + _corpusWords.get(_suffixes[i] + 3));
		    }

	
		    FileUtils.writeFile(recordsForFile, "D:/project/stats-june/tries/test.txt");
    }
    
    public int getLength()
    {
        return _corpusWordsCount;
    }

    public WordList getCorpusWords() {
        return _corpusWords;
    }

    public int[] getSuffixes() {
        return _suffixes;
    }

}
