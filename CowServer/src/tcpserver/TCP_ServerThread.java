package tcpserver;

import security.RSA;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCP_ServerThread extends Thread {
	private Socket clientSocket = null;
	private static final String path = "C:\\Users\\matti\\Documents\\CowRepository"; // da cambiare pre settaggio pc o
																						// trovare un modo di
																						// passarglielo, però scomodo
																						// dato che sarebbe da scrivere

	public TCP_ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {

		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		try {
			/* creazione stream di input e output su socket */
			try {
				inSock = new DataInputStream(clientSocket.getInputStream());
				outSock = new DataOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Problemi nella creazione degli stream su socket: " + e.getMessage());
				clientSocket.close();
				return;
			}

			while (true) {

				String richiesta;
				/* ricevo dati */

				try {

					richiesta = inSock.readUTF();
					if (richiesta == null) {
						System.out.println("Problemi nella ricezione");
						clientSocket.close();
						return;
					}

				} catch (Exception e) {
					System.out.println("Problemi nella ricezione della richiesta: " + e.getMessage());
					clientSocket.close();
					return;
				}

				if (richiesta.equalsIgnoreCase("DOWNLOAD")) {
					LocalDate start = LocalDate.parse(inSock.readUTF());
					System.out.println("start " + start.toString());
					LocalDate end = LocalDate.parse(inSock.readUTF());
					System.out.println("end " + end.toString());
					int delay = inSock.readInt();
					System.out.println("delay " + delay);
					// delay

					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					List<File> directoriesInRange = filerDirectoryByDate(path, start, end);
					outSock.writeInt(directoriesInRange.size());
					System.out.println("ncartelle " + directoriesInRange.size());
					for (File f : directoriesInRange) {
						outSock.writeUTF(f.getName());
						System.out.println("cartella " + f.getName());
						// devo mandare quante foto ci sono dentro alla subdir.
						List<File> photos = photosInDir(f.getAbsolutePath());
						outSock.writeInt(photos.size());
						System.out.println("numero foto " + photos.size());
						for (File p : photos) {
							// invio il nome della photo
							outSock.writeUTF(p.getName());
							System.out.println("nome foto " + p.getName());
							boolean ancoraDaMandare = inSock.readBoolean();
							if (ancoraDaMandare) {
								FileInputStream fis = new FileInputStream(p);
								outSock.writeLong(p.length());
								trasferisci_foto(fis, outSock, p.length());
								outSock.flush();
								fis.close();
								if (inSock.readBoolean())
									System.out.println("scaricato!");
							}
						}
					}

				} else if (richiesta.equalsIgnoreCase("LOGIN")) {
					// Prima ricevo user e passw, caso
					// mai criptati by chiave pub e privata
					// poi effettuo il login
					// ricevo richiesta
					boolean logged = false;
					final RSA encryption = new RSA();
					// invio mia chiave pub
					byte[] serverE = encryption.getPublicKey().getE();
					byte[] serverN = encryption.getPublicKey().getN();
					outSock.writeInt(serverN.length); // N length
					outSock.write(serverE, 0, serverE.length);
					outSock.write(serverN, 0, serverN.length);
					outSock.flush();
					// ricevo user e passw cript
					String user = inSock.readUTF();
					String passw = inSock.readUTF();

					// decript e controllo che esista user e passw
					BufferedReader br = new BufferedReader(new FileReader(new File("cred.dll")));
					String linea;
					while ((linea = br.readLine()) != null && !logged) {
						String[] cred = linea.split(" ");

						if (cred[0].equals(new String(encryption.decrypt(user.getBytes())))
								&& cred[1].equals(new String(encryption.decrypt(passw.getBytes()))))
							logged = true;
					}
					br.close();
					// dico se è logged o no.

					outSock.writeBoolean(logged);
					outSock.flush();

				} else if (richiesta.equalsIgnoreCase("TERM")) {
					// qua termino la connessione con il client.
					System.out.println("ktnxbye\n");
					// clientSocket.close();

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// noi denominiamo le cartelle delle varie foto per giornata. uguale ad un
	// LocalDateTime
	private List<File> filerDirectoryByDate(String path, LocalDate start, LocalDate end) {
		List<File> result = new ArrayList<File>();
		File[] arrayFileNellaCartella = (new File(path)).listFiles();
		for (File f : arrayFileNellaCartella) {
			if (f.isDirectory()) {
				LocalDate ltd = LocalDate.parse(f.getName());
				if ((ltd.isEqual(start) || ltd.isAfter(start)) && (ltd.isEqual(end) || ltd.isBefore(end)))
					result.add(f);
			}
		}
		return result;
	}

	private List<File> photosInDir(String path) {
		return Arrays.asList((new File(path).listFiles()));

	}

	public static void trasferisci_foto(InputStream src, OutputStream dest, long lsrc) throws IOException {
		int buffer;
		try {
			for (long l = 0; l < lsrc; l++) {
				buffer = src.read();
				dest.write(buffer);
			}
			dest.flush();
		} catch (EOFException e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
		}
	}
}
