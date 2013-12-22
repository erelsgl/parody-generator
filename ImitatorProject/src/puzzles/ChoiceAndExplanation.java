package puzzles;

import java.io.Serializable;

/**
 * One choice in a multi-choice question, plus the relevant explanation.
 * 
 * @author Erel Segal Halevi
 * @since 2011
 */
@SuppressWarnings("serial")
public class ChoiceAndExplanation implements Serializable {
	public String choice;       // a sentence, that may be real or random. shown to the user.
	public String reply;        // the reply - whether it is real or random. shown to the user.
	public String explanation;  // the explanation (about the generation of the sentence, if it is random). shown to the developer.
	public Boolean isCorrect;   // is the sentence real or random?
	
	public ChoiceAndExplanation() {}
	
	public ChoiceAndExplanation(String newChoice, String newReply, String newExplanation, boolean newIsCorrect) {
		choice = newChoice;
		reply = newReply;
		explanation = newExplanation;
		isCorrect = newIsCorrect;
	}
	
	@Override public String toString() {
		return choice;
	}
}
