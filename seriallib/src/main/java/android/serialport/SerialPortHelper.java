package android.serialport;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortHelper {
	SerialPort serialPort;
	InputStream inputStream;
	OutputStream outputStream;
	byte[] dataBuffer;
	String devName;

	public SerialPortHelper(String name) throws Exception {
		if (null != name) {
			serialPort = new SerialPort(new File(name), 9600, 0);
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			dataBuffer = new byte[128];
		}
	}

	public void sendData() throws Exception {
		if (outputStream != null) {
			byte[] buffer = "123456789\r\n".getBytes();
			outputStream.write(buffer);
			outputStream.flush();
		}
	}

	public String recieveData() throws Exception {
		if (inputStream != null) {
			int length = inputStream.read(dataBuffer);
			String recieve = new String(dataBuffer, 0, length);
			return recieve;
		}
		return null;
	}

}
