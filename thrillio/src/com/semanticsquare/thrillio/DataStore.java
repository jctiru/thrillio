package com.semanticsquare.thrillio;

import java.util.ArrayList;
import java.util.List;

import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.constants.Gender;
import com.semanticsquare.thrillio.constants.MovieGenre;
import com.semanticsquare.thrillio.constants.UserType;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;
import com.semanticsquare.thrillio.util.IOUtil;

public class DataStore {
	public static List<User> users = new ArrayList<>();
	private static List<List<Bookmark>> bookmarks = new ArrayList<>();
	private static List<UserBookmark> userBookmarks = new ArrayList<>();

	public static List<User> getUsers() {
		return users;
	}

	public static List<List<Bookmark>> getBookmarks() {
		return bookmarks;
	}

	public static List<UserBookmark> getUserBookmarks() {
		return userBookmarks;
	}

	public static void loadData() {
		loadUsers();
		loadWebLinks();
		loadMovies();
		loadBooks();
	}

	private static void loadUsers() {
		List<String> data = new ArrayList<>();
		IOUtil.read(data, "User");

		for (String row : data) {
			String[] values = row.split("\t");
			Gender gender;

			switch (values[5]) {
			case ("m"):
				gender = Gender.MALE;
				break;
			case ("f"):
				gender = Gender.FEMALE;
			default:
				gender = Gender.TRANSGENDER;
				break;
			}

			User user = UserManager.getInstance().createUser(Long.parseLong(values[0]), values[1], values[2], values[3],
					values[4], gender, UserType.valueOf(values[6]));
			users.add(user);
		}
	}

	private static void loadWebLinks() {
		List<String> data = new ArrayList<>();
		IOUtil.read(data, "WebLink");
		List<Bookmark> bookmarkList = new ArrayList<>();

		for (String row : data) {
			String[] values = row.split("\t");
			Bookmark bookmark = BookmarkManager.getInstance().createWebLink(Long.parseLong(values[0]), values[1],
					values[2], values[3]/* , values[4] */);
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	private static void loadMovies() {
		List<String> data = new ArrayList<>();
		IOUtil.read(data, "Movie");
		List<Bookmark> bookmarkList = new ArrayList<>();

		for (String row : data) {
			String[] values = row.split("\t");
			String[] cast = values[3].split(",");
			String[] directors = values[4].split(",");
			Bookmark bookmark = BookmarkManager.getInstance().createMovie(Long.parseLong(values[0]), values[1], "",
					Integer.parseInt(values[2]), cast, directors, MovieGenre.valueOf(values[5]),
					Double.parseDouble(values[6]));
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	private static void loadBooks() {
		List<String> data = new ArrayList<>();
		IOUtil.read(data, "Book");
		List<Bookmark> bookmarkList = new ArrayList<>();

		for (String row : data) {
			String[] values = row.split("\t");
			String[] authors = values[4].split(",");
			Bookmark bookmark = BookmarkManager.getInstance().createBook(Long.parseLong(values[0]), values[1],
					Integer.parseInt(values[2]), values[3], authors, BookGenre.valueOf(values[5]),
					Double.parseDouble(values[6]));
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	public static void add(UserBookmark userBookmark) {
		userBookmarks.add(userBookmark);
	}
}
