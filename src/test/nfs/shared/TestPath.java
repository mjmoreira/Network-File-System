package nfs.shared;

import static nfs.shared.Path.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPath {
	@Test
	void valid_root() {
		assertTrue(isValidPath("/"));
		assertTrue(isValidPath(new String[] {""}));
	}

	@Test
	void invalid_slashes() {
		assertFalse(isValidPath("//"));
		assertFalse(isValidPath("///"));
	}

	@Test
	void invalid_paths() {
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
	}

	@Test
	void valid_paths() {
		assertTrue(isValidPath("/"));
		assertTrue(isValidPath(new String[] {"", "a"}));
		assertTrue(isValidPath(new String[] {"", "b", "c"}));
		assertTrue(isValidPath("/a"));
		assertTrue(isValidPath("/a/"));
		assertTrue(isValidPath("/a/b"));
	}
}
