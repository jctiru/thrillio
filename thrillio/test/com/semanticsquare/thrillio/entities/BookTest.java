package com.semanticsquare.thrillio.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.managers.BookmarkManager;

class BookTest {

	@Test
	void testIsKidFriendlyEligible() {
		// Test 1: Philosophy genre should return false
		Book book = BookmarkManager.getInstance().createBook(4000, "Walden", 1854, "Wilder Publications",
				new String[] { "Henry David Thoreau" }, BookGenre.PHILOSOPHY, 4.3);
		boolean isKidEligible = book.isKidFriendlyEligible();
		assertFalse(isKidEligible, "For Philosophy Genre - isKidFriendlyEligible should return false");

		// Test 2: Self Help genre should return false
		book = BookmarkManager.getInstance().createBook(4000, "Walden", 1854, "Wilder Publications",
				new String[] { "Henry David Thoreau" }, BookGenre.SELF_HELP, 4.3);
		isKidEligible = book.isKidFriendlyEligible();
		assertFalse(isKidEligible, "For Self Help Genre - isKidFriendlyEligible should return false");
	}

}
