package dev.software.textsecure;

import java.util.ArrayList;

import dev.sugarscope.server.Handler;
import dev.sugarscope.server.Peer;
import dev.sugarscope.server.ServerTCP;
import dev.sugarscope.transport.Packet;

public class PacketHandler extends Handler {
	

	@Override
	public void handleMessage(Packet request) {
		switch (request.getTag()) {
			case Tag.VERIFY_SEED:
				verifySeed(request.getData());
			break;
			case Tag.SIGN_UP:
				signUp(request.getData());
			break;
			case Tag.LOGIN:
				logIn(request.getData());
				retreivedMessage(request.getData());
			break;
			case Tag.SEND_MESSAGE:
				sendMessage(request.getData());
			break;
		}
		System.out.println(mPeer.getUniqCode()+": "+request.getTag());
	}
	
	private void verifySeed(Object[] data){
		final String seed = data[0].toString();
		Packet packet = new Packet(Tag.VERIFY_SEED);
		packet.setData(DatabaseHandler.getInstance().verifySeed(seed));
		response(packet);
	}
	
	private void signUp(Object[] data){
		final String seed = data[0].toString();
		final String imei = data[1].toString();
		final String phone = data[2].toString();
		DatabaseHandler.getInstance().signUp(seed, imei, phone);
		Packet packet = new Packet(Tag.SIGN_UP);
		response(packet);
	}
	
	private void logIn(Object[] data){
		final String phone = data[0].toString();
		mPeer.setUniqCode(phone);
	}
	
	private void retreivedMessage(Object[] data){
		final String phone = data[0].toString();
		ArrayList<String[]> messages = DatabaseHandler.getInstance().retrieveMessage(phone);
		Packet packet = new Packet(Tag.OBTAIN_SAVED_MESSAGE);
		packet.setData(messages.toArray());
		response(packet);
	}
	
	private void sendMessage(Object[] data){
		final String[] remitters = (String[]) data[0];
		final String content = (String) data[1];
		final byte[] image = (byte[]) data[2];
		for(String remitter : remitters){
			final Peer peer = ServerTCP.getPeer(remitter);
			if(peer == null){
				DatabaseHandler.getInstance().saveMessage(mPeer.getUniqCode(), remitter, content, null);
			}else{
				Packet packet = new Packet(Tag.SEND_MESSAGE);
				packet.setData(mPeer.getUniqCode(), content, image);
				peer.sendPackage(packet);
			}
		}
	}

}
