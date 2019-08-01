package com.semanticsquare.thrillio.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.semanticsquare.thrillio.DataStore;
import com.semanticsquare.thrillio.entities.Book;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.Movie;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.entities.WebLink;

public class BookmarkDao {
	public List<List<Bookmark>> getBookmarks() {
		return DataStore.getBookmarks();
	}

	public void saveUserBookmark(UserBookmark userBookmark) {
		// try-with-resources ==> conn & stmt will be closed
		// Connection string: <protocol>:<sub-protocol>:<data-source details>
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/thrillio?useSSL=false", "root",
				"root")) {
			if (userBookmark.getBookmark() instanceof Book) {
				saveUserBook(userBookmark, conn);
			} else if (userBookmark.getBookmark() instanceof Movie) {
				saveUserMovie(userBookmark, conn);
			} else {
				saveUserWebLink(userBookmark, conn);
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			// e.printStackTrace();
			System.out.println("Duplicate bookmark!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveUserBook(UserBookmark userBookmark, Connection conn) throws SQLException {
		String query = "INSERT INTO User_Book (user_id, book_id) VALUES (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, userBookmark.getUser().getId());
			stmt.setLong(2, userBookmark.getBookmark().getId());
			stmt.executeUpdate();
		}
	}

	private void saveUserMovie(UserBookmark userBookmark, Connection conn) throws SQLException {
		String query = "INSERT INTO User_Movie (user_id, movie_id) VALUES (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, userBookmark.getUser().getId());
			stmt.setLong(2, userBookmark.getBookmark().getId());
			stmt.executeUpdate();
		}
	}

	private void saveUserWebLink(UserBookmark userBookmark, Connection conn) throws SQLException {
		String query = "INSERT INTO User_Weblink (user_id, weblink_id) VALUES (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, userBookmark.getUser().getId());
			stmt.setLong(2, userBookmark.getBookmark().getId());
			stmt.executeUpdate();
		}
	}

	// In real application, we would have SQL or hibernate queries
	public List<WebLink> getAllWebLinks() {
		List<WebLink> result = new ArrayList<>();
		List<List<Bookmark>> bookmarks = DataStore.getBookmarks();

		if (!bookmarks.isEmpty()) {
			List<Bookmark> allWebLinks = bookmarks.get(0);

			for (Bookmark bookmark : allWebLinks) {
				result.add((WebLink) bookmark);
			}
		}

		return result;
	}

	public List<WebLink> getWebLinks(WebLink.DownloadStatus downloadStatus) {
		List<WebLink> result = new ArrayList<>();
		List<WebLink> allWebLinks = getAllWebLinks();

		for (WebLink webLink : allWebLinks) {
			if (webLink.getDownloadStatus().equals(downloadStatus)) {
				result.add(webLink);
			}
		}

		return result;
	}
}
