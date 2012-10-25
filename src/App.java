public class App
{
   // Application Message Types

   /*
    * Sent from the Client, has no data Starts the session--says that Client
    * wants an image.
    */
   public static final byte MSG_REQUEST_IMG_FILE = 1;

   /*
    * Sent from the Server, has file name as the data--NOT null-terminated.
    * The Client could perhaps make an output file using this name. The data
    * from MSG_FILE_DATA segments will be written to this file. Either way,
    * your Client program must print the name of the file it's downloading.
    */
   public static final byte MSG_FILE_NAME = 2;

   /*
    * Sent from the Server, has 1 to 2048 bytes of file data
    */
   public static final byte MSG_FILE_DATA = 3;

   /*
    * Sent from the Server, has 1 to 2048 bytes of file data Indicates the
    * last piece of file data was sent. Server shuts down, Client displays the
    * received file and shuts down.
    */
   public static final byte MSG_FILE_DONE = 4;

   /*
    * Indicates the Server had no image files in the Imgs folder. Both sides
    * will print appropriate messages and shut down.
    */
   public static final byte MSG_NO_IMG_FILE_AVAILABLE = 5;

   // Application message maximum sizes
   public static final int MAX_DATA_SIZE = 2048;
   public static final int MAX_MSG_SIZE = MAX_DATA_SIZE + 1;

   // Recevier port numbers
   public static int CLIENT_RCV_PORT_NUM = 5281;
   public static int CLIENT_PEER_RCV_PORT_NUM = 7281;
   public static int SERVER_RCV_PORT_NUM = 7281;
   public static int SERVER_PEER_RCV_PORT_NUM = 5281;

   // Img subfolder
   public static String IMG_SUBFOLDER = ".\\Imgs\\";

   /**
    * Returns a random image file name from IMG_SUBFOLDER. If there are no
    * image files in Imgs, null is returned. The file name doesn't have the
    * path.
    * 
    * @return a random image filename or null.
    */
   public static String getRandomImgFile()
   {
      java.io.File dir = new java.io.File(IMG_SUBFOLDER);
      String entireFileList[] = dir.list();
      String imgFileList[] = new String[entireFileList.length];
      int imgCount = 0;

      for (int i = 0; i < entireFileList.length; i++)
      {
         String fileName = entireFileList[i].toLowerCase();
         if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
               || fileName.endsWith(".gif") || fileName.endsWith(".png"))
         {
            imgFileList[imgCount++] = entireFileList[i];
         }
      }

      if (imgCount == 0)
         return null;
      int index = new java.util.Random().nextInt(imgCount);
      return imgFileList[index];
   }
}