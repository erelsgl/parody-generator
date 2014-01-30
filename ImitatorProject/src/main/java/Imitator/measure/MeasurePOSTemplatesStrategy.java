package Imitator.measure;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import Imitator.common.Commons;
import Imitator.common.FileUtils;
import Imitator.common.FilterUtils;

public class MeasurePOSTemplatesStrategy implements MeasureStrategy{

    private Map<String, Long> _posTemplates; 
    private Map<String, Collection<String>> _template2RealSentences;
    
    public MeasurePOSTemplatesStrategy() 
    {
		_posTemplates = new HashMap<String, Long>(); 
		_template2RealSentences = new HashMap<String, Collection<String>>();
	
		String realSentencesFileName = FileUtils.getRealSentencesOfTemplateFile(MeasureProps.getCorpusDir()); 
		try {
		    new File(realSentencesFileName).createNewFile();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    @Override
    public void handleSentence(Map<String, Map<String,Integer>> map, TaggedCorpusParserIfc taggedCorpusParser)
    {

		StringBuffer posTemplateSB = new StringBuffer();
		StringBuffer realSentenceSB = new StringBuffer();
	
		// Iterate over the words on the current sentence
		while(taggedCorpusParser.hasMoreWordsInSentence()){
		    
		    String word = taggedCorpusParser.getNextWord();
		    
		    realSentenceSB.append(word).append(" ");
	
		    String pos = taggedCorpusParser.getPOS();
		    
		    String posKey = Commons.replaceSpecialCharacters(pos);
	
		    posTemplateSB.append(posKey).append(" ");
	
		}
	
		String posTemplate = posTemplateSB.toString();
	
		// filter too short templates
		if (FilterUtils.isTemplateTooShort(posTemplate, MeasureProps.getMinimalTemplateLength()))
		    return;
	
		// filter too long templates
		if (posTemplate.length() > 180)
		    return;
	
		if (_posTemplates.containsKey(posTemplate))
		{
		    long value = _posTemplates.get(posTemplate);
		    _posTemplates.put(posTemplate, value + 1);
		} else
		    _posTemplates.put(posTemplate, 1L);
	
		String sentence = realSentenceSB.toString();
	
		// keep the real sentence in special collection
		Collection<String> realSentencesOfTemplate = _template2RealSentences.get(posTemplate);
	
		if (realSentencesOfTemplate == null) {
		    realSentencesOfTemplate = new HashSet<String>();
		    realSentencesOfTemplate.add(sentence);
		    _template2RealSentences.put(posTemplate, realSentencesOfTemplate);
		} else {
		    realSentencesOfTemplate.add(sentence); // if already exist, the operation will fail
		}
    }

    @Override
    public void writeToFiles(Map<String, Map<String,Integer>> words) 
    {
		long numberOfRealSentence = 0;
	
		// the filter cannot be earlier because the frequency of each sentence
		// is known only at the end
		_posTemplates = FilterUtils.removeUncommonTemplates(_posTemplates, MeasureProps.getMinimalTemplateLength(), MeasureProps
			.getMinimalTemplateFrequency());
	
		// filter the real sentences map coordinately to the filter on
		// '_posTemplates'
		System.out.println(" # templates size before filtering: " + _template2RealSentences.size());
		for (Iterator<String> itr = _template2RealSentences.keySet().iterator(); itr.hasNext();) {
		    String posTemplate = itr.next();
	
		    if (!_posTemplates.keySet().contains(posTemplate))
			itr.remove();
		}
		System.out.println(" # templates size after filtering: " + _template2RealSentences.size());
	
		FileUtils.writeRealSentencesFile(_template2RealSentences, MeasureProps.getCorpusDir());
	
		System.out.println(" ## Total number of real sentences: " + numberOfRealSentence);
		System.out.println(" ## Total number of templates: " + _posTemplates.size());
	
		FileUtils.writePOSTemplatesMapToFile(MeasureProps.getCorpusDir(), _posTemplates);
	
		_posTemplates.clear();
		_template2RealSentences.clear();
	}
	
	@Override
	public String getMessage() 
	{
		return "Measuring POS templates";
    }

	@Override
	public void destroy() 
	{
		_posTemplates.clear();
		_posTemplates = null;
		_template2RealSentences.clear();
		_template2RealSentences = null;
		System.gc();
	}

}
