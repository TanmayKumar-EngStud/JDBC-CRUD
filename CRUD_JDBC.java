import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("unused")
class Mysql {
	private String url = "jdbc:mysql://localhost/";
	private String user = "";
	private String pass = "";
	private static String dbName = "";
	private static Connection con;

	Mysql(String user, String pass, String dbName) {
		try {
			url = url + dbName;
			this.user = user;
			this.pass = pass;
			Mysql.dbName = dbName;
			// no need of class.forName() if the JDBC is called externally in the system.
			// then there is no need.
			Mysql.con = DriverManager.getConnection(url, user, pass);
			if (con != null) {
				System.out.println("connected successfully!");
			}
		} catch (Exception e) {
			System.out.println("Error (Connection): " + e.getMessage());
		}
	}

	private static HashMap<String, String> getSchema(String tableName) {
		String sql = "SELECT COLUMN_NAME,DATA_TYPE from INFORMATION_SCHEMA.COLUMNS where table_schema= '" + dbName
				+ "' and table_name = '" + tableName + "';";
		HashMap<String, String> datatype = new HashMap<String, String>();
		try {
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery(sql);

			while (result.next()) {
				String[] val = { result.getString(1), result.getString(2) };
				datatype.put(val[0], val[1]);
			}
		} catch (Exception e) {
			System.out.println("Error (Schema): " + e.getMessage());
		}
		return datatype;
	}

	// Here is the Code for "Create"
	public void insert(String tableNAME, String[] values) {
		// Converting HashMap into 2D String array

		HashMap<String, String> map = getSchema(tableNAME);
		String[][] datatype = new String[map.size()][2];
		Set<Entry<String, String>> entries = map.entrySet();
		Iterator<Entry<String, String>> entriesIterator = entries.iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {

			Entry<String, String> mapping = entriesIterator.next();

			datatype[i][0] = mapping.getKey().toString();
			datatype[i][1] = mapping.getValue().toString();

			i++;
		}

		if (datatype.length != values.length) {
			System.out.println("Error the total number of values given are invalid");
			return;
		}
		String sql = "INSERT INTO " + tableNAME + "( ";
		for (i = 0; i < datatype.length; i++) {
			sql += datatype[i][0];
			if (i != values.length - 1) {
				sql += ", ";
			}
		}
		sql += ") values(";
		for (i = 0; i < values.length; i++) {
			sql = sql + "?";
			if (i != values.length - 1) {
				sql = sql + ",";
			}
		}
		sql += ");";
		System.out.println("query is: " + sql);
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			for (i = 0; i < values.length; i++) {
				if (datatype[i][1].equals("varchar")) {
					stmt.setString(i + 1, values[i]);
				} else if (datatype[i][1].equals("int")) {
					stmt.setInt(i + 1, Integer.parseInt(values[i]));
				}
			}

			int rowsInserted = stmt.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("Row is inserted");
			}
		} catch (Exception e) {
			System.out.println("Error (insertion): " + e.getMessage());
		}
	}

	// Code for read
	public ArrayList<String[]> read(String tabName, String ColName, String val) {
		HashMap<String, String> map = getSchema(tabName);
		String[][] datatype = new String[map.size()][2];
		Set<Entry<String, String>> entries = map.entrySet();
		Iterator<Entry<String, String>> entriesIterator = entries.iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {

			Entry<String, String> mapping = entriesIterator.next();

			datatype[i][0] = mapping.getKey().toString();
			datatype[i][1] = mapping.getValue().toString();

			i++;
		}
		int length = map.size();
		String sql = "SELECT * FROM " + tabName + " WHERE " + ColName + "= '" + val + "';";
		System.out.println("Query in read :- " + sql);
		ArrayList<String[]> ret = new ArrayList<String[]>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int count = 0;
			String[] temp = new String[length];

			while (rs.next()) {
				for (i = 0; i < length; i++) {
					if (datatype[i][1].equals("varchar")) {
						temp[i] = rs.getString(datatype[i][0]);
					} else if (datatype[i][1].equals("int")) {
						temp[i] = Integer.toString(rs.getInt(datatype[i][0]));
					}
				}
				ret.add(temp);
				temp = new String[length];
			}
			return ret;

		} catch (Exception e) {
			System.out.println("Error (read): " + e.getMessage());
		}
		return ret;

	}

	// Code for update
	public void update(String tabName, String[] colName, String[] val, String where, String value) {
		if (colName.length != val.length) {
			System.out.println("The length of both the strings colName, val are not same.");
			return;
		}
		String sql = "UPDATE " + tabName + " SET ";
		for (int i = 0; i < colName.length; i++) {
			sql += colName[i] + "='" + val[i] + "'";
			if (i != colName.length - 1) {
				sql += ", ";
			}
		}
		HashMap<String, String> datatype = getSchema(tabName);

		if (datatype.get(where).equals("varchar")) {
			sql += " WHERE " + where + "='" + value + "' ;";
		} else if (datatype.get(where).equals("int")) {
			sql += " WHERE " + where + "=" + value + " ;";
		}
//		System.out.println("SQL Statement:\n"+sql);
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			int ret = stmt.executeUpdate();
			if (ret != 0) {
				System.out.println("The data has been Updated from the table successfully.");
			}
		} catch (Exception e) {
			System.out.println("Error(Update) : " + e.getMessage());
		}
	}

	// Code for delete
	public void delete(String tabName, String colName, String val) {
		String sql = "DELETE FROM " + tabName + " WHERE " + colName + "= '" + val + "';";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			int ret = stmt.executeUpdate();
			if (ret != 0) {
				System.out.println("The data has been Deleted from the table successfully.");
			}
		} catch (Exception e) {
			System.out.println("Error(Removal) : " + e.getMessage());
		}
	}

	// Code for deleteLike
	public void deleteLike(String tabName, String colName, String val) {
		String sql = "DELETE FROM " + tabName + " WHERE " + colName + " LIKE '" + val + "';";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			int ret = stmt.executeUpdate();
			if (ret != 0) {
				System.out.println("The data has been Deleted from the table successfully.");
			}
		} catch (Exception e) {
			System.out.println("Error(Removal Like) : " + e.getMessage());
		}
	}
}

