import java.io.*;
import java.net.*;
import java.util.Arrays;


class UDPClient{
	public static void main(String args[]) throws Exception{
		DatagramSocket clientSocket = new DatagramSocket();
		System.out.println("client port: "+clientSocket.getLocalPort());	//sourcePort
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		InetAddress IPAddress = InetAddress.getByName("localhost");

		boolean isConnected = false;

		String header;
		String[] tcpHeader;
		int syn, ack, fin;
		DatagramPacket sendPacket, receivePacket;
		syn = 1;
		ack = 0;
		fin = 0;
			
		header = "SEQN=1000,ACKN=2000,SYN="+syn+",ACK="+ack+",FIN="+fin+",WINSIZE=1000";
		sendData = header.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000);
		System.out.println("destination to port: "+sendPacket.getPort()); //destinationPort
		clientSocket.send(sendPacket);

		clientSocket.setSoTimeout(4000);	//4 sec timeout
		while(true){	//timeout and resend of 'TCP' packet
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try{
				clientSocket.receive(receivePacket);
				break;
			}catch(SocketTimeoutException e){
				//resend
				System.out.println("timeout!");
				clientSocket.send(sendPacket);
				continue;
			}
		}
		

		header = new String(receivePacket.getData());
		tcpHeader = header.split(",");
		syn = Integer.parseInt(tcpHeader[2].substring(tcpHeader[2].lastIndexOf("=")+1));
		ack = Integer.parseInt(tcpHeader[3].substring(tcpHeader[3].lastIndexOf("=")+1));
		fin = Integer.parseInt(tcpHeader[4].substring(tcpHeader[4].lastIndexOf("=")+1));

		Thread.sleep(2000);	//2 sec delay
		System.out.println("Received from server: "+syn+" ,"+ack+" ,"+fin);

		if(syn == 1 && ack == 1 && fin == 0){
			syn = 0;
			ack = 1;
			fin = 0;
			header = "SEQN=1000,ACKN=2000,SYN="+syn+",ACK="+ack+",FIN="+fin+",WINSIZE=1000";
			sendData = header.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000);
			clientSocket.send(sendPacket);
			isConnected = true;
		}

		if(isConnected){
			System.out.println("Connection Established!");
		}

		/*for(int i=0; i<10; i++){
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			

			System.out.println("Input message:");

			String header = "SEQN=1000,ACKN=2000,SYN=1,ACK=1,FIN=1,WINSIZE=1000";

			String sentence = inFromUser.readLine();
			//sendData = sentence.getBytes();
			sendData = header.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			clientSocket.receive(receivePacket);

			System.out.println("IP ADRESS: "+receivePacket.getAddress());
			System.out.println("DATA: "+receivePacket.getData());
			System.out.println("LENGTH: "+receivePacket.getLength());
			System.out.println("OFFSET: "+receivePacket.getOffset());
			System.out.println("PORT: "+receivePacket.getPort());
			System.out.println("USER PORT: "+sendPacket.getPort());
			System.out.println("LENGTH: "+receivePacket.getSocketAddress());

			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER: "+modifiedSentence);
		}*/
		clientSocket.close();

		
	}
}

/*
	Three way handshake connection - DONE
	Timeout and resend of TCP packet - DONE
	Window sliding mechanism, source and dest -
	Receiving later packets, only acknowledge what has been fully received - 
	Correct use of seq and ack numbers - 
	Fourway handshake disconnection -

	Dropping of packets -
	Latency - Done
*/