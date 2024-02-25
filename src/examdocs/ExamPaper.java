package examdocs;

import commands.Command;
import commands.Commands;
import database.PaperDatabase;
import utils.FileHandler;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.PAPER_FILE_NAME;

public class ExamPaper
    implements Iterable<Page> {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    private final Document document;
    private final PaperDatabase database;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Creates a new exam paper and checks whether its images have been saved, but does not save them
     * @param databaseFile the directory where this paper has its database's root
     */
    public ExamPaper(File databaseFile) {
        this.document = new Document(new File(databaseFile, PAPER_FILE_NAME));
        this.database = new PaperDatabase(databaseFile, document);
    }

    public ExamPaper(ArrayList<Question> questions, File databaseFile) {
        FileHandler.clearDirectory(databaseFile);

        this.document = new Document(new File(databaseFile, PAPER_FILE_NAME));
        this.database = new PaperDatabase(databaseFile, document);

        int index = 0;
        for (Question question: questions) {
            int startPage = document.length();
            document.addPage(question);

            database.questionTable.setRow(
                    question.getImage(),
                    new int[] {index, startPage, 0, document.length(), 0}); // TODO: Improve this to actually use the correct page
            index ++;
        }
    }

    /**
     * Saves all the questions in files and updates the instance's questions to match.
     * Based on user input
     */
    public void makeQuestions() {
        // Get currently saved questions and start from there
        int questionNumber = (int) database.questionTable.length();

        Commands commands = new Commands(new Command[] {
                new Command("end", new String[]{"end", "e"}),
                new Command("start", new String[]{"start", "s"}),
                new Command("exit", new String[]{"exit"}),
                new Command("pass", new String[]{"pass", "p", " "})
        });

        Scanner scanner = new Scanner(System.in);

        int pageNumber = 0, startPercent = 0, startPage = 0;
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
                            pageNumber+1,
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
        try {
            return (Question) database.questionTable.getRows(index, index + 1).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public Iterator<Page> iterator() {
        return new Iterator<>() {
            int count = 0;

            @Override
            public boolean hasNext() {
                return !database.pageTable.getRows(count, count + 1).isEmpty();
            }

            @Override
            public Page next() {
                count++;
                return (Page) database.pageTable.getRows(count - 1, count).get(0);
            }
        };
    }
}
