import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A client which downloads an image file from a server and then display that
 * file in a pop-up window.
 * 
 * @author jacobs
 * 
 */
public class Client4
{
   private RDT rdt;

   /**
    * Constructor: Initializes the rdt for the client using the ip address and
    * the port numbers.
    * 
    * @param inIpAddress
    *           The IP Address which the client connects to.
    * @param inRcvPortNum
    *           The port number the server is listening to.
    * @param inPeerRcvPortNum
    *           The port number the client downloads from.
    * @throws InterruptedException
    * @throws UnknownHostException
    * @throws Exception
    */
   public Client4(String inIpAddress, int inRcvPortNum, int inPeerRcvPortNum)
         throws UnknownHostException, InterruptedException
   {
      rdt = new RDT(inIpAddress, inRcvPortNum, inPeerRcvPortNum);
      Thread.sleep(100);
   }

   /**
    * Displays the image which was downloaded.
    * 
    * @param fileData
    *           The image to be displayed.
    * @param fileName
    *           The file name, to be set as the window title.
    * @throws IOException
    * @throws InterruptedException
    */
   public void displayImage(byte[] fileData, byte[] fileName)
         throws IOException, InterruptedException
   {
      ByteArrayInputStream bin = new ByteArrayInputStream(fileData);
      JFrame frame = new JFrame(new String(fileName));
      JLabel label = new JLabel(new ImageIcon(ImageIO.read(bin)));
      frame.getContentPane().add(label, BorderLayout.CENTER);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      Thread.sleep(5000);

      frame.setVisible(false);
      frame.setEnabled(false);

      System.exit(0);
   }

   /**
    * An image request is sent here.
    */
   private void sendFileRequest()
   {
      byte[] sendData = new byte[1];

      sendData[0] = App.MSG_REQUEST_IMG_FILE;

      rdt.sendData(sendData);
   }

   /**
    * The file name is retrieved.
    * 
    * @return fileName, a byte array containing the file name.
    */
   private byte[] getFileName()
   {
      byte[] fileName;
      byte[] receiveData = new byte[App.MAX_MSG_SIZE];

      receiveData = rdt.receiveData();
      fileName = new byte[receiveData.length - 1];

      if (receiveData[0] == App.MSG_FILE_NAME)
      {
         for (int i = 1; i < receiveData.length; i++)
            fileName[i - 1] = receiveData[i];

         System.out.println("File was found. Sending: "
               + new String(fileName));
      }
      else if (receiveData[0] == App.MSG_NO_IMG_FILE_AVAILABLE)
      {
         System.out.println("No file was found");
         System.exit(0);
      }

      return fileName;
   }

   /**
    * The file is retrieved here.
    * 
    * @return file, a byte array which contains the image.
    */
   private byte[] getFile()
   {
      byte[] receiveData = new byte[App.MAX_MSG_SIZE];
      byte[] file = new byte[App.MAX_DATA_SIZE];
      int filePointer = 0;

      receiveData = rdt.receiveData();

      if (receiveData[0] == App.MSG_FILE_DATA)
      {
         while (receiveData[0] != App.MSG_FILE_DONE)
         {
            for (int i = 1; i < App.MAX_MSG_SIZE; i++)
            {
               if (filePointer >= file.length - 1)
               {
                  byte[] tempFile = file;
                  file = new byte[file.length + App.MAX_DATA_SIZE];

                  System.arraycopy(tempFile, 0, file, 0, tempFile.length);
               }

               file[filePointer++] = receiveData[i];
            }

            receiveData = rdt.receiveData();
         }
      }

      return file;
   }

   /**
    * Executes and handles displaying the image.
    * 
    * @throws IOException
    * @throws InterruptedException
    */
   private void run() throws IOException, InterruptedException
   {
      sendFileRequest();

      byte[] fileName = getFileName();
      byte[] file = getFile();

      displayImage(file, fileName);
   }

   public static void main(String args[])
   {
      try
      {
         if (args.length != 1)
         {
            new Client4("localhost", App.CLIENT_RCV_PORT_NUM,
                  App.CLIENT_PEER_RCV_PORT_NUM).run();
         }
         else
         {
            new Client4(args[0], App.CLIENT_RCV_PORT_NUM,
                  App.CLIENT_PEER_RCV_PORT_NUM).run();
         }
      }
      catch (UnknownHostException e)
      {
         System.out.println("Unidentifable host or port was sent.");
         e.printStackTrace();
      }
      catch (IOException e)
      {
         System.out.println("Error displaying the image.");
         e.printStackTrace();
      }
      catch (InterruptedException e)
      {
         System.out.println("Error while timing out.");
         e.printStackTrace();
      }
      catch (Exception e)
      {
         System.out.println("Error in Client.");
         e.printStackTrace();
      }
   }
}
