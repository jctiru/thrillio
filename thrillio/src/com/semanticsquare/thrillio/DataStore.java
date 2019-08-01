package com.semanticsquare.thrillio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// new com.mysql.cj.jdbc.Driver();
			// or
			// System.setProperty("jdbc.drivers", "com.mysql.cd.jdbc.Driver");
			// or java.sql.DriverManager
			// DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// try-with-resources ==> conn & stmt will be closed
		// Connection string: <protocol>:<sub-protocol>:<data-source details>
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/thrillio?useSSL=false", "root",
				"root"); Statement stmt = conn.createStatement()) {
			loadUsers(stmt);
			loadWebLinks(stmt);
			loadMovies(stmt);
			loadBooks(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void loadUsers(Statement stmt) throws SQLException {
		String query = "SELECT * FROM User";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			long id = rs.getLong("id");
			String email = rs.getString("email");
			String password = rs.getString("password");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			int gender_id = rs.getInt("gender_id");
			Gender gender = Gender.values()[gender_id];
			int user_type_id = rs.getInt("user_type_id");
			UserType userType = UserType.values()[user_type_id];

			User user = UserManager.getInstance().createUser(id, email, password, firstName, lastName, gender,
					userType);
			users.add(user);
		}
	}

	private static void loadWebLinks(Statement stmt) throws SQLException {
		String query = "SELECT * FROM WebLink";
		ResultSet rs = stmt.executeQuery(query);
		List<Bookmark> bookmarkList = new ArrayList<>();

		while (rs.next()) {
			long id = rs.getLong("id");
			String title = rs.getString("title");
			String url = rs.getString("url");
			String host = rs.getString("host");

			Bookmark bookmark = BookmarkManager.getInstance().createWebLink(id, title, url, host);
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	private static void loadMovies(Statement stmt) throws SQLException {
//		String query = "SELECT m.id, title, release_year, GROUP_CONCAT(a.name SEPARATOR ',') AS cast, GROUP_CONCAT(d.name SEPARATOR ',') AS directors, movie_genre_id, imdb_rating"
//				+ " FROM Movie m, Actor a, Movie_Actor ma, Director d, Movie_Director md"
//				+ " WHERE m.id = ma.movie_id AND ma.actor_id = a.id AND m.id = md.movie_id AND md.director_id = md.id"
//				+ " GROUP BY m.id";
		String query = "SELECT movie.id, movie.title, release_year, GROUP_CONCAT(distinct t1.actors) AS cast, GROUP_CONCAT(DISTINCT t2.directors) AS directors, movie_genre_id, imdb_rating "
				+ " FROM movie"
				+ " INNER JOIN (SELECT movie.id AS movie_id, actor.name AS actors FROM movie, actor, movie_actor WHERE movie.id = movie_actor.movie_id AND movie_actor.actor_id = actor.id) AS t1 ON movie.id = t1.movie_id"
				+ " INNER JOIN (SELECT  movie.id AS movie_id, director.name AS directors FROM movie, director, movie_director WHERE movie.id = movie_director.movie_id AND movie_director.director_id = director.id) AS t2 ON movie.id = t2.movie_id"
				+ " GROUP BY movie.id";
		ResultSet rs = stmt.executeQuery(query);
		List<Bookmark> bookmarkList = new ArrayList<>();

		while (rs.next()) {
			long id = rs.getLong("id");
			String title = rs.getString("title");
			int releaseYear = rs.getInt("release_year");
			String[] cast = rs.getString("cast").split(",");
			String[] directors = rs.getString("directors").split(",");
			int genre_id = rs.getInt("movie_genre_id");
			MovieGenre genre = MovieGenre.values()[genre_id];
			double imdbRating = rs.getDouble("imdb_rating");

			Bookmark bookmark = BookmarkManager.getInstance().createMovie(id, title, "", releaseYear, cast, directors,
					genre, imdbRating);
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	private static void loadBooks(Statement stmt) throws SQLException {
		String query = "SELECT b.id, title, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, amazon_rating, created_date"
				+ " FROM Book b, Publisher p, Author a, Book_Author ba"
				+ " WHERE b.publisher_id = p.id and b.id = ba.book_id and ba.author_id = a.id" + " GROUP BY b.id";
		ResultSet rs = stmt.executeQuery(query);
		List<Bookmark> bookmarkList = new ArrayList<>();

		while (rs.next()) {
			long id = rs.getLong("id");
			String title = rs.getString("title");
			int publicationYear = rs.getInt("publication_year");
			String publisher = rs.getString("name");
			String[] authors = rs.getString("authors").split(",");
			int genre_id = rs.getInt("book_genre_id");
			BookGenre genre = BookGenre.values()[genre_id];
			double amazonRating = rs.getDouble("amazon_rating");

			Date createdDate = rs.getDate("created_date");
			System.out.println("createdDate: " + createdDate);
			Timestamp timeStamp = rs.getTimestamp(8); // (created_date)
			System.out.println("timeStamp: " + timeStamp);
			System.out.println("localDateTime: " + timeStamp.toLocalDateTime());

			System.out.println("id: " + id + ", title: " + title + ", publication year: " + publicationYear
					+ ", publisher: " + publisher + ", authors: " + String.join(", ", authors) + ", genre: " + genre
					+ ", amazonRating: " + amazonRating);

			Bookmark bookmark = BookmarkManager.getInstance().createBook(id, title, publicationYear, publisher, authors,
					genre, amazonRating);
			bookmarkList.add(bookmark);
		}

		bookmarks.add(bookmarkList);
	}

	public static void add(UserBookmark userBookmark) {
		userBookmarks.add(userBookmark);
	}
}
