package Imitator.suffixArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
/**
 * A memory-efficient store for words. 
 * Stores words as char[].
 * @author erelsgl
 * @date 12/08/2011
 */
public class WordListWithByteArrays implements WordList {
	private List<byte[]> myList = new ArrayList<byte[]>();
	
	public WordListWithByteArrays() { }


	@Override public int size() {
		return myList.size();
	}

	/*
	 * get
	 */
	@Override public String get(int wordIndex) {
		try {
			return new String(myList.get(wordIndex), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * add
	 */
	@Override public void add(String word) {
		myList.add(word.getBytes());
	}

	@Override public void addAll(List<String> sentenceWords) {
		for (String word: sentenceWords)
			add(word);
	}

	@Override public void clear() {
		myList.clear();
	}
}
