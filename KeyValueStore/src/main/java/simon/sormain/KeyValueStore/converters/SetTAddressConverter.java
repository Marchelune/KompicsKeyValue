package simon.sormain.KeyValueStore.converters;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import se.sics.kompics.config.Conversions;
import se.sics.kompics.config.Converter;
import simon.sormain.KeyValueStore.network.*;

public class SetTAddressConverter implements Converter<SetTAddress> {

	public SetTAddress convert(Object o) {
		
		SetTAddress list = new SetTAddress();
		
		if (o instanceof String) {
			try {
                for (String val: ((String) o).split(",")){
                	String[] ipport = val.split(":");
                	InetAddress ip = InetAddress.getByName(ipport[0]);
                	int port = Integer.parseInt(ipport[1]);
                	list.add(new TAddress(ip, port));
                 }
                return list;
            } catch (UnknownHostException ex) {
                return null;
            }
        }
        return null;
	}
	
    public Class<SetTAddress> type() {
        return SetTAddress.class;
    }
}
