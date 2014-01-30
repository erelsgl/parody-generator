package dataStructures;

public class GeneratedSentence 
{
	private String beginTemplate;
	private String beginSentence;
	private String createdSentence;
	private String mergeParts;
	
	public GeneratedSentence(String beginTemplate, String beginSentence) 
	{
		this.beginTemplate = beginTemplate;
		this.beginSentence = beginSentence;
		setMergeParts(null);
	}
	
	public void setBeginSentence(String beginSentence) 
	{
		this.beginSentence = beginSentence;
	}
	
	public String getBeginSentence() 
	{
		return beginSentence;
	}
	
	public void setBeginTemplate(String beginTemplate) 
	{
		this.beginTemplate = beginTemplate;
	}
	
	public String getBeginTemplate() 
	{
		return beginTemplate;
	}
	
	public void setCreatedSentence(String createdSentence) 
	{
		this.createdSentence = createdSentence;
	}
	
	public String getCreatedSentence() 
	{
		return createdSentence;
	}

	public void setMergeParts(String mergeParts) {
		this.mergeParts = mergeParts;
	}

	public String getMergeParts() {
		return mergeParts;
	}
}
