package nfs.shared;

import static nfs.shared.Path.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPath {
	@Test
	void valid_root_String() {
		assertTrue(isValidPath("/"));
		assertTrue(isValidPath(new String[] {""}));
	}

	@Test
	void valid_root_String_array() {
		assertTrue(isValidPath(new String[] {""}));
	}
}
