package logReader;

import java.util.Scanner;

public class Record {
	private String ipAddress;
	private int 
	timestamp,
	sent_total,
	sent_icmp,
	sent_udp,
	sent_tcp,
	sent_ftp,
	sent_http,
	sent_p2p,
	rec_total,
	rec_icmp,
	rec_udp,
	rec_tcp,
	rec_ftp,
	rec_http,
	rec_p2p;
	private Record next = null;

	
	public Record(String cdfFormat){
		int len = cdfFormat.length();
		int i,loc=0;
		char ch;
		
		//each bd record has 16 pieces of data
		String[] temp= new String[16];
		for(i=0;i<temp.length;i++) temp[i]="";
		
		//cuts the single string into its 16 components and removes delimiter ","
		for(i=0;i<len;i++){
			ch = cdfFormat.charAt(i);
			if(ch == ',') loc++;
			else temp[loc]+= ch;
		}
		
		
		ipAddress = temp[0];
		timestamp = Integer.parseInt(temp[1]);
		sent_total= Integer.parseInt(temp[2]);
		sent_icmp= Integer.parseInt(temp[3]);
		sent_udp= Integer.parseInt(temp[4]);
		sent_tcp= Integer.parseInt(temp[5]);
		sent_ftp= Integer.parseInt(temp[6]);
		sent_http= Integer.parseInt(temp[7]);
		sent_p2p= Integer.parseInt(temp[8]);
		rec_total= Integer.parseInt(temp[9]);
		rec_icmp= Integer.parseInt(temp[10]);
		rec_udp= Integer.parseInt(temp[11]);
		rec_tcp= Integer.parseInt(temp[12]);
		rec_ftp= Integer.parseInt(temp[13]);
		rec_http= Integer.parseInt(temp[14]);
		rec_p2p= Integer.parseInt(temp[15]);
		
	}
	
	public Record(String ipAddress, int timestamp, int sent_total,
			int sent_icmp, int sent_udp, int sent_tcp, int sent_ftp,
			int sent_http, int sent_p2p, int rec_total, int rec_icmp,
			int rec_udp, int rec_tcp, int rec_ftp, int rec_http, int rec_p2p) {
		super();
		this.ipAddress = ipAddress;
		this.timestamp = timestamp;
		this.sent_total = sent_total;
		this.sent_icmp = sent_icmp;
		this.sent_udp = sent_udp;
		this.sent_tcp = sent_tcp;
		this.sent_ftp = sent_ftp;
		this.sent_http = sent_http;
		this.sent_p2p = sent_p2p;
		this.rec_total = rec_total;
		this.rec_icmp = rec_icmp;
		this.rec_udp = rec_udp;
		this.rec_tcp = rec_tcp;
		this.rec_ftp = rec_ftp;
		this.rec_http = rec_http;
		this.rec_p2p = rec_p2p;
	}

	public Record bdRecord(String in){
		Scanner sc = new Scanner(in);
		ipAddress = sc.next();
		timestamp = Integer.parseInt(sc.next());
		sent_total= Integer.parseInt(sc.next());
		sent_icmp= Integer.parseInt(sc.next());
		sent_udp= Integer.parseInt(sc.next());
		sent_tcp= Integer.parseInt(sc.next());
		sent_ftp= Integer.parseInt(sc.next());
		sent_http= Integer.parseInt(sc.next());
		sent_p2p= Integer.parseInt(sc.next());
		rec_total= Integer.parseInt(sc.next());
		rec_icmp= Integer.parseInt(sc.next());
		rec_udp= Integer.parseInt(sc.next());
		rec_tcp= Integer.parseInt(sc.next());
		rec_ftp= Integer.parseInt(sc.next());
		rec_http= Integer.parseInt(sc.next());
		rec_p2p= Integer.parseInt(sc.next());
		sc.close();
		Record out = new Record(ipAddress,timestamp,sent_total,sent_icmp,sent_udp,sent_tcp,sent_ftp,sent_http,
				sent_p2p,rec_total,rec_icmp,rec_udp,rec_tcp,rec_ftp,rec_http,rec_p2p);
		
		return out;
	}
	
	public String toString(){
		return ipAddress+","+timestamp+","
				+sent_total+","+sent_icmp+","+sent_udp+","+sent_tcp+","+sent_ftp+","+sent_http+","+sent_p2p+","
				+rec_total+","+rec_icmp+","+rec_udp+","+rec_tcp+","+rec_ftp+","+rec_http+","+rec_p2p;
	}
	
	public Record getNext(){
		return next;
	}
	
	public void setNext(Record in){
		next = in;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public int getSent_total() {
		return sent_total;
	}

	public int getSent_icmp() {
		return sent_icmp;
	}

	public int getSent_udp() {
		return sent_udp;
	}

	public int getSent_tcp() {
		return sent_tcp;
	}

	public int getSent_ftp() {
		return sent_ftp;
	}

	public int getSent_http() {
		return sent_http;
	}

	public int getSent_p2p() {
		return sent_p2p;
	}

	public int getRec_total() {
		return rec_total;
	}

	public int getRec_icmp() {
		return rec_icmp;
	}

	public int getRec_udp() {
		return rec_udp;
	}

	public int getRec_tcp() {
		return rec_tcp;
	}

	public int getRec_ftp() {
		return rec_ftp;
	}

	public int getRec_http() {
		return rec_http;
	}

	public int getRec_p2p() {
		return rec_p2p;
	}
}