public class CRUD_JDBC {
	public static void main(String[] args) {
		// Give the username and password like this.
		Mysql mysql = new Mysql("tanmay", "tanmayDB", "test");

		// Now here are all the CRUD operations...

		// 1. Insertion
		String[] values = { "7", "Tanmay", "Horror", "2005", "A002", "Night of the living dead" };
		// Here is one issue though, it's about how you are going to create the String
		// list.
		// You have to check it manually, like what is the order in which the
		// INFORMATION SCHEMA is giving the result.
		// run this command in sql :
		// SELECT COLUMN_NAME, DATA_TYPE from INFORMATION_SCHEMA.COLUMNS where
		// table_schema = 'test' and table_name = 'movies';

		mysql.insert("movies", values);

		// 2. Delete
		String[] KeyValue = { "movies", "director", "Tanmay" };
		mysql.delete(KeyValue[0], KeyValue[1], KeyValue[2]);

		// 3. like removal
		// This might be handy sometimes.
		String[] KeyVal = { "movies", "director", "Tanmay" };
		mysql.deleteLike(KeyVal[0], KeyVal[1], KeyVal[2]);

		// 4. Update
		String[] colName = { "title", "genre", "director", "release_year" };
		String[] Val = { "Perfect Blue", "Thriller/Mistery", "Satoshi Kon", "1998" };
		String col = "sno";
		String val = "6";
		mysql.update(KeyVal[0], colName, Val, col, val);

		// 5. Read
		String colNAME = "release_year";
		String colVal = "2000";
		String tabName = "movies";
		ArrayList<String[]> readVal = mysql.read(tabName, colNAME, colVal);

		// So, you guys can easily use the above Class that I have created, it is now
		// easy to use for small projects.
	}
}

// SQL commands...

/*
 * Here are all the table creation commands CREATE TABLE movies( sno INT
 * AUTO_INCREMENT, movieID VARCHAR(5), title VARCHAR(50) NOT NULL, genre
 * VARCHAR(30) NOT NULL, director VARCHAR(60) NOT NULL, release_year VARCHAR(50)
 * NOT NULL, PRIMARY KEY(sno) ); To check the INFROMATION SCHEMA OF column in
 * the database SELECT COLUMN_NAME, DATA_TYPE from INFORMATION_SCHEMA.COLUMNS
 * where table_schema = 'test' and table_name = 'movies';
 */

/*
 * DELETE FROM movies; INSERT INTO movies(sno, movieID, title, genre, director,
 * release_year) VALUES (1, "A001", "Requiem for a Dream",
 * "Psychological/Horror", "Darren Aronofsky", "2000"), (2, "A002",
 * "American Psycho", "Psychological", "Mary Harron", "2000"), (3, "A003",
 * "Leaving Las Vegas", "Drama/Romance", "Mike Figgis", "1995"), (4, "A004",
 * "Se7en", "Crime/Mystery", "David Fincher", "1995"), (5, "A005",
 * "The Silence of the Lambs","Thriller/Horror", "Jonathan Demme", "1991"), (6,
 * "A006", "Fight Club", "David Fincher","Drama/Thriller","1999");
 */