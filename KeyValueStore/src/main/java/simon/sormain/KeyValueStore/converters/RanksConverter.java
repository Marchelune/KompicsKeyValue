package simon.sormain.KeyValueStore.converters;

import se.sics.kompics.config.Converter;
import simon.sormain.KeyValueStore.network.TAddress;

public class RanksConverter implements Converter<MapRanks> {

	TAddressConverter TaddressConv = new TAddressConverter();
	
	public MapRanks convert(Object o) {
		MapRanks ranks = new MapRanks();
		
		if (o instanceof String) {
			for (String val: ((String) o).split("!")){
				// val is a rank/address pair
				String[] rankAddrPair = val.split(";");
				
				//Rank
				int Rank = Integer.parseInt(rankAddrPair[0]);
				
				//Address
				TAddress Addr = TaddressConv.convert(rankAddrPair[1]);
				ranks.put(Rank, Addr);
			 }
			return ranks;
        }
        return null;
	}
	
	
    public Class<MapRanks> type() {
        return MapRanks.class;
    }
}
