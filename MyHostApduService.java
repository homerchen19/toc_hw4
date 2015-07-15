package hce_demo;

import java.util.Arrays;

import android.R.bool;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
 
public class MyHostApduService extends HostApduService {
	static byte walletBalance = 0;

	private static final byte[] AID_SELECT_APDU = {
			(byte) 0x00, // CLA (class of command)
			(byte) 0xA4, // INS (instruction); A4 = select
			(byte) 0x04, // P1  (parameter 1)  (0x04: select by name)
			(byte) 0x00, // P2  (parameter 2)
			(byte) 0x07, // LC  (length of data)  
			(byte) 0xF0, (byte) 0x39, (byte) 0x41, (byte) 0x48, (byte) 0x14, (byte) 0x81, (byte) 0x00,
			(byte) 0x00 // LE   (max length of expected result, 0 implies 256)
	};
	
	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		String inboundApduDescription;
		boolean alreadyVeriftPIN = false;
		byte[] responseApdu;

		if (Arrays.equals(AID_SELECT_APDU, apdu)) {
			inboundApduDescription = "Application selected";
			Log.i("HCEDEMO", inboundApduDescription);
			byte[] answer = new byte[2];
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectPersoApdu(apdu)) {      //perso
			Log.i("HCEDEMO", "Perso selected");
			byte[] answer = new byte[3];
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;			
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectVerifyPINApdu(apdu)) {  //verifyPIN
			Log.i("HCEDEMO", "VerifyPIN selected");
			byte[] answer = new byte[3];
			if(apdu[4] != (byte) 0x08) { //PIN code verifies fail
				answer[0] = (byte) 0x90;
				answer[1] = (byte) 0x02;
			}
			else { //PIN code verifies success
				answer[0] = (byte) 0x90;
				answer[1] = (byte) 0x00;	
			}
			responseApdu = answer;
			alreadyVeriftPIN = true;
			return responseApdu;
		}
		
		else if(selectChangePINApdu(apdu) && alreadyVeriftPIN) {  //changePIN
			Log.i("HCEDEMO", "ChangePIN selected");
			byte[] answer = new byte[3];
			if(apdu[4] < (byte) 0x08) { //PIN code length too short
				answer[0] = (byte) 0x0a;
				answer[1] = (byte) 0x02;
			}
			else if(apdu[4] > (byte) 0x08) { //PIN code length too long
				answer[0] = (byte) 0x0a;
				answer[1] = (byte) 0x03;
			}
			else if(apdu[4] == (byte) 0x08){ //PIN code verifies success
				answer[0] = (byte) 0x90;
				answer[1] = (byte) 0x00;	
			}
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectWriteDataApdu(apdu) && alreadyVeriftPIN) {  //writeData
			Log.i("HCEDEMO", "WriteData selected");
			byte[] answer = new byte[3];
			if(apdu[4] > (byte) 0xff || apdu[4] < (byte) 0x00) { //URL write fail (no available space)
				answer[0] = (byte) 0x01;
				answer[1] = (byte) 0x01;
			}
			else { //URL write success 
				answer[0] = (byte) 0x90;
				answer[1] = (byte) 0x00;	
			}
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectReadDataApdu(apdu) && alreadyVeriftPIN) {  //readData
			Log.i("HCEDEMO", "ReadData selected");
			byte[] answer = new byte[3];
			if(apdu[4] != (byte) 0x0a) { //URL read fail (not found match SID)
				answer[0] = (byte) 0x01;
				answer[1] = (byte) 0x02;
			}
			else { //URL write success 
				answer[0] = (byte) 0x90;
				answer[1] = (byte) 0x00;	
			}
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectRSAencryptApdu(apdu)) {  //RSAencrypt
			Log.i("HCEDEMO", "RSAencrypt selected");
			byte[] answer = new byte[3];
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;	
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectRSAdecryptApdu(apdu)) {  //RSAdecrypt
			Log.i("HCEDEMO", "RSAdecrypt selected");
			byte[] answer = new byte[3];
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;	
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectRSAsignApdu(apdu)) {  //RSAsign
			Log.i("HCEDEMO", "RSAsign selected");
			byte[] answer = new byte[3];
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;	
			responseApdu = answer;
			return responseApdu;
		}
		
		else if(selectRSAsignVerifyApdu(apdu)) {  //RSAsignVerify
			Log.i("HCEDEMO", "RSAsignVerify selected");
			byte[] answer = new byte[3]; 
			answer[0] = (byte) 0x90;
			answer[1] = (byte) 0x00;	
			responseApdu = answer;
			return responseApdu;
		}
				
		else {
			Log.i("HCEDEMO", "Unknown command");
			byte[] answer = new byte[2];
			answer[0] = (byte) 0x6F;
			answer[1] = (byte) 0x00;
			responseApdu = answer;
			return responseApdu;
		}
	}
	
	private boolean selectPersoApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x0b,  // INS
//		(byte) 0x00,  // P1
//		(byte) --,  // P2		
		if(apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x0b 
				&& apdu[2] == (byte) 0x00) {
			switch(apdu[3])
			{
				case 0x00:
					if(apdu[4] == (byte) 0x0a) //10
						return true;
				case 0x01:
					if(apdu[4] == (byte) 0x0c) //12
						return true;
				case 0x02:
					if(apdu[4] == (byte) 0x0c) //12
						return true;
				case 0x03:
					if(apdu[4] == (byte) 0x06) //6
						return true;
				case 0x04:
					if(apdu[4] == (byte) 0x06) //6
						return true;
				case 0x05:
					if(apdu[4] == (byte) 0x0c) //12
						return true;
				case 0x06:
					if(apdu[4] == (byte) 0x0c) //12
						return true;
				case 0x07:
					if(apdu[4] == (byte) 0x0c) //12
						return true;
				case 0x08:
					if(apdu[4] == (byte) 0x40) //64
						return true;
				case 0x09:
					if(apdu[4] == (byte) 0x40) //64
						return true;
				case 0x0a:
					if(apdu[4] == (byte) 0x40) //64
						return true;
				case 0x0b:
					if(apdu[4] == (byte) 0x08) //8
						return true;
				case 0x0c:
					if(apdu[4] == (byte) 0x0a) //10
						return true;
			}
		}
		return false;
	}
	
	private boolean selectVerifyPINApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x09,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x09
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectChangePINApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x0a,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x0a 
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectWriteDataApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x01,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x01 
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectReadDataApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x01,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x01,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x01 
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x01;
	}
	
	private boolean selectRSAencryptApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x02,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x02 
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectRSAdecryptApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x03,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x03
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectRSAsignApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x04,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x04
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}
	
	private boolean selectRSAsignVerifyApdu(byte[] apdu) { 
//		(byte) 0x80,  // CLA
//		(byte) 0x05,  // INS
//		(byte) 0x00,  // P1
//		(byte) 0x00,  // P2				
		return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x05
		&& apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}
}