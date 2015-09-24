package simpledb.server;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import simpledb.buffer.BufferMgr;
import simpledb.file.FileMgr;
import simpledb.log.LogMgr;
import simpledb.metadata.MetadataMgr;
import simpledb.planner.BasicQueryPlanner;
import simpledb.planner.BasicUpdatePlanner;
import simpledb.planner.Planner;
import simpledb.planner.QueryPlanner;
import simpledb.planner.UpdatePlanner;
import simpledb.tx.Transaction;

/**
 * The class that provides system-wide static global values. These values must
 * be initialized by the method {@link #init(String) init} before use. The
 * methods {@link #initFileMgr(String) initFileMgr},
 * {@link #initFileAndLogMgr(String) initFileAndLogMgr},
 * {@link #initFileLogAndBufferMgr(String) initFileLogAndBufferMgr}, and
 * {@link #initMetadataMgr(boolean, Transaction) initMetadataMgr} provide
 * limited initialization, and are useful for debugging purposes.
 *
 * @author Edward Sciore
 */
public class SimpleDB {
	public static int BUFFER_SIZE = 8;

	public static String LOG_FILE = "simpledb.log";
	public static String CS4431_LOGS = "cs4432.log";

	private static FileMgr fm;
	private static BufferMgr bm;
	private static LogMgr logm;
	private static MetadataMgr mdm;

	// CS 4432 Project 1
	// Logger for log file output
	private static Logger logger;

	public static BufferMgr bufferMgr() {
		return bm;
	}

	// The following initialization methods are useful for
	// testing the lower-level components of the system
	// without having to initialize everything.

	public static FileMgr fileMgr() {
		return fm;
	}

	/**
	 * CS 4432 Project 1
	 *
	 * Returns the global SimpleDB file logger.
	 *
	 * @return a Logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Initializes the system. This method is called during system startup.
	 *
	 * @param dirname
	 *            the name of the database directory
	 */
	public static void init(String dirname, int bufferSelect) {
		initFileLogAndBufferMgr(dirname, bufferSelect);

		Transaction tx = new Transaction();
		boolean isnew = fm.isNew();
		if (isnew) {
			logger.log(Level.INFO, "Creating new database");
		} else {
			logger.log(Level.INFO, "Recovering existing database");
			tx.recover();
		}
		initMetadataMgr(isnew, tx);
		tx.commit();
	}

	/**
	 * Initializes the file and log managers.
	 *
	 * @param dirname
	 *            the name of the database directory
	 */
	public static void initFileAndLogMgr(String dirname) {
		initFileMgr(dirname);
		logm = new LogMgr(LOG_FILE);

		// CS 4432 Project 1
		// Added our own log file logging handlers
		try {
			FileHandler logFileHandler = new FileHandler(CS4431_LOGS);
			logFileHandler.setLevel(Level.ALL);

			SimpleFormatter simpleFormatter = new SimpleFormatter();
			logFileHandler.setFormatter(simpleFormatter);

			logger = Logger.getGlobal();
			logger.addHandler(logFileHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the file, log, and buffer managers.
	 *
	 * @param dirname
	 *            the name of the database directory
	 */
	public static void initFileLogAndBufferMgr(String dirname, int bufferSelect) {
		initFileAndLogMgr(dirname);
		bm = new BufferMgr(BUFFER_SIZE, bufferSelect);
	}

	/**
	 * Initializes only the file manager.
	 *
	 * @param dirname
	 *            the name of the database directory
	 */
	public static void initFileMgr(String dirname) {
		fm = new FileMgr(dirname);
	}

	/**
	 * Initializes metadata manager.
	 *
	 * @param isnew
	 *            an indication of whether a new database needs to be created.
	 * @param tx
	 *            the transaction performing the initialization
	 */
	public static void initMetadataMgr(boolean isnew, Transaction tx) {
		mdm = new MetadataMgr(isnew, tx);
	}

	public static LogMgr logMgr() {
		return logm;
	}

	public static MetadataMgr mdMgr() {
		return mdm;
	}

	/**
	 * Creates a planner for SQL commands. To change how the planner works,
	 * modify this method.
	 *
	 * @return the system's planner for SQL commands
	 */
	public static Planner planner() {
		QueryPlanner qplanner = new BasicQueryPlanner();
		UpdatePlanner uplanner = new BasicUpdatePlanner();
		return new Planner(qplanner, uplanner);
	}
}
