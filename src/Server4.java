import java.io.FileInputStream;

/**
 * This program will be a one-shot server that serves image files to the
 * connected client. The process in which this is implemented will be in a
 * stop-and-wait process.
 * 
 * @author trulenjo
 */
public class Server4
{
   private RDT rdt = null;
   private FileInputStream inFile = null;

   public Server4(String inIpAddress, int inRcvPortNum, int inPeerRcvPortNum)
         throws Exception
   {
      rdt = new RDT(inIpAddress, inRcvPortNum, inPeerRcvPortNum);
      Thread.sleep(100); // Let RDT have time to come up
   }

   /**
    * Begins processing the request from the client. Also handles sending the
    * file to the client.
    */
   private void processFile()
   {
      String fileName = App.getRandomImgFile();

      safetySleep();

      if (fileName == null)
      {
         rdt.sendData(byteToArr(App.MSG_NO_IMG_FILE_AVAILABLE));
         System.exit(0);
      }

      rdt.sendData(concat(App.MSG_FILE_NAME, fileName.getBytes()));

      getInFile(fileName);

      int pass = 0;
      byte[] buffer = readFile(inFile);

      while (buffer != null)
      {
         if (pass == 1)
            safetySleep();

         rdt.sendData(concat(App.MSG_FILE_DATA, buffer));

         safetySleep();

         buffer = readFile(inFile);
      }

      rdt.sendData(byteToArr(App.MSG_FILE_DONE));
   }

   /**
    * Helper method to convert a byte to a byte array.
    * 
    * @param bt
    *           - byte to be converted
    * @return - byte array containing one element, bt
    */
   private byte[] byteToArr(byte bt)
   {
      byte[] tmp = new byte[1];
      tmp[0] = bt;

      return tmp;
   }

   /**
    * Initializes inFile variable to reduce code clutter.
    * 
    * @param fileName
    *           - String representing the name of the file
    */
   private void getInFile(String fileName)
   {
      try
      {
         inFile = new FileInputStream(App.IMG_SUBFOLDER + fileName);
      }
      catch (Exception e)
      {
         System.out.println("There was a problem - getInFile: " + e);
      }
   }

   /**
    * Concatenates byte and byte array together.
    * 
    * @param a
    *           - byte to go in front of array
    * @param b
    *           - array to follow byte
    * @return - resulting byte array
    */
   private byte[] concat(byte a, byte[] b)
   {
      byte[] tmp = new byte[1 + b.length];

      tmp[0] = a;

      for (int i = 1; i <= b.length; i++)
         tmp[i] = b[i - 1];

      return tmp;
   }

   /**
    * Reads the file based on the pass.
    * 
    * @param inFile
    *           - FileInputStream handle to the file.
    * @param pass
    *           - Used to determine the offset.
    * @return - byte array holding the contents of the file
    */
   private byte[] readFile(FileInputStream inFile)
   {
      byte[] tmp = new byte[App.MAX_DATA_SIZE];
      int valid = 0;

      try
      {
         valid = inFile.read(tmp, 0, App.MAX_DATA_SIZE);
      }
      catch (Exception e)
      {
         System.out.println("There was a problem - readFile: " + e);
      }

      if (valid == -1)
         return null;

      return tmp;
   }

   /**
    * Encapsulates the Thread.sleep function in a try catch loop to reduce
    * code clutter.
    */
   private void safetySleep()
   {
      try
      {
         Thread.sleep(100);
      }
      catch (Exception e)
      {
         System.out.println("There was a problem - safetySleep: " + e);
      }
   }

   /**
    * Combines all shutdown commands together.
    */
   private void shutdown()
   {
      try
      {
         inFile.close();

         Thread.sleep(2000); // Give protocols time to finish
         System.out.println("Server shutting down.");
         System.exit(0);
      }
      catch (Exception e)
      {
         System.out.println("There was a problem closing: " + e);
      }
   }

   private void run()
   {
      byte[] msg = rdt.receiveData();

      if (msg[0] == App.MSG_REQUEST_IMG_FILE)
      {
         processFile();
      }

      shutdown();
   }

   /**
    * Starting point for Server4
    * 
    * @param args
    *           - standard main method parameters
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 1)
         {
            new Server4("localhost", App.SERVER_RCV_PORT_NUM,
                  App.SERVER_PEER_RCV_PORT_NUM).run();
         }
         else
         {
            new Server4(args[0], App.SERVER_RCV_PORT_NUM,
                  App.SERVER_PEER_RCV_PORT_NUM).run();
         }

      }
      catch (Exception ex)
      {
         System.out.println("Error in Client, closing: " + ex.toString());
         ex.printStackTrace();
      }
   }
}