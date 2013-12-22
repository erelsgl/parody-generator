package Imitator.suffixArray;

public class Range {

    private int startIndex;
    private int endIndex;

    public Range(int startIndex, int endIndex) {
	this.startIndex = startIndex;
	this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    
    public int getCount() {
    	return endIndex-startIndex+1;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    
    public String toString() {
    	return startIndex+"-"+endIndex;
    }
    
}
