package hu.bme.aut.exhibitionexplorer.quiz;

/**
 * Created by Zay on 2016.11.06..
 */
public class QuizHelper {
    public static final int ANSWER_A = 0;
    public static final int ANSWER_B = 1;
    public static final int ANSWER_C = 2;
    public static final int ANSWER_D = 3;

    private int correctAnswer;
    private String questionTitle;

    private String[] questions;

    public QuizHelper(String msg) {
        String parts[] = msg.split("#");
        questions = new String[parts.length-1];
        questionTitle = parts[0];
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].startsWith("*")) {
                questions[i-1] = parts[i].substring(1);
                correctAnswer = i-1;
            } else {
                questions[i-1] = parts[i];
            }

        }
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public String getQuestionA(){
        return questions[0];
    }

    public String getQuestionB(){
        return questions[1];
    }

    public String getQuestionC(){
        return questions[2];
    }

    public String getQuestionD(){
        return questions[3];
    }

    public boolean isCorrectAnswer(int answer){
        if(correctAnswer == answer){
            return true;
        }
        return false;
    }

    public int getCorrectAnswerID(){
        return correctAnswer;
    }
}
