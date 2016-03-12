package atlantis.util;

import bwapi.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class UnitUtil {
	
	/**
	 * Returns the total cost of a unit for score calculation,
	 * considering the Zergling special case (half cost per unit)
	 * @param u
	 * @return
	 */
	public static int getTotalPrice(UnitType t){
		int total = t.gasPrice() + t.mineralPrice();
        if (t.equals(UnitTypes.Zerg_Zergling)) {
            total /= 2;
        }
        return total;
	}
}
