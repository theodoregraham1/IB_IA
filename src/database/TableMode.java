package database;

public enum TableMode {
    /* Format:
        Question number (1 byte);
        Start page (1 byte);
        Percent start height (1 byte);
        End page (1 byte);
        Percent end height (1 byte);
     */
    QUESTIONS,
    PAGES,
}
