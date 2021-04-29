package nfs.shared;

public class Constants {
	public static final String REGISTRY_ID_METADATA = "NFS-MetaDataServer";
	public static final String REGISTRY_ID_STORAGE = "NFS-StorageServer";
	public static final int REGISTRY_PORT = 1099;

	public static class ReturnStatus {
		public static final int NOT_IMPLEMENTED = -1;
		public static final int SUCCESS = 0;
		public static final int FAILURE_INVALID_PATH = 1;
		public static final int FAILURE_PATH_DOES_NOT_EXIST = 2;
		public static final int FAILURE_FILE_ALREADY_EXISTS = 3;
		public static final int FAILURE_DIRECTORY_ALREADY_EXISTS = 4;
	}
}
