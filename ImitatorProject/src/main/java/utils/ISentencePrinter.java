package utils;

import java.util.List;

/**
 * @author Vladimir
 * The main purpose is to join prefixes in hebrew. 
 * Strategy that is injected (or just instantiated) according to the language.
 */
public interface ISentencePrinter 
{
	/**
	 * prints the sentences to the standard IO.
	 * @param sentences - list of generated sentences
	 */
	void printSentences(List<String> sentences, String message);
	/**
	 * prints the sentences to the file specified by user as a plain text (divided into paragraphs).
	 * If the file cannot be created the text is not printed.
	 * @param outputFilePath
	 * @param sentences
	 */
	void printTextToFile(String outputFilePath, List<String> sentences);
}
