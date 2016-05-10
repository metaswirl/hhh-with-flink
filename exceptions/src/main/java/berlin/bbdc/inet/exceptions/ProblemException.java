package berlin.bbdc.inet.exceptions;

public class ProblemException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -2632666384358895236L;

    public ProblemException() {
        super();
    }
    public ProblemException(Throwable e) {
        super(e);
    }
    public ProblemException(String str) {
        super(str);
    }

}
