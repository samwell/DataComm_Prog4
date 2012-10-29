import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Client4
{
   private RDT rdt;

   public Client4(String inIpAddress, int inRcvPortNum, int inPeerRcvPortNum)
         throws Exception
   {
      rdt = new RDT(inIpAddress, inRcvPortNum, inPeerRcvPortNum);
      Thread.sleep(100); // Let RDT have time to come up
   }

   public void displayImage(byte[] fileData, String fileName)
         throws IOException
   {
      ByteArrayInputStream bin = new ByteArrayInputStream(fileData);
      JFrame frame = new JFrame(fileName);
      JLabel label = new JLabel(new ImageIcon(ImageIO.read(bin)));
      frame.getContentPane().add(label, BorderLayout.CENTER);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      try
      {
         Thread.sleep(5000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }

      frame.setVisible(false);
      frame.setEnabled(false);
      
      System.exit(0);
   }

   private void run() throws IOException
   {
      byte[] sendData = new byte[1];
      byte[] receiveData = new byte[App.MAX_MSG_SIZE];
      byte[] fileName;
      sendData[0] = App.MSG_REQUEST_IMG_FILE;

      rdt.sendData(sendData);

      try
      {
         Thread.sleep(10);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }

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

      displayImage(file, new String(fileName));

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
      catch (Exception ex)
      {
         System.out.println("Error in Client, closing: " + ex.toString());
         ex.printStackTrace();
      }
   }
}
