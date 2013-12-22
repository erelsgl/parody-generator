package Imitator.sentenceCreator;

import java.io.IOException;
import java.util.*;

public interface NGramsIfc {

    public void readStatistics(String corpusName) throws IOException;
    
    public String generateSentence(List<String> posTemplate, List<String> realSentence, boolean isReversed);

    /**
     * @param firstPartOfsentence
     * @param secondPartOfsentence must be the same length.
     * @param possibleMergeLocations all indices where a merge is possible. If null - check all indices in the arrays.
     * @return the merged sentence
     */
	public String mergeTwoSentences(String firstPartOfsentence, String secondPartOfsentence, int[] possibleMergeLocations);
}
