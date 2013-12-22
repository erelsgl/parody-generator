package Imitator.suffixArray;

import java.util.*;

/**
 * A memory-efficient store for words. 
 * Stores every 10 words in a single string.
 * Words are delimited by a single space.
 * @author erelsgl
 * @date 12/08/2011
 */
public class WordListWithChunks implements WordList {
	public static int defaultChunkSize = 100;

	private List<CharSequence> myList = new ArrayList<CharSequence>();;
	private int myCount = 0;
	private int myChunkSize;

	public WordListWithChunks(int theChunkSize) {
		myChunkSize = theChunkSize;
	}
	
	public WordListWithChunks() {
		this(defaultChunkSize);
	}


	@Override public int size() {
		return myCount;
	}

	/*
	 * get
	 */
	private int cachedChunkIndex = -1;
	private String[] cachedWords = null;
	@Override public String get(int wordIndex) {
		if (myChunkSize==1)
			return myList.get(wordIndex).toString();
		else {
			int chunkIndex = wordIndex/myChunkSize; // words 10..19 are in chunk 1
			int wordIndexInChunk = wordIndex%myChunkSize;
			if (cachedChunkIndex!=chunkIndex) {
				cachedChunkIndex = chunkIndex;
				CharSequence chunk = chunkIndex<myList.size()? myList.get(chunkIndex): addBuffer.toString();
				cachedWords = chunk.toString().split(" ");
			}
			return cachedWords[wordIndexInChunk];
		}
	}

	/*
	 * add
	 */
	private StringBuffer addBuffer = new StringBuffer();
	private int addBufferWordCount = 0;
	@Override public void add(String word) {
		if (myChunkSize==1)
			myList.add(word);
		else {
			addBuffer.append(word);
			addBufferWordCount++;
			if (addBufferWordCount>=myChunkSize) {
				addBuffer.trimToSize();
				myList.add(addBuffer);
				//addBuffer.delete(0, addBuffer.length());
				addBuffer = new StringBuffer();
				addBufferWordCount = 0;
			} else {
				addBuffer.append(' ');
			}
		}
		myCount++;
	}

	@Override public void addAll(List<String> sentenceWords) {
		for (String word: sentenceWords)
			add(word);
	}

	@Override public void clear() {
		myList.clear();
		myCount = 0;
		cachedChunkIndex = -1;
		cachedWords = null;
		addBuffer.delete(0, addBuffer.length());
		addBufferWordCount = 0;
	}
}
