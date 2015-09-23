package simpledb.server;

import simpledb.remote.*;
import java.rmi.registry.*;

public class Startup {
   public static void main(String args[]) throws Exception {
      /*
       * 0 = basic
       * 1 = clock
       * 2 = lru
       */
      int bufferManager = 0;
      for (String s: args) {
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
