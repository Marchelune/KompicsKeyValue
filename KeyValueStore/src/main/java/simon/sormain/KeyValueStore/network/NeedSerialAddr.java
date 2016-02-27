package simon.sormain.KeyValueStore.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.netty.buffer.ByteBuf;

public class NeedSerialAddr {
    protected void addressToBinary(TAddress addr, ByteBuf buf) {
        buf.writeBytes(addr.getIp().getAddress()); // 4 bytes IP (let's hope it's IPv4^^)
        buf.writeShort(addr.getPort()); // we only need 2 bytes here
        // total of 6 bytes
    }

    protected TAddress addressFromBinary(ByteBuf buf) {
        byte[] ipBytes = new byte[4];
        buf.readBytes(ipBytes); // 4 bytes
        try {
            InetAddress ip = InetAddress.getByAddress(ipBytes);
            int port = buf.readUnsignedShort(); // 2 bytes
            return new TAddress(ip, port); // total of 6, check
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex); // let Netty deal with this
        }
    }
}
