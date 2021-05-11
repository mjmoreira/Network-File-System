package nfs.shared;

public enum ReturnStatus {
	SUCCESS						(true, "Success."),
	FAILURE_NOT_IMPLEMENTED		(false, "Failure: not implemented."),
	FAILURE_INVALID_PATH		(false, "Failure: invalid path."),
	FAILURE_PATH_DOES_NOT_EXIST	(false, "Failure: path does not exist."),
	FAILURE_FILE_ALREADY_EXISTS	(false, "Failure: file already exists."),
	FAILURE_DIRECTORY_ALREADY_EXISTS (false, "Failure: directory already exists."),
	FAILURE_PATH_NOT_OWNED_BY_STORAGE
		(false, "Failure: path is not owned by a storage server."),
	FAILURE_STORAGE_MOUNT_TOO_DEEP
		(false, "Failure: storage mount point is too deep."),
	FAILURE_INVALID_STORAGE_ID (false, "Failure: invalid storage identifier.");

	public final boolean ok;
	public final String message;

	private ReturnStatus(boolean ok, String message) {
		this.ok = ok;
		this.message = message;
	}
}
