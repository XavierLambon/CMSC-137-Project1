import java.io.*;
import java.net.*;
import java.util.Arrays;

class UDPServer{
	public static void main(String args[] ) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(5000);
		System.out.println("server port: "+serverSocket.getLocalPort());
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		boolean isConnected = false;

		String header;
		String[] tcpHeader;
		int syn, ack, fin;
		DatagramPacket sendPacket, receivePacket;

		Arrays.fill(receiveData, (byte)0);	//resets the value of this variable
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		serverSocket.receive(receivePacket);
		header = new String(receivePacket.getData());
		tcpHeader = header.split(",");
		syn = Integer.parseInt(tcpHeader[2].substring(tcpHeader[2].lastIndexOf("=")+1));
		ack = Integer.parseInt(tcpHeader[3].substring(tcpHeader[3].lastIndexOf("=")+1));
		fin = Integer.parseInt(tcpHeader[4].substring(tcpHeader[4].lastIndexOf("=")+1));

		Thread.sleep(2000); //2 sec delay
		System.out.println("From Client: "+syn+" ,"+ack+" ,"+fin);

		if(syn == 1 && ack == 0 && fin == 0){
			System.out.println("yay!");
			syn = 1;
			ack = 1;
			fin = 0;
			header = "SEQN=1000,ACKN=2000,SYN="+syn+",ACK="+ack+",FIN="+fin+",WINSIZE=1000";
			sendData = header.getBytes();
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);


			serverSocket.setSoTimeout(4000); //4 secs timeout
			while(true){
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try{
					serverSocket.receive(receivePacket);
					break;
				}catch(SocketTimeoutException e){
					//resend
					System.out.println("timeout!");
					serverSocket.send(sendPacket);
					continue;
				}
			}

			
			
			header = new String(receivePacket.getData());
			tcpHeader = header.split(",");
			syn = Integer.parseInt(tcpHeader[2].substring(tcpHeader[2].lastIndexOf("=")+1));
			ack = Integer.parseInt(tcpHeader[3].substring(tcpHeader[3].lastIndexOf("=")+1));
			fin = Integer.parseInt(tcpHeader[4].substring(tcpHeader[4].lastIndexOf("=")+1));
			Thread.sleep(2000); //2 sec delay
			System.out.println("From Client again: "+syn+" ,"+ack+" ,"+fin);
			if(syn == 0 && ack == 1 && fin == 0){
				isConnected = true;
			}
		}

		if(isConnected){
			System.out.println("Connection Established!");
		}

		/*while(true){

			Arrays.fill(receiveData, (byte)0);	//resets the value of this variable
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			System.out.println("wow: "+new String(receiveData));

			serverSocket.receive(receivePacket);
			
			System.out.println("IP ADRESS: "+receivePacket.getAddress());
			System.out.println("DATA: "+receivePacket.getData());
			System.out.println("LENGTH: "+receivePacket.getLength());
			System.out.println("OFFSET: "+receivePacket.getOffset());
			System.out.println("PORT: "+receivePacket.getPort());
			System.out.println("LENGTH: "+receivePacket.getSocketAddress());



			String sentence = new String(receivePacket.getData());
			String[] sentenceArray = sentence.split(",");

			System.out.println("RECEIVED: "+sentence);
			InetAddress IPAdress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			System.out.println("MODIFIED SENTENCE: "+capitalizedSentence);
			
			//sendData = capitalizedSentence.getBytes();
			sendData = sentenceArray[1].getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAdress, port);

			serverSocket.send(sendPacket);
		}*/
	}
}