package nfs.filesystem;

import static nfs.shared.Constants.ReturnStatus.*;
import nfs.shared.LsInfo;
import nfs.shared.LsDirectory;
import nfs.shared.LsFile;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestFilesystem {
	// TODO: criar teste para verificar se o que criei existe, ou é imprimido
	// corretamente pelo LsInfo.

	@Test
	void fail_create_heigth_1_dir_non_storage() {
		Filesystem f = new Filesystem();
		String[] path = {"", "nonStorage"};
		assertEquals(FAILURE_PATH_NOT_OWNED_BY_STORAGE, f.createDirectory(path));
	}

	@Test
	void create_heigth_1_storage() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage1"));
	}

	@Test
	void fail_create_heigth_2_storage() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage1"};
		f.createStorageDirectory(path, "storage1");
		String[] path2 = {"", "storage1", "storage2"};
		assertEquals(FAILURE_PATH_TOO_DEEP,
		             f.createStorageDirectory(path2, "storage2"));
	}

	@Test
	void fail_create_file_in_root() {
		Filesystem f = new Filesystem();
		String[] path = {"", "file1.txt"};
		assertEquals(FAILURE_PATH_NOT_OWNED_BY_STORAGE, f.createFile(path, 123));
	}

	@Test
	void create_file_in_storage() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		f.createStorageDirectory(path, "storage1");
		String[] path2 = {"", "storage", "file"};
		assertEquals(SUCCESS, f.createFile(path2, 1234));
	}

	@Test
	void create_2_storages() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage1"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage1"));
		String[] path2 = {"", "storage2"};
		assertEquals(SUCCESS, f.createStorageDirectory(path2, "storage2"));
	}

	@Test
	void create_2_storages_same_path() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		assertEquals(FAILURE_DIRECTORY_ALREADY_EXISTS,
		             f.createStorageDirectory(path, "storage"));
	}

	@Test
	void create_1_storage_2_directories() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(path1));
		String[] path2 = {"", "storage", "dir2"};
		assertEquals(SUCCESS, f.createDirectory(path2));
	}

	@Test
	void create_file_in_directory() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(path1));
		String[] path2 = {"", "storage", "dir1", "file1.asdf"};
		assertEquals(SUCCESS, f.createFile(path2, 2345));
	}

	@Test
	void create_empty_file() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(path1));
		String[] path2 = {"", "storage", "dir1", "file1.asdf"};
		assertEquals(SUCCESS, f.createFile(path2, 0));
	}

	@Test
	void fail_create_file_with_bad_name() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(path1));
		String[] path2 = {"", "storage", "dir1", " file1.asdf"};
		assertEquals(FAILURE_INVALID_PATH, f.createFile(path2, 2345));
	}

	@Test
	void fail_create_directory_with_bad_name() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", " dir1"};
		assertEquals(FAILURE_INVALID_PATH, f.createDirectory(path1));
	}

	@Test
	void fail_create_file_with_inexistent_tree() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir", "file"};
		assertEquals(FAILURE_PATH_DOES_NOT_EXIST, f.createFile(path1, 10));
	}

	@Test
	void fail_create_directory_with_inexistent_tree() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] path1 = {"", "storage", "dir1", "dir2"};
		assertEquals(FAILURE_PATH_DOES_NOT_EXIST, f.createDirectory(path1));
	}
	
	@Test
	void list_empty_directory() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] pathDir = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir));

		LsInfo info = f.listDirectory(pathDir);
		assertEquals(0, info.directories.length);
		assertEquals(0, info.files.length);
		assertTrue(Arrays.equals(info.path, pathDir));
	}

	@Test
	void list_directory_only_with_files() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] pathDir = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir));
		String[] pathF1 = {"", "storage", "dir1", "file1.asdf"};
		String[] pathF2 = {"", "storage", "dir1", "file2.asdf"};
		assertEquals(SUCCESS, f.createFile(pathF1, 2345));
		assertEquals(SUCCESS, f.createFile(pathF2, 3456));

		LsInfo info = f.listDirectory(pathDir);
		assertEquals(0, info.directories.length);
		assertEquals(2, info.files.length);
		assertTrue(Arrays.equals(info.path, pathDir));
	}

	@Test
	void list_directory_only_with_directories() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] pathDir = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir));
		String[] pathD1 = {"", "storage", "dir1", "subdir1"};
		String[] pathD2 = {"", "storage", "dir1", "subdir2"};
		assertEquals(SUCCESS, f.createDirectory(pathD1));
		assertEquals(SUCCESS, f.createDirectory(pathD2));

		LsInfo info = f.listDirectory(pathDir);
		assertEquals(2, info.directories.length);
		assertEquals(0, info.files.length);
		assertTrue(Arrays.equals(info.path, pathDir));
	}

	@Test
	void list_directory_with_directories_and_files() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] pathDir = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir));
		String[] pathF1 = {"", "storage", "dir1", "file1.asdf"};
		String[] pathF2 = {"", "storage", "dir1", "file2.asdf"};
		String[] pathD1 = {"", "storage", "dir1", "subdir1"};
		String[] pathD2 = {"", "storage", "dir1", "subdir2"};
		assertEquals(SUCCESS, f.createFile(pathF1, 2345));
		assertEquals(SUCCESS, f.createFile(pathF2, 3456));
		assertEquals(SUCCESS, f.createDirectory(pathD1));
		assertEquals(SUCCESS, f.createDirectory(pathD2));

		LsInfo info = f.listDirectory(pathDir);
		assertEquals(2, info.directories.length);
		assertEquals(2, info.files.length);
		assertTrue(Arrays.equals(info.path, pathDir));
	}

	@Test
	void list_inexistent_directory() {
		Filesystem f = new Filesystem();
		String[] pathDir = {"", "storage", "dir1"};
		assertNull(f.listDirectory(pathDir));
	}

	@Test
	void list_file() {
		Filesystem f = new Filesystem();
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, "storage"));
		String[] pathDir = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir));
		String[] pathF1 = {"", "storage", "dir1", "file1.asdf"};
		String[] pathF2 = {"", "storage", "dir1", "file2.asdf"};
		String[] pathD1 = {"", "storage", "dir1", "subdir1"};
		String[] pathD2 = {"", "storage", "dir1", "subdir2"};
		assertEquals(SUCCESS, f.createFile(pathF1, 2345));
		assertEquals(SUCCESS, f.createFile(pathF2, 3456));
		assertEquals(SUCCESS, f.createDirectory(pathD1));
		assertEquals(SUCCESS, f.createDirectory(pathD2));

		// listing file should be equivalent to listing parent dir.
		LsInfo info = f.listDirectory(pathF1);
		assertEquals(2, info.directories.length);
		assertEquals(2, info.files.length);
		assertTrue(Arrays.equals(info.path, pathDir));
	}

	@Test
	void directory_storageId_set_correctly() {
		Filesystem f = new Filesystem();
		String STORAGE_ID = "storageId1";
		String[] path = {"", "storage"};
		assertEquals(SUCCESS, f.createStorageDirectory(path, STORAGE_ID));
		String[] pathDir1 = {"", "storage", "dir1"};
		assertEquals(SUCCESS, f.createDirectory(pathDir1));
		String[] pathDir2 = {"", "storage", "dir1", "dir2"};
		assertEquals(SUCCESS, f.createDirectory(pathDir2));

		LsInfo info1 = f.listDirectory(pathDir1);
		assertTrue(STORAGE_ID.equals(info1.storageId));
		LsInfo info2 = f.listDirectory(pathDir2);
		assertTrue(STORAGE_ID.equals(info2.storageId));
	}
}
