package Imitator.suffixArray;

import java.util.Comparator;
import java.util.List;

import Imitator.measure.MeasureProps;

public class SuffixesComparator implements Comparator<Integer> {

    private WordList _list;
    private int _listSize;
    private int _numberOfWordsToCompare;
    
    public SuffixesComparator(WordList list) {
		_list = list;
		_listSize = _list.size();
	
		_numberOfWordsToCompare = MeasureProps.getNumberOfWordsToCompareInSuffixArray();
    }
    
    @Override public int compare(Integer o1, Integer o2) {
		int maxIndex = Math.max(o1, o2);
		int n = Math.min(_numberOfWordsToCompare, _listSize - maxIndex);
		
		StringBuffer str1 = new StringBuffer(32);
		StringBuffer str2 = new StringBuffer(32);
		
		// create the string to compare
		for (int i = 0; i < n; i++) {
		    str1.append(_list.get(o1 + i)).append(" ");
		    str2.append(_list.get(o2 + i)).append(" ");
		    // TODO: get a span of words at once
		}

		//TODO case sensitive
		return (str1.toString()).compareTo/*IgnoreCase*/(str2.toString());
    }
}
