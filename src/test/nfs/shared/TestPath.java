package nfs.shared;

import static nfs.shared.Path.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class TestPath {
	@Test
	void valid_root() {
		assertTrue(isValidPath("/"));
		assertTrue(isValidPath(new String[] {""}));
	}

	@Test
	void invalid_paths() {
		assertFalse(isValidPath("//"));
		assertFalse(isValidPath("///"));
		assertFalse(isValidPath("/// "));
		assertFalse(isValidPath(""));
		assertFalse(isValidPath(new String[] {"", ""}));
		assertFalse(isValidPath(new String[] {"a", ""}));
		assertFalse(isValidPath(new String[] {"a", "b"}));
		assertFalse(isValidPath(new String[] {"a"}));
		assertFalse(isValidPath(new String[] {"", "/"}));
		assertFalse(isValidPath(new String[] {"", "?"}));
		assertFalse(isValidPath("*"));
		assertFalse(isValidPath("asdf"));
		assertFalse(isValidPath("a/b"));
		assertFalse(isValidPath("/a//"));
		assertFalse(isValidPath("/as/as/b//a"));
		assertFalse(isValidPath("a/b"));
		assertFalse(isValidPath("/ as/a"));
		assertFalse(isValidPath("/as/ as/b/a"));
		assertFalse(isValidPath("/as/as/b/a "));
		assertFalse(isValidPath(" /as/as/b/a"));
	}

	@Test
	void valid_paths() {
		assertTrue(isValidPath("/"));
		assertTrue(isValidPath(new String[] {"", "a"}));
		assertTrue(isValidPath(new String[] {"", "b", "c"}));
		assertTrue(isValidPath("/a"));
		assertTrue(isValidPath("/a/"));
		assertTrue(isValidPath("/a/b"));
		assertTrue(isValidPath("/a/b/c/"));
	}

	@Test
	void convert_path_array_to_string() {
		assertTrue("/a/".equals(convertPath(new String[] {"", "a"})));
		assertTrue("/b/c/".equals(convertPath(new String[] {"", "b", "c"})));
		assertTrue("//".equals(convertPath(new String[] {"", ""})));
		assertTrue("/".equals(convertPath(new String[] {""})));
		assertTrue("/a///".equals(convertPath(new String[] {"", "a", "", ""})));
		assertTrue("/a///b/".equals(convertPath(new String[] {"", "a", "", "", "b"})));
	}

	@Test
	void convert_path_string_to_array() {
		assertEquals(0, Arrays.compare(new String[] {"", "a"}, convertPath("/a")));
		assertEquals(0, Arrays.compare(new String[] {"", "b", "c"}, convertPath("/b/c/")));
		assertEquals(0, Arrays.compare(new String[] {"", "", "c"}, convertPath("//c/")));
		assertEquals(0, Arrays.compare(new String[] {"", "", "c"}, convertPath("//c")));
		assertEquals(0, Arrays.compare(new String[] {"a", "b", "c"}, convertPath("a/b/c/")));
		assertEquals(0, Arrays.compare(new String[] {"a", "b", "c"}, convertPath("a/b/c")));
		assertEquals(0, Arrays.compare(new String[] {"a", "", "c"}, convertPath("a//c")));
	}
}
