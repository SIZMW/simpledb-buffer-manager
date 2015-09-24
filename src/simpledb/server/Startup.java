package simpledb.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import simpledb.remote.RemoteDriver;
import simpledb.remote.RemoteDriverImpl;

public class Startup {
	public static void main(String args[]) throws Exception {

		/**
		 * CS 4432 Project 1
		 *
		 * Command line arguments:
		 *
		 * -clock : triggers the clock policy for replacement
		 *
		 * -lru : triggers the LRU policy for replacement
		 *
		 * No arguments : triggers the basic buffer manager
		 *
		 * @author Lambert Wang
		 */
		int bufferManager = 0;
		for (String s : args) {
			if (s.equals("-clock")) {
				bufferManager = 1;
			} else if (s.equals("-lru")) {
				bufferManager = 2;
			}
		}

		// configure and initialize the database
		SimpleDB.init(args[0], bufferManager);

		// create a registry specific for the server on the default port
		Registry reg = LocateRegistry.createRegistry(1099);

		// and post the server entry in it
		RemoteDriver d = new RemoteDriverImpl();
		reg.rebind("simpledb", d);

		System.out.println("database server ready");
	}
}
