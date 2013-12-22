package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class LTRSentencePrinter implements ISentencePrinter 
{
	private static final int lineLength = 60;
	
	@Override
	public void printSentences(List<String> sentences,String message) 
	{
		System.out.println("\n***********************************");
		System.out.println(message);
		for (String sentence : sentences) 
		{
		    System.out.println(TextEditingUtility.connectPunctuations(sentence));
		}
		System.out.println("***********************************");
	}

	@Override
	public void printTextToFile(String outputFilePath, List<String> sentences) 
	{
		try {
			PrintStream out = new PrintStream(new FileOutputStream(outputFilePath,true));
			sentences = TextEditingUtility.devideToParagraphs(sentences);
			int len = sentences.size();
			for (int i = 0; i < len; i++) 
			{
				String paragraph = sentences.get(i);
				paragraph = TextEditingUtility.connectPunctuations(paragraph);
				int parLength;
				while ((parLength = paragraph.length()) > 0)
				{
					if (parLength > lineLength)
					{
						int curLenght = TextEditingUtility.calcCurrentLineLength(paragraph,lineLength);
						out.println(paragraph.substring(0, curLenght));
						paragraph = (parLength > curLenght+1)?paragraph.substring(curLenght+1) : "";
					}
					else
					{
						out.println(paragraph);
						paragraph = "";
					}
				}
			}
		} catch (FileNotFoundException e) 
		{ }catch (NullPointerException e) 
		{ }
	}

}
