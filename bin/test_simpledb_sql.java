import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
/*
 * Some code borrowed from SQLInterpreter.java in studentClient/simpledb
 *
 * Usage: java test_simpledb_sql {file_name}
 */
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import simpledb.remote.SimpleDriver;

public class test_simpledb_sql {
	private static Connection conn = null;

	private static void doQuery(String cmd) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(cmd);
			ResultSetMetaData md = rs.getMetaData();
			int numcols = md.getColumnCount();
			int totalwidth = 0;

			// print header
			for (int i = 1; i <= numcols; i++) {
				int width = md.getColumnDisplaySize(i);
				totalwidth += width;
				String fmt = "%" + width + "s";
				System.out.format(fmt, md.getColumnName(i));
			}
			System.out.println();
			for (int i = 0; i < totalwidth; i++)
				System.out.print("-");
			System.out.println();

			// print records
			while (rs.next()) {
				for (int i = 1; i <= numcols; i++) {
					String fldname = md.getColumnName(i);
					int fldtype = md.getColumnType(i);
					String fmt = "%" + md.getColumnDisplaySize(i);
					if (fldtype == Types.INTEGER)
						System.out.format(fmt + "d", rs.getInt(fldname));
					else
						System.out.format(fmt + "s", rs.getString(fldname));
				}
				System.out.println();
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void doUpdate(String cmd) {
		try {
			Statement stmt = conn.createStatement();
			int howmany = stmt.executeUpdate(cmd);
			System.out.println(howmany + " records processed");
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				throw new Exception("Program requires file name as an argument.");
			}
			// parse file name from command line arguments
			String file_name = args[0];

			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);

			// Read sql queries from a file
			Reader rdr = new FileReader(file_name);
			BufferedReader br = new BufferedReader(rdr);

			String line = br.readLine();

			while (line != null) {
				line = line.trim();
				// process one line of input
				System.out.println("\n" + "\"" + line + "\"");
				if (line.startsWith("select")) {
					doQuery(line);
				} else if (line.startsWith("insert") || line.startsWith("create")) {
					doUpdate(line);
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}