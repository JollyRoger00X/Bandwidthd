package logReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class BandwidthdServer extends Thread implements Runnable {
	private int myPort,cnflen=14;
	private boolean shutdown = false;
	private File conf;
	private ServerSocket servSock;
	private String[] cnfdat;
	private String 
		dbURL =  "localhost",
		dbUser = "Shmo",
		dbPass = "Joe",
		dbName = "bandwidthdb",
		dbTable = "bd_log";

	public BandwidthdServer(File cnf){
		conf = cnf;
		System.out.println("A BandwidthdServer was initialized");
	}
	
	/**
	 * Starts the Client Handler thread 
	 */
	public void run(){
		
		if (!readConf() || !testPort()){
			System.err.println("bandwidthd server was unable to read its configuration file or bind to the specified port");
			return;
		}
		Socket link = null;
		String messageIn;
		int numrecords=0,writes=0;
		Scanner sc;

		while(!shutdown){
			try {
				//wait for a client to connect
				System.out.println("Awaiting Client");
				servSock = new ServerSocket(myPort);
				
				//Receives a client connection
				link = servSock.accept();
				
				BufferedReader in = 
						new BufferedReader(
								new InputStreamReader(
										link.getInputStream()));
				
				PrintWriter out = new PrintWriter(
						link.getOutputStream(), true);
				
				BdMysqlWriter wrt = null;
				Record head=null;
				Record nxt=null;
				
				messageIn = in.readLine();
				System.out.println("Bandwidthd Server recieved a message!");
				System.out.println(messageIn);
				
				out.println("Go Ahead");
				
				if(messageIn.equals("done")){
					continue;
				}
				else{
					sc = new Scanner(messageIn);
					numrecords = Integer.parseInt(sc.next());
					for(int i=0;i<numrecords;i++){
						messageIn = in.readLine();
						//diagnostic output
						System.out.println(messageIn);
						nxt = new Record(messageIn);
						nxt.setNext(head);
						head = nxt;
					}
					
					wrt = new BdMysqlWriter(dbURL,dbUser,dbPass,dbName,dbTable);
					wrt.writeToDB(head);
					writes =wrt.getNumWrites();
					wrt.clearWrites();
				}
				
				out.println(writes);
				System.out.println("Recipt message sent: "+writes+" records stored to database");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Unable to attach port");
				e.printStackTrace();
			}
			finally{
				try{
					System.out.println("\nClosing bd Server Connection... ");
					link.close();
					link = null;
					servSock.close();
					servSock = null;
					
				}
				catch(IOException e){
					System.out.println("Unable to disconnect!");
					System.exit(1);
				}
			}
			
			readConf();
			
		} //end server run loop
		
		
		resetConf();
	}
	
	private void resetConf(){
		try {
			PrintWriter fr= new PrintWriter(conf);
			int i;
			
			for(i=0;i<(cnflen-1);i++){
				fr.println(cnfdat[i]);
			}
			
			fr.println("GO");
			fr.close();
		} catch (IOException e) {
			System.err.println("Bandwidthd Server cannot write/reset configuration file!");
			e.printStackTrace();
		}			
	}
	
	private boolean readConf(){
		cnfdat = new String[cnflen];
		try {
			Scanner sc = new Scanner(conf);
			String data;
			
			
			data = sc.nextLine();//1 all odd number lines are disregarded
			cnfdat[0] = data;
		
			data = sc.nextLine();
			cnfdat[1] = data;
			myPort = Integer.parseInt(data);
			
			data = sc.nextLine();//3 all odd number lines are disregarded
			cnfdat[2] = data;
			
			data = sc.nextLine();
			dbURL = data;
			cnfdat[3] = data;
			
			data = sc.nextLine();//5 all odd number lines are disregarded
			cnfdat[4] = data;
			
			data = sc.nextLine();
			dbUser = data;
			cnfdat[5] = data;
			
			data = sc.nextLine();//7 all odd number lines are disregarded
			cnfdat[6] = data;
			
			data = sc.nextLine();
			dbPass = data;
			cnfdat[7] = data;
				
			data = sc.nextLine();//9 all odd number lines are disregarded
			cnfdat[8] = data;
			
			data = sc.nextLine();
			dbName = data;
			cnfdat[9] = data;
				
			data = sc.nextLine();//11 all odd number lines are disregarded
			cnfdat[10] = data;
			
			data = sc.nextLine();
			dbTable = data;
			cnfdat[11] = data;
			
			data = sc.nextLine();//13 all odd number lines are disregarded
			cnfdat[12] = data;
			
			data = sc.nextLine();
			cnfdat[13] = data;
			if(data.equals("stop")) shutdown = true;
			
			sc.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	private boolean testPort(){
		try {
			ServerSocket test = new ServerSocket(myPort);
			Thread.sleep(10);
			test.close();
			test = null;
			Thread.sleep(40);
			return true;
		} catch (IOException e){
			System.out.println("Port Could not be attached");
			return false;
		} catch (InterruptedException e){
			System.err.println("Bandwidthd Server thread interupted");
			return false;
		}
	}
	
	public static void main(String args[]){
		
		File conf = new File ("bdServer.conf");
		BandwidthdServer sv1 = new BandwidthdServer(conf);
		//sv1.setDaemon(true);
		//sv1.start();
		sv1.run();
	}
		
}
