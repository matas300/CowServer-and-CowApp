package tcpserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCP_ServerThread extends Thread {
	private Socket clientSocket = null;

	public TCP_ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {

		ObjectInputStream inSock = null;
		ObjectOutputStream outSock = null;
		try {
			/* creazione stream di input e output su socket */
			try {
				inSock = new ObjectInputStream(clientSocket.getInputStream());
				outSock = new ObjectOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Problemi nella creazione degli stream su socket: " + e.getMessage());
				clientSocket.close();
				return;
			}

			while (true) {

				Request richiesta;
				/* ricevo dati */
				try {
					richiesta = (Request) inSock.readObject();
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

				if (richiesta.getKind().equalsIgnoreCase("DOWNLOAD")) {

					File fd = new File(System.getProperty("user.dir"));
					int result = 0, count = 0;
					for (String s : fd.list()) {
						count = 0;
						for (int i = 0; i < s.length(); i++)
							if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
								count++;
								if (count == 3) {
									result++;
									break;
								}
							}
					}

					if (result == 0)
						outSock.writeInt(-1);
					else
						outSock.writeInt(result);

					System.out.printf("%d\n", result);

					for (int j = 0; j < result; j++) {
						for (String s : fd.list()) {
							count = 0;
							File curr = new File(s);
							FileInputStream fis = new FileInputStream(curr);
							byte[] pacchetto = new byte[fis.available()];
							fis.read(pacchetto);
							String data = pacchetto.toString();
							for (int i = 0; i < data.length(); i++)
								if (data.charAt(i) >= 'A' && data.charAt(i) <= 'Z') {
									count++;
									if (count == 3) {
										outSock.writeUTF(s);
										outSock.write(pacchetto);
										outSock.flush();
										fis.close();

										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										break;
									}
								}

						}
					}
				} else if (richiesta.getKind().equalsIgnoreCase("LOGIN")) {

					outSock.writeBoolean(
							login(((LoginRequest) richiesta).getUsername(), ((LoginRequest) richiesta).getPassw()));

				} else if (richiesta.getKind().equalsIgnoreCase("TERM")) {
					System.out.println("ktnxbye\n");
					clientSocket.close();
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean login(String nomeUtente, String password) throws IOException {
		BufferedReader br = null;
		boolean result = false;
		try {
			br = new BufferedReader(new FileReader("login.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String riga;
		try {
			riga = br.readLine();
			while ((riga != null)) {
				String[] parti = riga.split("\t");
				if (parti[0].equals(nomeUtente))
					if (parti[1].equals(password))
						result = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}

		return result;

	}
}

/*
 * 
 * public static List<String> findCartelle(LocalDate start, LocalDate end) {
        File folder = new File("C:\Users\david\eclipse-workspace\Try\prova");
        List<String> list = Arrays.asList(folder.list());
        if (!folder.isDirectory()) {
            folder.mkdir();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = null;
        List<String>result= new ArrayList<String>();
        for (String s : list) {
            localDate = LocalDate.parse(s, formatter);
            if (localDate.isBefore(end.plusDays(1)) && localDate.isAfter(start.minusDays(1))) {
                result.add(s);
            }
        }
        return result;
    }
 * */
