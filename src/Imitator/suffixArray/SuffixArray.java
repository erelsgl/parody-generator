package Imitator.suffixArray;

import java.util.Arrays;

public class SuffixArray {
    int len;
    String[] suffixes;

    SuffixArray(String s) {
	len = s.length();
	suffixes = new String[len];
	for (int i = 0; i < len; i++) {
	    // keep the whole substring and not only pointer - not efficient!
	    suffixes[i] = s.substring(i); 
	}
	Arrays.sort(suffixes);
    }

    /**
     * 
     * @param p
     * @return
     */
    public int find(String p) {
	if (len == 0) {
	    return p.length() == 0 ? 0 : -1;
	}
	int low = 0;
	int high = suffixes.length - 1;
	while (low < high) {
	    int middle = low + (high - low) / 2;
	    if (suffixes[middle].compareTo(p) >= 0) {
		high = middle;
	    } else {
		low = middle + 1;
	    }
	}
	if (suffixes[high].startsWith(p)) {
	    return len - suffixes[high].length();
	}
	return -1;
    }

    /**
     * Search for the range of suffixes that starts with the given string
     * @param str
     * @param startIndex - the index of the first suffix that starts with 'str'
     * @param endIndex - the index of the last suffix that starts with 'str'
     */
    public void findRange(String str, int startIndex, int endIndex) {
	int middleIndex = find(str);
	startIndex = -1;
	endIndex = -1;
	
	// search for the index of the last suffix that starts with p
	for (int i = middleIndex; i < len; i++) {
	    if (!suffixes[i].startsWith(str)){
		endIndex = i - 1;
		break;
	    }
	}
	
	if(endIndex == -1)
	    endIndex = len-1; 
	    
	// search for the index of the first suffix that starts with p
	for (int i = middleIndex; i > 0; i--) {
	    if (!suffixes[i].startsWith(str)){
		startIndex = i + 1;
		break;
	    }
	}

	if(startIndex == -1)
	    startIndex = 0; 

    }
}