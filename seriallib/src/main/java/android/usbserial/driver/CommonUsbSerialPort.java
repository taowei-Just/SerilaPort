package android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import java.io.IOException;


abstract class CommonUsbSerialPort implements UsbSerialPort {

    public static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;

    protected final UsbDevice mDevice;
    protected final int mPortNumber;

    // non-null when open()
    protected UsbDeviceConnection mConnection = null;

    protected final Object mReadBufferLock = new Object();
    protected final Object mWriteBufferLock = new Object();

    /** Internal read buffer.  Guarded by {@link #mReadBufferLock}. */
    protected byte[] mReadBuffer;

    /** Internal write buffer.  Guarded by {@link #mWriteBufferLock}. */
    protected byte[] mWriteBuffer;

    public CommonUsbSerialPort(UsbDevice device, int portNumber) {
        mDevice = device;
        mPortNumber = portNumber;

        mReadBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
        mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
    }
    
    @Override
    public String toString() {
        return String.format("<%s device_name=%s device_id=%s port_number=%s>",
                getClass().getSimpleName(), mDevice.getDeviceName(),
                mDevice.getDeviceId(), mPortNumber);
    }

    /**
     * Returns the currently-bound USB device.
     *
     * @return the device
     */
    public final UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getPortNumber() {
        return mPortNumber;
    }
    
    /**
     * Returns the device serial number
     *  @return serial number
     */
    @Override
    public String getSerial() {
        return mConnection.getSerial();
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for read operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setReadBufferSize(int bufferSize) {
        synchronized (mReadBufferLock) {
            if (bufferSize == mReadBuffer.length) {
                return;
            }
            mReadBuffer = new byte[bufferSize];
        }
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for write operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setWriteBufferSize(int bufferSize) {
        synchronized (mWriteBufferLock) {
            if (bufferSize == mWriteBuffer.length) {
                return;
            }
            mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override
    public abstract void open(UsbDeviceConnection connection) throws IOException;

    @Override
    public abstract void close() throws IOException;

    @Override
    public abstract int read(final byte[] dest, final int timeoutMillis) throws IOException;

    @Override
    public abstract int write(final byte[] src, final int timeoutMillis) throws IOException;

    @Override
    public abstract void setParameters(
            int baudRate, int dataBits, int stopBits, int parity) throws IOException;

    @Override
    public abstract boolean getCD() throws IOException;

    @Override
    public abstract boolean getCTS() throws IOException;

    @Override
    public abstract boolean getDSR() throws IOException;

    @Override
    public abstract boolean getDTR() throws IOException;

    @Override
    public abstract void setDTR(boolean value) throws IOException;

    @Override
    public abstract boolean getRI() throws IOException;

    @Override
    public abstract boolean getRTS() throws IOException;

    @Override
    public abstract void setRTS(boolean value) throws IOException;

    @Override
    public boolean purgeHwBuffers(boolean flushReadBuffers, boolean flushWriteBuffers) throws IOException {
        return !flushReadBuffers && !flushWriteBuffers;
    }

}
