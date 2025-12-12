public enum Command {
    QUERY(1),
    REPORT(2),
    RAW_DATA(3),
    EXPORT(4),
    COMPARE(5),
    EXIT(6),
    MANUAL(7);

    private final int value;
    //^ Holds string representation of the user/client commands.

    /**
     * Constructor assigns string representation of the user/client commands.
     */
    Command(int value) { this.value = value; }

    public int getValue() { return this.value; }
}
