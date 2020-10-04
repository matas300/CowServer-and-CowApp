package tcpclient;

import security.RSA;
import security.RSA.PublicKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP_Client {

    public static String TCP_send(String serverIP, int serverPort, Request request) {
        InetAddress addr = null;
        int port = -1;
        boolean login = false;
        boolean download = false;
        if (!serverIP.isEmpty() && serverPort > 0) {
            try {
                addr = InetAddress.getByName(serverIP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return "ErrIp";
            }
            port = serverPort;
            if (port < 1024 || port > 65535) {
                System.out.println("Numero di porta errato, 1024 < serverPort < 65535");
                return "ErrPort";
            }

        } else {
            System.out.println("Usare: java TCP_Client serverIP serverPort");
            return "ErrIpPort";
        }

        Socket socket = null;
        DataInputStream inSock = null;
        DataOutputStream outSock = null;

        try {
            socket = new Socket(addr, port);
            System.out.println("Connessione avviata...\n");
        } catch (Exception e) {
            System.out.println("Problemi nella creazione della socket: " + e.getMessage());
            return "ErrCreazSocket";
        }

        /* creazione stream di input e output su socket */
        try {
            outSock = new DataOutputStream(socket.getOutputStream());
            inSock = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Problemi nella creazione degli stream su socket: " + e.getMessage());
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return "ErrCreazStreamInOut";
        }

        try {

            if (request.getKind().equalsIgnoreCase("LOGIN")) {

                final RSA encryption = new RSA();
                // avviso che voglio fare il login
                outSock.writeUTF("LOGIN");
                // devo ricevere chiave pub server.
                byte[] serverE = new byte[1];
                byte[] serverN = new byte[inSock.readInt()];

                // Read Server's Public Key to encrypt Client's Message.
                inSock.read(serverE);
                inSock.read(serverN);

                PublicKey pkServer = new PublicKey(serverE, serverN);
                // cript user e passw
                byte[] username = encryption.encrypt(((LoginRequest) request).getUsername().getBytes(), pkServer);
                byte[] passw = encryption.encrypt(((LoginRequest) request).getPassw().getBytes(), pkServer);
                // send user e passw cript
                System.out.println(username);
                outSock.writeUTF(new String(username));
                outSock.writeUTF(new String(passw));
                // attendo risposta login eff
                System.out.println("In attesa di risposta...\n");
                login = inSock.readBoolean();
                if (login)
                    System.out.println("LOGGED");
                else
                    System.out.println("NOT LOGGED");
                outSock.flush();

            } else if (request.getKind().equalsIgnoreCase("DOWNLOAD")) {
                outSock.writeUTF("DOWNLOAD");
                // outSock.writeObject(request);
                System.out.println("In attesa di risposte...\n");
                outSock.writeUTF(((DownloadRequest) request).getStart().toString());
                outSock.writeUTF(((DownloadRequest) request).getEnd().toString());
                outSock.writeInt(((DownloadRequest) request).getDelay());
                // Mi mandano quante cartelle devo scaricare

                int nCartelle = inSock.readInt();
                if (nCartelle == -1) {
                    System.out.printf("Nessuna foto da scaricare.\n");
                } else {
                    for (int i = 0; i < nCartelle; ++i) {

                        String nomeCartella = inSock.readUTF();

                        File folder = new File(((DownloadRequest) request).getPathDir() + "\\" + nomeCartella);
                        // Verifichiamo che non sia già esistente come cartella
                        if (!folder.isDirectory()) {
                            // In caso non sia già presente, la creiamo
                            folder.mkdir();
                        }

                        // ora ho la cartella. chiedo quanti file devo scaricare
                        // mi mandano quanti file sto per scaricare
                        int nFotoDaScaricare = inSock.readInt();
                        if (nFotoDaScaricare == -1)
                            System.out.printf("Nessuna foto da scaricare.\n");
                        else {
                            for (int j = 0; j < nFotoDaScaricare; ++j) {
                                String filename = inSock.readUTF();
                                System.out.printf("Sto per scaricare il file %s.\n", filename);
                                File curr = new File(folder.getAbsoluteFile() + "\\" + filename);
                                if (!curr.isFile()) {
                                    outSock.writeBoolean(true);
                                    curr.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(curr);
                                    long length = inSock.readLong();
                                    trasferisci_foto(inSock, fos, length);
                                    fos.close();
                                    outSock.writeBoolean(true);
                                } else
                                    outSock.writeBoolean(false);
                                System.out.printf("Ho scaricato il file %s\n", filename);
                            }

                        }
                    }
                    System.out.println("Downloads terminati.\n");
                    download = true;
                }

            }
            System.out.println("Termino...\n");
            outSock.writeUTF("TERM");
            System.out.println("\nClient terminato...");
            socket.close();

            if (login)
                return "LOGGED";
            if (download)
                return "DOWNLOADED";
        } catch (Exception e) {
            System.out.println("Client crashed!!");
            return "ErrClientCrashed";
        }
        return "SmtgWentWrong";
    }

    public static void trasferisci_foto(InputStream src, OutputStream dest, long lsrc)
            throws IOException {
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
