package dev.software.textsecure;

import dev.sugarscope.server.Handler;
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
			case Tag.OBTAIN_SAVED_MESSAGE:
				obtainSavedMessage(request.getData());
			break;
		}
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
	
	private void obtainSavedMessage(Object[] data){
		
	}
	
	/**
	 * 
	 * @param data
	 */
	private void sendMessage(Object[] data){
		
	}

}
