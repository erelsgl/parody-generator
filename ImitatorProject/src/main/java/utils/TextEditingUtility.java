package utils;

import java.util.ArrayList;
import java.util.List;

public class TextEditingUtility 
{
	public static List<String> devideToParagraphs(List<String> sentences)
	{
		ArrayList<String> result = new ArrayList<String>();
		int numOfSentences = sentences.size(),curSentenceCount = 0;
		while (curSentenceCount < numOfSentences) 
		{
			String sentToAdd = "";
			sentToAdd += sentences.get(curSentenceCount);
			//a paragraph has 3 - 5 sentences
			int rand = ((int)(Math.random() * 10)) % 3 + 3;
			for (int i = 1; i < rand && (curSentenceCount + i < numOfSentences); i++) 
			{
				sentToAdd += sentences.get(curSentenceCount + i);
			}
			curSentenceCount += rand;
			result.add(sentToAdd);
		}
		return result;
	}

	public static String connectPunctuations(String sentence) 
	{
		int i,j;
		i = sentence.length() - 1;
		//leave one space at end
		if (sentence.charAt(i) == ' ')
		{
			i--;
		}
		j=i;
		//connect , and .
		while (i > 0)
		{
			while (j >= 0 && sentence.charAt(j) != ' ')
			{
				j--;
			}
			char ch = sentence.charAt(j+1);
			if ((i-j == 1) && ((ch == ',') || (ch == '.') || (ch == ':') || (ch == ';')))
			{
				//if the word is of length 1 - delete the space
				sentence = sentence.substring(0, j) + sentence.substring(j+1);
				i = j;
			}
			else
			{
				i = j-1;
				j = i;
			}
		}
		return sentence;
	}

	/**
	 * calculates the actual line length - to prevent lice cutting
	 * @param paragraph
	 * @param lineLength
	 * @return - index of the next char or length of the string if the last word in range
	 */
	public static int calcCurrentLineLength(String paragraph, int lineLength) 
	{
		int result = lineLength;
		while (result < paragraph.length() && paragraph.charAt(result) != ' ')
		{
			result++;
		}
		return result;
	}
}
