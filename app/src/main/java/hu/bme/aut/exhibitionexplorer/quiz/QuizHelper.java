package hu.bme.aut.exhibitionexplorer.quiz;

/**
 * Created by Zay on 2016.11.06..
 */
public class QuizHelper {
    public int correctAnswer;
    public String[] getQuestion(String msg) {
        String parts[] = msg.split("#");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("*")) {
                parts[i] = parts[i].substring(1);
                correctAnswer = i;
            }
        }

        return parts;
    }
}
