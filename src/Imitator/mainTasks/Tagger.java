package Imitator.mainTasks;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import Imitator.common.FileUtils;

public class Tagger {

	// The valid characters. Is been used in the blogs' filter
	private final static String validChars = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~?!@#$%^*()_-+=|\\/,.<>:;&{}[]";
	
	public static void main(String[] args) {
		if (args.length < 4){
			System.out.println(" Usage: Tagger <Corpus files path> <Filtered corpus files path> <Tagged corpus path> <Tagger directory>");
			System.exit(0);
		}

		File corpusDir = new File(args[0]);
		File filteredCorpusDir = new File(args[1]);
		File taggedCorpusDir = new File(args[2]);

		if(!taggedCorpusDir.exists())
			taggedCorpusDir.mkdir();

		if(!filteredCorpusDir.exists())
			filteredCorpusDir.mkdir();
		
		if (!corpusDir.isDirectory() || !taggedCorpusDir.isDirectory() || !filteredCorpusDir.isDirectory()) {
			System.err.println(" ## parameters are not directories!!");
			return;
		}

		tagCorpus(corpusDir, filteredCorpusDir, taggedCorpusDir, args[3]);
		
	}

	/**
	 * 
	 * @param corpusDir
	 * @param filteredCorpusDir
	 * @param taggedCorpusDir
	 * @param taggerDir
	 */
	public static void tagCorpus(File corpusDir, File filteredCorpusDir, File taggedCorpusDir, String taggerDir) {

		File[] corpusFiles = corpusDir.listFiles();

		int successCounter = 0;
		int failuresCounter = 0;

		int maxTime = 3000;

		for (int i = 0; i < corpusFiles.length; i++) {
			String fileName = corpusFiles[i].getName();

			boolean isFileAlreadyExist = fliterFile(corpusFiles[i], filteredCorpusDir);
			if(isFileAlreadyExist){
				System.out.println(" ## Skipping " + fileName + ". File already exists");
				continue;
			}
				

			try {
//				String command = "d:/project/tag.bat " + taggerDir + " " + filteredCorpusDir + "/" + fileName + " " + taggedCorpusDir + "/"
//				+ FileUtils.getFileNameNoExtension(fileName) + ".tagged.xml";
				String command = "tag.bat " + taggerDir + " " + filteredCorpusDir + "/" + fileName + " " + taggedCorpusDir + "/"
				+ FileUtils.getFileNameNoExtension(fileName) + ".tagged.xml";

				System.out.print(" ## Tagging " + fileName);

				Process p = Runtime.getRuntime().exec(command);

				boolean isFinished = false;
				int time = 0;
				while (!isFinished && time < maxTime) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						if (p.exitValue() == 0) {
							System.out.println("finished successfully (" + time + ")");
							successCounter++;
						} else {
							System.out.println("failed (" + time + ")");
							failuresCounter++;
						}
						isFinished = true;
					} catch (IllegalThreadStateException e) {
						System.out.print(".");
						time += 2;
					}
				}

				if (time >= maxTime) {
					System.out.println("failed (" + time + ")");
					failuresCounter++;
					p.destroy();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		int total = successCounter + failuresCounter;
		if(total != 0){
        		float successPercent = successCounter * 100 / total;
        		System.out.println("total = " + total);
        		System.out.println("==> Precentage of successes: " + successPercent + "%");
		}
	}

	/**
	 * 
	 * @param file
	 * @param filteredCorpusDir
	 * @return
	 */
	private static boolean fliterFile(File file, File filteredCorpusDir) {
		
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));

			File filteredFileName = new File(filteredCorpusDir.getAbsolutePath() + "/" + file.getName());
			if(filteredFileName.exists())
				return true;
			
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filteredFileName)));

			int i = 0;
			for (String currLine = fileReader.readLine(); currLine != null; currLine = fileReader.readLine()) {
				i++;
				if (!isTag(currLine)) {
					currLine = currLine.replaceAll("&nbsp;", "");
					currLine = currLine.replaceAll("&", "&amp;");
					currLine = currLine.replaceAll("\"", "&quot;");
					currLine = currLine.replaceAll("\\t", "");
					currLine = currLine.replaceAll("'", "&apos;");
					currLine = currLine.replaceAll(">", "&gt;");
					currLine = currLine.replaceAll("<", "&lt;");
				}
				dos.writeBytes(filterLine(currLine) + "\n");
			}
			dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * 
	 * @param currLine
	 * @return
	 */
	private static boolean isTag(String currLine) {
	    if(currLine.startsWith("<") && currLine.endsWith(">"))
		    return true;
	    return false;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private static String filterLine(String line) {
		char[] lineChars = line.toCharArray();
		int length = lineChars.length;
		int wrongCharsCounter = 0;
		for (int i = 0; i < length; i++) {
			if (!isCharLegal(lineChars[i])) {
				//System.out.println(" found illigal char: " + i + " " + lineChars[i]);

				String firstPart = line.substring(0, i - wrongCharsCounter);
				String lastPart = "";
				if (i + 1 < lineChars.length)
					lastPart = line.substring(i - wrongCharsCounter + 1);
				line = firstPart.concat(lastPart);

				wrongCharsCounter++;
			}
		}
		return line;
	}

	/**
	 * 
	 * @param currChar
	 * @return
	 */
	private static boolean isCharLegal(char currChar) {
		char[] validCharsArray = validChars.toCharArray();
		for (int i = 0; i < validCharsArray.length; i++) {
			if (validCharsArray[i] == currChar)
				return true;
		}
		return false;
	}
}
