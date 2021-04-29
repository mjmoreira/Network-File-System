package nfs.filesystem;

public class InvalidSizeException extends RuntimeException {
	private static final long serialVersionUID = 20210428001L;

	public InvalidSizeException() {
		super();
	}

	public InvalidSizeException(String message) {
		super(message);
	}
}
