package examdocs;

import java.io.File;
import java.util.logging.Logger;

public class Page
    implements DocumentPageData{

    private final Logger logger;
    private final File imageFile

    public Page(File file, Logger logger) {
        this.imageFile = file;
        this.logger = logger;
    }

    @Override
    public File getFile() {
        return null;
    }
}
