package atlantis.util;

import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
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
    
    /**
     * Not that we're racists, but spider mines and larvas aren't really units...
     */
    public static boolean isNotActuallyUnit(UnitType t) {
        return isType(t, UnitType.Terran_Vulture_Spider_Mine, UnitType.Zerg_Larva, UnitType.Zerg_Egg);
    }
    
    /**
     * Replaces variable _isMilitaryBuildingAntiGround of old Unit class
     * @param t
     * @return
     */
    public static boolean isMilitaryBuildingAntiGround(UnitType t) {
    	return isType(    
            t, UnitType.Terran_Bunker, UnitType.Protoss_Photon_Cannon, UnitType.Zerg_Sunken_Colony
		);
    }
    
    /**
     * Replaces variable _isMilitaryBuildingAntiAir of old Unit class
     * @param t
     * @return
     */
    public static boolean isMilitaryBuildingAntiAir(UnitType t){
    	return isType(
            t, UnitType.Terran_Bunker, UnitType.Protoss_Photon_Cannon, UnitType.Zerg_Spore_Colony
		);
    }
    
    /**
     * Returns true if given unit type is one of buildings like Bunker, Photon Cannon etc. For more details, you
     * have to specify at least one <b>true</b> to the params.
     */
    public static boolean isMilitaryBuilding(UnitType t, boolean canShootGround, boolean canShootAir) {
        if (!t.isBuilding()) {
            return false;
        }
        if (canShootGround && isMilitaryBuildingAntiGround(t)) {
            return true;
        }
        else if (canShootAir && isMilitaryBuildingAntiAir(t)) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the remaining Unit life, in %
     * @param u
     * @return
     */
    public static int getHPPercent(Unit u) {
        return 100 * u.getHitPoints() / u.getType().maxHitPoints();
    }
    
    /**
     * Returns the 'normalized' damage of a UnitType
     * 'normalized' is damageAmount * damageFactor 
     * @param t
     * @return
     */
    public static int getNormalizedDamage(WeaponType wt){
    	return wt.damageAmount() * wt.damageFactor();
    }
    
    /**
     * Returns true if unit has ground weapon.
     * Replaces Unit.canAttackGroundUnits
     */
    public static boolean attacksGround(Unit u){
        return u.getType().groundWeapon() != WeaponType.None;
    }
    
    /**
     * Returns true if unit has anti-air weapon.
     * Replaces Unit.canAttackAirUnits
     */
    public static boolean attacksAir(Unit u) {
        return u.getType().airWeapon() != WeaponType.None;
    }
    
    /**
     * Returns true if attacker is capable of attacking <b>victim</b>. For example Zerglings can't
     * attack flying targets and Corsairs can't attack ground targets.
     * @param includeCooldown if true, then unit will be considered able to attack only if the cooldown
     * after the last shot allows it
     */
    public static boolean canAttack(Unit attacker, Unit victim, boolean includeCooldown) {
        
        // Enemy is GROUND unit
        if (!victim.getType().isFlyer()) {
            return attacksGround(attacker) && (!includeCooldown || attacker.getGroundWeaponCooldown() == 0);
        }
        
        // Enemy is AIR unit
        else  {
            return attacksAir(attacker) && (!includeCooldown || attacker.getAirWeaponCooldown() == 0);
        }
    }
    
    public static String getShortName(UnitType t) {
        return t.toString().replace("Terran ", "").replace("Protoss ", "").replace("Zerg ", "");
    }

    
}
