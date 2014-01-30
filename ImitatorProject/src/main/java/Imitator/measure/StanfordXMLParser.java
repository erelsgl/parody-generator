package Imitator.measure;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class StanfordXMLParser implements TaggedCorpusParserIfc{

    private DOMParser parser;
    private int _totalNumberOfSentences = 0;
    
    private int _numOfSentencesInCurrFile;
    private int _sentenceIndex;
    private NodeList _sentencesList;
    
    private int _numOfWordsInCurrSentence;
    private int _wordIndex;
    private NodeList _wordsNodeList;
    
    private String _currWord;
    private String _currPOS;
    
    StanfordXMLParser() 
    {
		parser = new DOMParser();
		//parser.s.setPreserveWhitespace(true);

		/*try 
		{
		//	parser.setFeature("http://apache.org/xml/features/allow-java-encodings",true);
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		  
    }

    @Override
    public void startFile(String filePath)
    {
		try 
		{
			
		    parser.parse(filePath);
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	
		Document document = parser.getDocument();
	
		// get all 'sentence' elements from the current file
		_sentencesList = document.getElementsByTagName("sentence");
	
		_numOfSentencesInCurrFile = _sentencesList.getLength();
		_totalNumberOfSentences += _numOfSentencesInCurrFile;
		
		_sentenceIndex = 0;

    }

    @Override
    public boolean hasMoreSentences() {
	return _sentenceIndex < _numOfSentencesInCurrFile; // TODO: check that the last sentence is not ignored!
    }

    @Override
    public String getNextSentence() {
	
	_wordIndex = 0;
	
	// get all 'word' elements of the current 'sentence'
	_wordsNodeList = _sentencesList.item(_sentenceIndex).getChildNodes();

	_numOfWordsInCurrSentence = _wordsNodeList.getLength();
	
	_sentenceIndex++;
	return null; // TODO: Think if there is a need to return the sentence here
    }

    @Override
    public boolean hasMoreWordsInSentence() {
	return _wordIndex < _numOfWordsInCurrSentence; // TODO: check that the last word is not ignored!
    }

    @Override
    public String getNextWord() {

	for (; _wordIndex < _numOfWordsInCurrSentence; _wordIndex++) {
	    
	    Node wordNode = _wordsNodeList.item(_wordIndex);
	    if (wordNode.getNodeName().equals("word")) {

		// get the POS tag of the current word
		_currPOS = wordNode.getAttributes().getNamedItem("pos").getNodeValue();

		_currWord = wordNode.getChildNodes().item(0).getNodeValue();
		
		break;
	    }
	}

	_wordIndex++;
	return _currWord;
    }

    @Override
    public String getPOS() {
	return _currPOS;
    }

    @Override
    public void finishFile() {
	parser.reset();
	
    }
}
