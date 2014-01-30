package Imitator.suffixArray;

import java.util.*;

/**
 * A memory-efficient store for words. 
 * Stores every 10 words in a single string.
 * Words are delimited by a single space.
 * @author erelsgl
 * @date 12/08/2011
 */
public class WordListWithHash implements WordList {
	private List<String> myList = new ArrayList<String>();
	private Map<String,Integer> words = new HashMap<String,Integer>(); // store the first index of the word 

	@Override public void add(String word) {
		if (words.containsKey(word))
			myList.add(myList.get(words.get(word)));
		else {
			words.put(word, myList.size());
			myList.add(word);
		}
	}


	public WordListWithHash() {}

	@Override public int size() {
		return myList.size();
	}

	/*
	 * get
	 */
	@Override public String get(int wordIndex) {
		return myList.get(wordIndex);
	}

	@Override public void addAll(List<String> sentenceWords) {
		for (String word: sentenceWords)
			add(word);
	}

	@Override public void clear() {
		myList.clear();
		words.clear();
	}
}
