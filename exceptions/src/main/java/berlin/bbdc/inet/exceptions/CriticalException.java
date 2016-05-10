package berlin.bbdc.inet.exceptions;

public class CriticalException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 4723170447436849938L;

    public CriticalException() {
        super();
    }
    public CriticalException(Throwable e) {
        super(e);
    }
    public CriticalException(String str) {
        super(str);
    }

}
