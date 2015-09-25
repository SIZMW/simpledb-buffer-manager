package sqlclient.main;

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

/**
 * CS 4432 Project 1
 *
 * This class is used to parse our SQL file and execute queries and statements
 * on the SimpleDB database. This is our SQL application to create and run
 * queries on our database.
 *
 * @author Lambert Wang
 */
public class ExecuteSimpleDBSQL {
	private static Connection conn = null;

	/**
	 * Executes a query with the specified command.
	 *
	 * @param cmd
	 *            The command to execute as a query.
	 */
	protected static void doQuery(String cmd) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(cmd);
			ResultSetMetaData md = rs.getMetaData();
			int numcols = md.getColumnCount();
			int totalwidth = 0;
			
			// Print header
			for (int i = 1; i <= numcols; i++) {
				int width = md.getColumnDisplaySize(i);
				totalwidth += width;
				String fmt = "%" + width + "s";
				System.out.format(fmt, md.getColumnName(i));
			}
			System.out.println();
			for (int i = 0; i < totalwidth; i++) {
				System.out.print("-");
			}
			System.out.println();
			
			// Print records
			while (rs.next()) {
				for (int i = 1; i <= numcols; i++) {
					String fldname = md.getColumnName(i);
					int fldtype = md.getColumnType(i);
					String fmt = "%" + md.getColumnDisplaySize(i);
					if (fldtype == Types.INTEGER) {
						System.out.format(fmt + "d", rs.getInt(fldname));
					} else {
						System.out.format(fmt + "s", rs.getString(fldname));
					}
				}
				System.out.println();
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Executes an update statement with the specified command.
	 *
	 * @param cmd
	 *            The command to execute as an update.
	 */
	protected static void doUpdate(String cmd) {
		try {
			Statement stmt = conn.createStatement();
			int howmany = stmt.executeUpdate(cmd);
			System.out.println(howmany + " records processed");
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Main method. Takes in one argument for the SQL file to parse.
	 *
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				throw new Exception("Program requires file name as an argument.");
			}

			// Parse file name from command line arguments
			String file_name = args[0];

			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);

			// Read SQL queries from a file
			Reader rdr = new FileReader(file_name);
			BufferedReader br = new BufferedReader(rdr);

			String line = br.readLine();

			long totalStartTime = System.nanoTime();
			while (line != null) {
				line = line.trim();

				// Process one line of input
				if (line.startsWith("select")) {
					System.out.println("\n" + "\"" + line + "\"");
					//long startTime = System.nanoTime();
					doQuery(line);
					//long endTime = System.nanoTime();
					//System.out.println(String.format("Time elapsed: %.3f ms", ((float)(endTime - startTime))/1000000.0));
				} else if (line.startsWith("insert") || line.startsWith("create")) {
					System.out.println("\n" + "\"" + line + "\"");
					//long startTime = System.nanoTime();
					doUpdate(line);
					//long endTime = System.nanoTime();
					//System.out.println(String.format("Time elapsed: %.3f ms", ((float)(endTime - startTime))/1000000.0));
				}
				line = br.readLine();
			}

			long totalEndTime = System.nanoTime();
			System.out.println(String.format("Total Time elapsed: %.3f ms\n", ((float)(totalEndTime - totalStartTime))/1000000.0));
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}