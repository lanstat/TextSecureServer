package dev.software.textsecure;


import dev.sugarscope.server.ServerTCP;

public class TextSecureServer {
	
	public static void main(String[] arg){
		try {
			DatabaseHandler.getInstance().init();
			ServerTCP server = new ServerTCP(6000);
			server.registerHandler(PacketHandler.class);
			server.start();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}
}
