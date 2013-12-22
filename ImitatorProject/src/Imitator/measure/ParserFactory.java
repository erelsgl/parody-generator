package Imitator.measure;

public class ParserFactory {

    /**
     * Factory of parsers. 
     * If we want to change the implementation of the parser, 
     * just change here the impl of TaggedCorpusParserIfc that is been returned
     * @return
     */
    public static TaggedCorpusParserIfc createParser(){
	return new StanfordXMLParser();
    }
}
