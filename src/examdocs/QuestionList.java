package examdocs;

import java.util.ArrayList;

// TODO: There's absolutely no reason to do this
public interface QuestionList
    extends Iterable<Question> {

    /**
     * Gets the question at a specific index
     * @param i the index of the question
     * @return the question
     */
    public Question getQuestion(int i);

    /**
     * Gets all the questions from the list
     * @return an ArrayList of all the questions
     */
    public ArrayList<Question> getAllQuestions();
}
