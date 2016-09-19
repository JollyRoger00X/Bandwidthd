package logReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
//import java.net.UnknownHostException;
import java.util.Scanner;

public class BandwidthdClient extends Thread implements Runnable {
	private InetAddress host;
	private int myPort;
	private String serverIp;
	private String confFileName = "bdClient.conf";
	private File confFile;
	private String logDir = "/usr/local/bandwidthd/";
	private int delay = 20000;
	private int recvFilter=512,sentFilter=2048;
	private boolean stop = false;
	private int conflen = 12;
	private String[] confdat;

	
	public BandwidthdClient(){
		confFile = new File(confFileName);
	}
	
	public void run(){
		Socket link = null;
		int numRecords;
		do{
			//load configuration from file
			loadConf();
			if(stop){
				System.out.println("BD Client was config stopped!");
				stop = true;
				resetConf();
				return;
			}
			BdLogReader rd = new BdLogReader(logDir);
			
			if(rd.read(recvFilter,sentFilter)){
				numRecords = rd.getNumRecords();
				try{
					System.out.println("Starting into connection");
					//Scanner user = new Scanner(System.in);
					host=InetAddress.getByName(serverIp);
					//host=InetAddress.getLocalHost();
					System.out.println("host found");
					link = new Socket(host,myPort);
					System.out.println("link established");
					BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
					
					PrintWriter out = new PrintWriter(link.getOutputStream(),true);
					
					String message="",responce="";
					Record current = rd.getHead();
					
					message = ""+numRecords;
					out.println(message);
					
					responce = in.readLine();
					System.out.println(responce);
					if(responce.equals("Go Ahead")){
						for(int i=0;i<numRecords;i++){
							message = current.toString();
							out.println(message);
							current = current.getNext();
						}
						
						//did the data actually get written to the db?
						responce = in.readLine();
						int recipt = Integer.parseInt(responce);
						
						//if so delete the original logs
						System.out.println(responce);
						if(recipt == numRecords)
							rd.deleteLogs();
					}

				}catch(IOException e){
					e.printStackTrace();
				}
				finally{
					try{
						System.out.println("\nClosing BandwidthdClient Connection... ");
						link.close();
					}catch(IOException e){
						System.err.println("BandwidthdClient experienced an IOException");
						System.exit(1);
						
					}
				}
			}
			else{
				System.out.println("");
			}
			
			rd.clearHead();
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.err.println("BandwidthdClient was rudely interupted while sleeping");
				e.printStackTrace();
			}
		}while(!stop);	
	}
	
	private void resetConf(){
		int i=0;
		try {
			PrintWriter pr = new PrintWriter(confFile);
			for(i=0;i<(conflen-1);i++)
				pr.println(confdat[i]);
			
			
			pr.println("GO");
			
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads every even numbered line of its config file to allow the odd lines to be used as headers
	 * @param config the config file
	 */
	private void loadConf(){
		
		Scanner sc;
		try {
			sc = new Scanner(confFile);
			
			confdat = new String[conflen];
			String data;
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[0] = data;
			//System.out.println(data);
			
			data = sc.nextLine(); //bandwidthd directory
			logDir = data;
			confdat[1] = data;
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[2] = data;
			
			data = sc.nextLine(); //recv filter
			recvFilter = Integer.parseInt(data);
			confdat[3] = data;
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[4] = data;
			
			data = sc.nextLine();//sent filter
			confdat[5] = data;
			sentFilter = Integer.parseInt(data);	
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[6] = data;
			
			data = sc.nextLine();//server's ip address
			confdat[7]=data;
			serverIp = data;
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[8] = data;
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[9] = data;
			myPort = Integer.parseInt(data);
			
			data = sc.nextLine();//all odd number lines are disregarded
			confdat[10] = data;
			
			data = sc.nextLine();//used to stop the daemon
			confdat[11] = data;
			
			if(data.equals("stop")) stop = true;
			sc.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		BandwidthdClient bdct = new BandwidthdClient();
		//bdct.setDaemon(true);
		bdct.run();
	}
}