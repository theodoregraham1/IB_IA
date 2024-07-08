# Explanation of Classes
## Package: commands
This package contains only deprecated classes used on the command line interface

## Package: database
This package contains classes to represent a database and the data within it.

### Abstract Class: Database
This is a superclass for a generalised database. A 'database' is designed to contain multiple tables 
and allow interface between them. Therefore, inheritors of this class should have class variables 
with public visibility which each contain a single table of the database.

#### Abstract Internal Class: ImageTable<T>
This contains the methods required to save data containing an image file for each record on the most 
basic level. It is an internal class so that tables can access each other.

### Interface: ImageFile<T>
This represents an image stored in a file (so far this can be a Page or a Question)

### Class: Paper Database
This is a subclass of Database. It represents a database for an ExamPaper specifically. Each instance
contains a table for pages and one for questions. It should be noted that the PDF is not contained
within the Database.

#### Internal Class: QuestionTable
This is a subclass of ImageTable<Question>. It implements the methods to specifically store a question 
from the paper into the File system.

#### Internal Class: PageTable
This is a subclass of ImageTable<Page>. It implements the methods to specifically store pages
from the paper into the File system. It also contains the Document so that it can do this 
automatically.

## Package: examdocs
### Enum: BoardLevel
Suggests what type of paper an ExamBoard contains

### Class: Document
Handles all interfacing between the system and PDFs, via Apache PDFBox. For the purpose of external usage, represents
a PDF document.

### Class: ExamBoard
Contains and manages ExamPapers and their databases, allowing the front-end to work with them as 
a group. Also implements Iterable<Question> for use by the GUI.

### Class: ExamPaper
Pulls together Document and PaperDatabase to comprehensively represent a PDF document made up of questions and pages.
As of current this is also used to represent mark schemes.

### Class: Page
Implementation of ImageFile to represent a page of a PDF document in particular.

### Class: Question
Implementation of ImageFIle to represent a question from an exam paper or a mark scheme.

## Package: GUI
### Class: AnchorListener
Implementation of ActionListener for specific use on the anchor selection boxes in GUI pages. Also handles opening new
pages.

### Class: Constants
Contains constants for use in the GUI package.

### Class: ImageScroller 
A Scrollable implementation of a JLabel to allow an image (usually a Page or Question) to be scrolled in a JScrollPane

### Class: InputValidation
Contains static methods to validate user inputs.

### Class: LinedImageScroller
A subclass of ImageScroller that allows lines to be added to the image. These lines can overlap and can be edited.

### Abstract Class: SplitPDFPage
Contains implemented and abstract methods for use by any GUI page that is splitting a PDF in any capacity. Uses 
access methods for class variables due to stipulations by the IntelliJ UI Designer.

### Class: CreatePaperPage
Put together by IntelliJ UI Designer. Adds questions on to a new paper and then exports it to an existing ExamBoard.

### Class: ImportPaperPage
Put together by IntelliJ UI Designer. Opens a file chooser dialog to import a paper to be split by SplitPaperPage.

### Class: SplitPaperPage
Put together by IntelliJ UI Designer. Allows the user to define a series of questions from a PDF, makes the splits, then
exports it.

### Class: SplitSchemePage
Put together by IntelliJ UI Designer. Allows the user to define a series of questions from a PDF, makes the splits, then
exports it. Lacks the marks choice of SplitPaperPage and allows reference to the questions its brother generated. This 
is in progress

## Package: utils
### Class: Constants
Contains constants used throughout the system (although not in the GUI).

### Class: FileHandler
Contains useful utility methods for working with files.

### Class: ImageHandler
Contains useful utility methods for working with images.

### Class: MultiValueMap<K, V>
This implementation of the Map interface allows multiple different values for the same key. This is used primarily
to hold lines for a LinedImageScroller. This acts as an implementation of a hash table.