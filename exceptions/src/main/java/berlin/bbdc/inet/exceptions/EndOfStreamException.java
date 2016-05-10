package berlin.bbdc.inet.exceptions;

public class EndOfStreamException extends Exception {
    public EndOfStreamException() {
        super();
    }
    public EndOfStreamException(Throwable e) {
        super(e);
    }
    public EndOfStreamException(String str) {
        super(str);
    }


    /**
     * 
     */
    private static final long serialVersionUID = -2632666384358895236L;

}
