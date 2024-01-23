import examdocs.ExamPaper;

public class Main {

    public static void main(String[] args) {
        ExamPaper paper = new ExamPaper("Questionpaper-Paper1-June2017.pdf", "./papers/GCSE/June-2013/examdocs.Question-paper/");
        paper.saveAsImages();
        paper.makeQuestions();
    }
}