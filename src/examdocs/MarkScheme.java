package examdocs;

import java.io.File;

public class MarkScheme extends QuestionPaper {
    public MarkScheme(File databaseFile) {
        super(databaseFile);
    }

    public MarkScheme(File databaseFile, Question[] questions) {
        super(databaseFile, questions);
    }
}
