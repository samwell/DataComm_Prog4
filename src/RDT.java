import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RDT
{

   private InetAddress peerIpAddress;
   private int rcvPortNum;
   private int peerRcvPortNum;
   private boolean dataWaitingToBeSent = false;
   private boolean dataWasReceived = false;
   private byte dataToSend[];
   private byte dataReceived[];

   public RDT(String inPeerIP, int inRcvPortNum, int inPeerRcvPortNum)
         throws java.net.UnknownHostException
   {
      rcvPortNum = inRcvPortNum;
      peerRcvPortNum = inPeerRcvPortNum;
      peerIpAddress = InetAddress.getByName(inPeerIP);
      new Sender().start(); // ignore the warning this line gives
      new Receiver().start(); // ignore the warning this line gives
   }

   public void sendData(byte data[])
   {
      while (dataWaitingToBeSent)
      {
         Thread.yield(); // Wait until it's ready for the next packet
      }
      
      dataToSend = data;
      dataWaitingToBeSent = true;
   }

   public byte[] receiveData()
   {
      while(!dataWasReceived)
      {
         Thread.yield();
      }
      
      dataWasReceived = false;
      return dataReceived;
   }

   private class Receiver extends Thread
   {
      private DatagramSocket receiverSocket;
      private byte receiveData[] = new byte[2 * App.MAX_MSG_SIZE];
      private DatagramPacket receivePacket;

      @Override
      public void run()
      {
         try
         {
            receiverSocket = new DatagramSocket(rcvPortNum);
            System.out.println("Started Receiver.");
         }
         catch (Exception ex)
         {
            System.out.println("Can't start Receiver: " + ex.toString());
            System.exit(1);
         }

         while (true)
         {
            try
            {
               System.out.println("Receiver waiting for data from below.");
               
               receivePacket = new DatagramPacket(receiveData, receiveData.length);
               receiverSocket.receive(receivePacket);

               System.out.println("Receiver got data from below.");
               System.out.println("Rcv Data good - try to deliver.");
               
               dataReceived = new byte[receivePacket.getLength()];
               dataReceived = receivePacket.getData();

               dataWasReceived = true;
               
               while (dataWasReceived)
               {
                  Thread.yield();
               }
               
               System.out.println("Rcv Data delivered.");
            }
            catch (Exception ex)
            {
               System.out.println("Error in Recv loop: " + ex.toString());
            }
         }
      }
   } // end Receiver class

   private class Sender extends Thread
   {
      private DatagramSocket senderSocket;

      @Override
      public void run()
      {
         try
         {
            senderSocket = new DatagramSocket();
            System.out.println("Started Sender.");
         }
         catch (Exception ex)
         {
            System.out.println("Can't start Sender: " + ex.toString());
            System.exit(1);
         }
         
         while (true)
         {
            try
            {
               System.out.println("Sender waiting for Data to be sent.");
               
               while(!dataWaitingToBeSent)
               {
                  Thread.yield();
               }

               System.out.println("Sender got Data to be sent.");
               
               while(!sendPacket());
               
               dataWaitingToBeSent = false;
            }
            catch (Exception ex)
            {
               System.out.println("Error in Sender loop: " + ex.toString());
            }
         }
      }

      private boolean sendPacket()
      {
         try
         {
            DatagramPacket packet = new DatagramPacket(dataToSend, 
                    dataToSend.length, peerIpAddress, peerRcvPortNum);
            // DO: You write it! That is, Create a new DatagramPacket and 
            //     send it. You should know which byte array to use as well 
            //     as to which IP address and port number it should be sent. 
            senderSocket.send(packet);
            
            System.out.println( "Sender sent data" );
            return true;
         }
         catch (Exception ex)
         {
            System.out.println("Sender send packet failed: " + ex.toString());
            return false;
         }
      }
   }    // end Sender class
} // end RDT class