package examdocs;

import commands.Command;
import commands.Commands;
import database.PaperDatabase;
import utils.FileHandler;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    // TODO: Move this to database
    private final Document document;
    private final PaperDatabase database;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Creates a new exam paper and checks whether its images have been saved, but does not save them
     * @param fileName the name of the file, relative to dirPath
     * @param dirPath the path to the directory where the paper, its questions and its page images are stored. From the root directory
     */
    public ExamPaper(String fileName, String dirPath) {
        this.document = new Document(fileName, dirPath);
        this.database = new PaperDatabase(new File(fileName + File.separator + dirPath));
    }

    public ExamPaper(ArrayList<Question> questions, String fileName, String dirPath) {
        FileHandler.clearDirectory(dirPath);

        this.document = new Document(fileName, dirPath);
        this.database = new PaperDatabase(new File(fileName + File.separator + dirPath));

        int index = 0;
        for (Question question: questions) {
            int startPage = document.length();
            document.addPage(question);

            // TODO: Work out how to do this
            database.questionTable.setRow(
                    question.getImage(),
                    new int[] {index, startPage, 0, document.length(), 0});
            index ++;
        }

        database.pageTable.makeFromDocument(document);
    }

    /**
     * Saves images from exam paper's document, updating imagesSaved if it was successful. Only saves
     * images if they are not already saved
     */
    public void saveAsImages() {
        database.pageTable.makeFromDocument(document);
    }

    /**
     * Saves all the questions in files and updates the instance's questions to match.
     * Based on user input
     */
    public void makeQuestions() {
        Commands commands = new Commands(new Command[] {
                new Command("end", new String[]{"end", "e"}),
                new Command("start", new String[]{"start", "s"}),
                new Command("exit", new String[]{"exit"}),
                new Command("pass", new String[]{"pass", "p", " "})
        });

        // Save page images
        this.saveAsImages();

        Scanner scanner = new Scanner(System.in);

        int pageNumber = 0, startPercent = 0, startPage = 0, questionNumber = 0;
        boolean ended = false, inQuestion = false;

        while (!ended) {
            // Command line interface
            System.out.printf("Page number %d reached\n", pageNumber);

            // Get command
            Command command = null;
            while (command == null) {
                command = commands.scanCommand(scanner);

                // Check that questions are being started/ended at correct times
                if (command.equals("start") && inQuestion) {
                        System.out.println("Question cannot be started until current has been ended");
                        command = null;
                } else if (command.equals("end") && !inQuestion) {
                        System.out.println("Question must be started before being ended");
                        command = null;
                }
            }

            if (command.equals("end") || command.equals("start")) {
                System.out.print("Percentage: ");
                int linePercent = Integer.parseInt(scanner.next());

                if (!inQuestion) {
                    // Start creating new question
                    startPercent = linePercent;
                    startPage = pageNumber;

                } else {
                    // Save the current question
                    database.questionTable.setRow(new int[] {
                            questionNumber,
                            startPage,
                            startPercent,
                            pageNumber,
                            linePercent
                    });

                    questionNumber ++;
                }

                // Allow a question to start on the same page as the previous one ended
                pageNumber --;

                inQuestion = !inQuestion;
            } else if (command.equals("exit")) {
                ended = true;
            }

            // Get the next image
            pageNumber ++;
        }
    }


    /**
     * Get specific question from the paper
     * @param index the number for the question
     * @return a reference to the Question
     */
    public Question getQuestion(int index) {
        return (Question) database.questionTable.getRows(index, index+1).get(0);
    }
}
