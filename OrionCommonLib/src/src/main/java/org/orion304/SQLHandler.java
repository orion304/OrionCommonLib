package src.main.java.org.orion304;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import src.main.java.org.orion304.utils.ServerUtils;

public class SQLHandler {

	protected Connection connection;

	private final String centralJdbcUrl;

	private final String centralDbUsername;

	private final String centralDbpassword;

	/**
	 * Instantiates a new SQL Handler.
	 * 
	 * @throws ClassNotFoundException
	 */
	public SQLHandler(OrionPlugin plugin, String host, int port,
			String database, String username, String password) {
		this.centralJdbcUrl = "jdbc:mysql://" + host + ":" + port + "/"
				+ database + "?autoReconnect=true";
		this.centralDbUsername = username;
		this.centralDbpassword = password;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connect();

	}

	/**
	 * Connect to the database.
	 * 
	 * @return true, if successful
	 */
	public boolean connect() {
		if (this.connection != null) {
			try {
				if (this.connection.isValid(5)) {
					return true;
				}
			} catch (final SQLException e) {
				// This only throws an SQLException if the number input is less
				// than 0
			}
		}

		try {
			this.connection = DriverManager.getConnection(this.centralJdbcUrl,
					this.centralDbUsername, this.centralDbpassword);
			return true;
		} catch (final SQLException e) {
			ServerUtils.verbose("Failed to connect to the database!");
			e.printStackTrace();
			return false;
		}
	}

	public ResultSet get(PreparedStatement statement) throws SQLException {

		return statement.executeQuery();

	}

	public ResultSet get(String statement) throws SQLException {
		return get(getStatement(statement));
	}

	public PreparedStatement getStatement(String string) throws SQLException {

		return this.connection.prepareStatement(string);

	}

	public int update(PreparedStatement statement) throws SQLException {

		return statement.executeUpdate();

	}

	public int update(String statement) throws SQLException {
		return update(getStatement(statement));
	}

}
