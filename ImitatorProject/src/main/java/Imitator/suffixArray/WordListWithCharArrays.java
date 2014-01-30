package Imitator.suffixArray;

import java.util.ArrayList;
import java.util.List;
/**
 * A memory-efficient store for words. 
 * Stores words as char[].
 * @author erelsgl
 * @date 12/08/2011
 */
public class WordListWithCharArrays implements WordList {
	private List<char[]> myList = new ArrayList<char[]>();;
	
	public WordListWithCharArrays() { }


	@Override public int size() {
		return myList.size();
	}

	/*
	 * get
	 */
	@Override public String get(int wordIndex) {
		return new String(myList.get(wordIndex));
	}

	/*
	 * add
	 */
	@Override public void add(String word) {
		myList.add(word.toCharArray());
	}

	@Override public void addAll(List<String> sentenceWords) {
		for (String word: sentenceWords)
			add(word);
	}

	@Override public void clear() {
		myList.clear();
	}
}
