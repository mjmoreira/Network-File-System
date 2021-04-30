package nfs.filesystem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestDirectoryFile {
	// TODO: Escrever testes para o validador de nomes e dos throws dos construtores.

	// @Test
	// void directory_null_parent_exception() {
	// 	assertThrows(NullPointerException.class, () -> new Directory("name", null, ""));
	// }

	@Test
	void file_in_root_Path() {
		Directory r = Directory.createRootDirectory();
		File f = r.createChildFile("file1", 10);
		assertEquals("/file1", f.getCanonicalPathString());
	}

	@Test
	void file_in_height_1_directory() {
		Directory r = Directory.createRootDirectory();
		Directory d = r.createChildDirectory("dir1", "storage1");
		File f = d.createChildFile("file1", 10);
		assertEquals("/dir1/file1", f.getCanonicalPathString());
	}

	@Test
	void root_directory() {
		Directory d = Directory.createRootDirectory();
		assertEquals("/", d.getCanonicalPathString());
	}

	@Test
	void directory_with_height_1() {
		Directory r = Directory.createRootDirectory();
		Directory d1 = r.createChildDirectory("d1", "server1");
		assertEquals("/d1/", d1.getCanonicalPathString());
	}

	@Test
	void directory_with_height_1_stored_in_the_same_server_as_parent() {
		Directory r = Directory.createRootDirectory();
		Directory d = r.createChildDirectory("dir1");
		assertEquals("/dir1/", d.getCanonicalPathString());
	}
	
	@Test
	void file_with_height_4() {
		Directory r = Directory.createRootDirectory();
		Directory d1 = r.createChildDirectory("d1", "server1");
		Directory d2 = d1.createChildDirectory("d2", "server1");
		Directory d3 = d2.createChildDirectory("d3", "server1");
		File f1 = d3.createChildFile("f1", 10);
		assertEquals("/d1/d2/d3/f1", f1.getCanonicalPathString());
	}

	@Test
	void directory_create_child_directory() {
		Directory r = Directory.createRootDirectory();
		Directory c1 = r.createChildDirectory("c1");
		assertEquals("/c1/", c1.getCanonicalPathString());
	}

	@Test
	void directory_create_2_child_directory() {
		Directory r = Directory.createRootDirectory();
		Directory c1 = r.createChildDirectory("c1");
		Directory c2 = c1.createChildDirectory("c2");
		assertEquals("/c1/c2/", c2.getCanonicalPathString());
	}

	@Test
	void directory_create_2_child_directory_and_file() {
		Directory r = Directory.createRootDirectory();
		Directory c1 = r.createChildDirectory("c1");
		Directory c2 = c1.createChildDirectory("c2");
		File f1 = c2.createChildFile("f1.txt", 1000);
		assertEquals("/c1/c2/f1.txt", f1.getCanonicalPathString());
	}

	@Test
	void file_name_validator_good() {
		assertAll("good",
		          () -> assertTrue(File.validName("asdf")),
		          () -> assertTrue(File.validName("124.txt")),
		          () -> assertTrue(File.validName(".abc")),
		          () -> assertTrue(File.validName("asdf.")),
		          () -> assertTrue(File.validName(". asdf")),
		          () -> assertTrue(File.validName("asdf .")),
		          () -> assertTrue(File.validName(". asdf .")),
		          () -> assertTrue(File.validName(".asdf.")),
		          () -> assertTrue(File.validName("...")),
		          () -> assertTrue(File.validName("......."))
		         );
	}

	@Test
	void file_name_validator_bad() {
		assertAll("bad",
		          () -> assertFalse(File.validName("..")),
		          () -> assertFalse(File.validName(".")),
		          () -> assertFalse(File.validName(" ..")),
		          () -> assertFalse(File.validName(" .")),
		          () -> assertFalse(File.validName(".. ")),
		          () -> assertFalse(File.validName(". ")),
		          () -> assertFalse(File.validName(" .. ")),
		          () -> assertFalse(File.validName(" . ")),
		          () -> assertFalse(File.validName(" ..")),
		          () -> assertFalse(File.validName(" ")),
		          () -> assertFalse(File.validName("  ")),
		          () -> assertFalse(File.validName("")),
		          () -> assertFalse(File.validName("asdf-asdg")),
		          () -> assertFalse(File.validName(" valid.txt")),
		          () -> assertFalse(File.validName("valid.txt ")),
		          () -> assertFalse(File.validName(" valid.txt "))
		         );
	}

}
