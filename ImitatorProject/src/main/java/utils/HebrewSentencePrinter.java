package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class HebrewSentencePrinter implements ISentencePrinter 
{
	private static final int lineLength = 60;
	
	@Override
	public void printSentences(List<String> sentences,String message) 
	{
		System.out.println("\n***********************************");
		System.out.println(message);
		sentences = formatHebrewSentences(sentences);
		for (String sentence : sentences) 
		{
		    System.out.println(sentence);
		}
		System.out.println("***********************************");
	}
	
	@Override
	public void printTextToFile(String outputFilePath, List<String> sentences) 
	{
		try {
			PrintStream out = new PrintStream(new FileOutputStream(outputFilePath,true));
			sentences = formatHebrewSentences(sentences);
			sentences = TextEditingUtility.devideToParagraphs(sentences);
			int len = sentences.size();
			for (int i = 0; i < len; i++) 
			{
				String paragraph = sentences.get(i);
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
		{ }
	}

	private List<String> formatHebrewSentences(List<String> sentences) 
	{
		ArrayList<String> result = new ArrayList<String>();
		int i = 0, j = 0,len;
		for (String sentence : sentences) 
		{
			len = sentence.length();
			i = 0;
			j = 1;
			//connect letters
			while (i < len)
			{
				while (j < len && sentence.charAt(j) != ' ')
				{
					j++;
				}
				char ch = sentence.charAt(j-1);
				if ((j-i == 1) && (ch != ',') && (ch != '.') && (ch != ':') && (ch != ';'))
				{
					//if the word is of length 1 - delete the space
					sentence = sentence.substring(0, j) + sentence.substring(j+1);
					i = j;
				}
				else
				{
					i = j+1;
					j = i;
				}
				len = sentence.length();
			}
			sentence = TextEditingUtility.connectPunctuations(sentence);
		    result.add(sentence);
		}
		return result;
	}
}
