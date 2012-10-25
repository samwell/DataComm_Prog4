import java.awt.BorderLayout;
import java.awt.Image;
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

   public void displayImage(byte[] fileData) throws IOException
   {
      Image image = null;
      ByteArrayInputStream bin = new ByteArrayInputStream(fileData);
      image = ImageIO.read(bin);
      JFrame frame = new JFrame();
      JLabel label = new JLabel(new ImageIcon(image));
      frame.getContentPane().add(label, BorderLayout.CENTER);
      frame.pack();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // (1)
      frame.setVisible(true);
   }

   private void run()
   {
      // TODO Auto-generated method stub

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
      }
   }
}
