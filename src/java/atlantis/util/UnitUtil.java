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
        if (t.equals(UnitType.Zerg_Zergling)) {
            total /= 2;
        }
        return total;
	}
	
	public static boolean isBase(UnitType t){
	        return isType(t, UnitType.Terran_Command_Center, UnitType.Protoss_Nexus, UnitType.Zerg_Hatchery,
	                UnitType.Zerg_Lair, UnitType.Zerg_Hive);
	}
	
	/**
     * Returns true if given type equals to one of types passed as parameter.
     */
    public static boolean isType(UnitType t, UnitType... types) {
        for (UnitType otherType : types) {
            if (t.equals(otherType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether UnitType is Refinery, Assimilator or Extractor
     * @param t
     * @return
     */
    public static boolean isGasBuilding(UnitType t){
    	return isType(t, UnitType.Terran_Refinery, UnitType.Protoss_Assimilator, UnitType.Zerg_Extractor);
    }
}
