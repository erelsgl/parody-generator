package Imitator.suffixArray;

import java.util.List;

public interface WordList /*extends Iterable<String>*/  {
	public abstract int size();

	public abstract String get(int wordIndex);

	public abstract void add(String word);

	public abstract void addAll(List<String> sentenceWords);

	public abstract void clear();
}
