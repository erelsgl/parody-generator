package Imitator.suffixArray;

import java.util.*;

/**
 * A memory-efficient store for words. 
 * Stores every 10 words in a single string.
 * Words are delimited by a single space.
 * @author erelsgl
 * @date 12/08/2011
 */
public class WordListWithIntern implements WordList {
	private List<String> myList = new ArrayList<String>();;

	public WordListWithIntern() {}


	@Override public int size() {
		return myList.size();
	}

	/*
	 * get
	 */
	@Override public String get(int wordIndex) {
		return myList.get(wordIndex);
	}

	/*
	 * add
	 */
	@Override public void add(String word) {
		myList.add(word.intern());
	}

	@Override public void addAll(List<String> sentenceWords) {
		for (String word: sentenceWords)
			add(word);
	}

	@Override public void clear() {
		myList.clear();
	}
}
