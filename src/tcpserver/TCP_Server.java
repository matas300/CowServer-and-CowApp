package tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Server {
	public static final int PORT=3000;
	  

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket=null;
		Socket clientSocket=null;
		int port=-1;
	
	  	if ((args.length==0)) {
		  	port=PORT;
	  	} else if (args.length==1) {
		  	try {
				port=Integer.parseInt(args[0]);
			  	if (port<1024 || port>65535) {
				  	System.out.println("Numero di porta errato, 1024 < serverPort < 65535");
				  	System.exit(1);
			}
		} catch(NumberFormatException e) {
				System.out.println("Il numero della porta deve essere un intero");
			  	System.exit(1);
			}
		} else {
			System.out.println("Usare: java TCP_Server [serverPort]");
			System.exit(1);
	  	}
		

	  	try {
		  	serverSocket=new ServerSocket(port,5);
		  	serverSocket.setReuseAddress(true);
		  	System.out.println("Server: avviato...");
	  	} catch(Exception e) {
		  	System.out.println("Problemi nella creazione della server socket: "+ e.getMessage());
		  	System.exit(1);
	  	}

	  	try {
			while (true) {
			  	System.out.println("Server: in attesa di richieste...\n");
			  	
			  	try {
				  	clientSocket=serverSocket.accept();
				  	clientSocket.setSoTimeout(60000);
				  	System.out.println("Server: connessione accettata: " + clientSocket);
			  	} catch(Exception e) {
				  	System.out.println("Problemi nella accettazione della connessione: "+ e.getMessage());
				  	continue;
			  	}
			  
			  	try {
				  	new TCP_ServerThread(clientSocket).start();
			  	} catch (Exception e) {
				  	System.out.println("Problemi nel server thread: "+ e.getMessage());
				  	continue;
			  	}
		  	}
	  	} catch(Exception e) {
		  	e.printStackTrace();
		  	serverSocket.close();
		  	System.out.println("Server terminato...");
		  	System.exit(1);
	  	}
  	}
}

