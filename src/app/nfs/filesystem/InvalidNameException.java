package nfs.filesystem;

public class InvalidNameException extends RuntimeException {
	private static final long serialVersionUID = 20210428001L;
	
	public InvalidNameException() {
		super();
	}

	public InvalidNameException(String message) {
		super(message);
	}
}
