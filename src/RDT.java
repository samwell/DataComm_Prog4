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
      // TODO: You write this one, similar to sendData.
      // Uses dataWasReceived and dataReceived.
      
      dataReceived = new byte[App.MAX_DATA_SIZE];

      // TODO: This was done, so compiler doesn't yell at me. Change this
      // when it comes time to implement it.
      return new byte[1];
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
               // TODO: Instantiate receivePacket and pass receiveData and
               // receiveData.length to the DatagramPacket constructor

               // TODO: Receive a a datagram packet into receivePacket

               System.out.println("Receiver got data from below.");
               System.out.println("Rcv Data good - try to deliver.");

               // TODO: Copy the data received in receivePacket into
               // dataReceived
               // Make sure you use receivePacket.getLength() to get exactly
               // the number of bytes received.

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

         // TODO: Make a senderSocket similar to how receiverSocket was
         // made in Receiver, except the DatagramSocket won't
         // take a port number parameter.

         while (true)
         {
            try
            {
               System.out.println("Sender waiting for Data to be sent.");

               // TODO: Do Thread.yield() until there is data waiting to be
               // sent.
               // Hint: use a while loop that loops until a certain class
               // variable is true. Thread.yield() will allow other Threads
               // to execute ahead of this one.

               System.out.println("Sender got Data to be sent.");

               while (!sendPacket())
                  ;

               // TODO: Set dataWaitingToBeSent appropriately. Think about it:
               // you just sent the data that was waiting to be sent, so
               // at this very instant, is data waiting to be sent?

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

            // TODO: You write it! That is, Create a new DatagramPacket and
            // send it. You should know which byte array to use as well
            // as to which IP address and port number it should be sent.

            System.out.println("Sender sent data");
            return true;
         }
         catch (Exception ex)
         {
            System.out.println("Sender send packet failed: " + ex.toString());
            return false;
         }
      }
   } // end Sender class
} // end RDT class