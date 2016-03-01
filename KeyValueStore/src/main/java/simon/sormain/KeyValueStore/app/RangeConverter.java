package simon.sormain.KeyValueStore.app;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

import se.sics.kompics.config.Converter;
import simon.sormain.KeyValueStore.network.SetTAddress;
import simon.sormain.KeyValueStore.network.SetTAddressConverter;
import simon.sormain.KeyValueStore.network.TAddress;


public class RangeConverter implements Converter<MapRanges>  {
	
	SetTAddressConverter SetTaddressConv = new SetTAddressConverter();
	
	public MapRanges convert(Object o) {
		MapRanges ranges = new MapRanges();
		
		if (o instanceof String) {
			for (String val: ((String) o).split("!")){
				// val is a range/set of addresses pair
				String[] rangeAddrPair = val.split(";");
				
				//Range
				String SRange = rangeAddrPair[0];
				String[] TabSRange = SRange.split(":");
				int[] range = new int[2];
				for(int i = 0; i < 2; i++){
					range[i] = Integer.parseInt(TabSRange[i]);
				}
				
				//Addresses
				String SAddrs = rangeAddrPair[1];
				SetTAddress Addrs = SetTaddressConv.convert(SAddrs);
				ranges.put(range, Addrs.get());
			 }
			return ranges;
        }
        return null;
	}
	
	
    public Class<MapRanges> type() {
        return MapRanges.class;
    }

}
