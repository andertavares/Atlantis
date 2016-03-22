package atlantis;

import static atlantis.Atlantis.getBwapi;
import atlantis.production.strategies.AtlantisProductionStrategy;
import atlantis.util.RUtilities;
import atlantis.wrappers.AtlantisTech;
import atlantis.wrappers.Select;
import bwapi.Player;
import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

/**
 * Represents various aspect of the game like time elapsed (in frames or approximated seconds),
 * free supply (from our point of view), game speed, enemy player etc.<br />
 * <br /><b>It's worth to study this class carefully as it contains some really useful methods.</b>
 */
public class AtlantisGame {

    private static Player _enemy = null; // Cached enemy player

    // =========================================================
    
    /**
     * Returns object that is responsible for the production queue.
     */
    public static AtlantisProductionStrategy getProductionStrategy() {
        return AtlantisConfig.getProductionStrategy();
    }

    /**
     * Returns true if we have all techs needed for given unit (but we may NOT have some of the buildings!).
     */
    public static boolean hasTechToProduce(UnitType unitType) {

        // Needs to have tech
        TechType techType = unitType.requiredTech();
        if (techType != null && techType != TechType.None && !AtlantisTech.isResearched(techType)) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if we have all buildings needed for given unit.
     */
    public static boolean hasBuildingsToProduce(UnitType unitType) {

        // Need to have every prerequisite building
        for (UnitType requiredUnitType : unitType.requiredUnits().keySet()) {
            //UnitType requiredUnitType = UnitType.getByID(unitTypeID);
            
//            if (requiredUnitType.isLarva()) {
//                continue;
//            }
//            System.out.println("=req: " + requiredUnitType);
            if (!requiredUnitType.isBuilding()) {
//                System.out.println("  continue");
                continue;
            }
            
            int requiredAmount = unitType.requiredUnits().get(requiredUnitType);
            int weHaveAmount = requiredUnitType.equals(UnitType.Zerg_Larva) ? 
                    Select.ourLarva().count() : Select.our().ofType(requiredUnitType).count();
//            System.out.println(requiredUnitType + "    x" + requiredAmount);
//            System.out.println("   and we have: " + weHaveAmount);
            if (weHaveAmount < requiredAmount) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if it's possible to produce unit (or building) of given type.
     */
    public static boolean hasTechAndBuildingsToProduce(UnitType unitType) {
        return hasTechToProduce(unitType) && hasBuildingsToProduce(unitType);
    }

    // =========================================================
    
    /**
     * Changes game speed. 0 - fastest 1 - very quick 20 - around default
     */
    public static void changeSpeed(int speed) {
        AtlantisConfig.GAME_SPEED = speed;
        getBwapi().setLocalSpeed(AtlantisConfig.GAME_SPEED);
    }

    /**
     * Returns game speed.
     */
    public static int getGameSpeed() {
        return AtlantisConfig.GAME_SPEED;
    }

    /**
     * Returns approximate number of in-game seconds elapsed.
     */
    public static int getTimeSeconds() {
        return Atlantis.getBwapi().getFrameCount() / 30;
    }

    /**
     * Returns number of frames elapsed.
     */
    public static int getTimeFrames() {
        return Atlantis.getBwapi().getFrameCount();
    }

    /**
     * Number of minerals.
     */
    public static int getMinerals() {
        return Atlantis.getBwapi().self().minerals();
    }

    /**
     * Number of gas.
     */
    public static int getGas() {
        return Atlantis.getBwapi().self().gas();
    }

    /**
     * Number of free supply.
     */
    public static int getSupplyFree() {
        return getSupplyTotal() - getSupplyUsed();
    }

    /**
     * Number of supply used.
     */
    public static int getSupplyUsed() {
        return Atlantis.getBwapi().self().supplyUsed() / 2;
    }

    /**
     * Number of supply totally available.
     */
    public static int getSupplyTotal() {
        return Atlantis.getBwapi().self().supplyTotal() / 2;
    }

    /**
     * Returns current player.
     */
    public static Player getPlayerUs() {
        return Atlantis.getBwapi().self();
    }

    /**
     * Returns enemy player.
     */
    public static Player enemy() {
        if (_enemy == null) {
            _enemy = Atlantis.getBwapi().enemies().iterator().next();
        }
        return _enemy;
    }

    /**
     * Returns enemy player.
     */
    public static Player getEnemy() {
        if (_enemy == null) {
            _enemy = Atlantis.getBwapi().enemies().iterator().next();
        }
        return _enemy;
    }

    // =========================================================
    // Auxiliary
    
    /**
     * Returns random int number from range [min, max], both inclusive.
     */
    public static int rand(int min, int max) {
        return RUtilities.rand(min, max);
    }

    /**
     * Returns true if user plays as Terran.
     */
    public static boolean playsAsTerran() {
        return AtlantisConfig.MY_RACE.equals(Race.Terran);
    }

    /**
     * Returns true if user plays as Protoss.
     */
    public static boolean playsAsProtoss() {
        return AtlantisConfig.MY_RACE.equals(Race.Protoss);
    }

    /**
     * Returns true if user plays as Zerg.
     */
    public static boolean playsAsZerg() {
        return AtlantisConfig.MY_RACE.equals(Race.Zerg);
    }

    /**
     * Returns true if enemy plays as Terran.
     */
    public static boolean isEnemyTerran() {
        return AtlantisGame.enemy().getRace().equals(Race.Terran);
    }

    /**
     * Returns true if enemy plays as Protoss.
     */
    public static boolean isEnemyProtoss() {
        return AtlantisGame.enemy().getRace().equals(Race.Protoss);
    }

    /**
     * Returns true if enemy plays as Zerg.
     */
    public static boolean isEnemyZerg() {
        return AtlantisGame.enemy().getRace().equals(Race.Zerg);
    }

    /**
     * Returns true if we can afford given amount of minerals.
     */
    public static boolean hasMinerals(int mineralsToAfford) {
        return getMinerals() >= mineralsToAfford;
    }

    /**
     * Returns true if we can afford given amount of gas.
     */
    public static boolean hasGas(int gasToAfford) {
        return getGas() >= gasToAfford;
    }

    /**
     * Returns true if we can afford minerals and gas for given unit type.
     */
    public static boolean canAfford(UnitType unitType) {
        return hasMinerals(unitType.mineralPrice()) && hasGas(unitType.gasPrice());
    }

    /**
     * Returns true if we can afford minerals and gas for given upgrade.
     */
    public static boolean canAfford(UpgradeType upgrade) {
    	//TODO: check whether we need to pass level 0 to match getMineral/GasPriceBase()
        return hasMinerals(upgrade.mineralPrice()) && hasGas(upgrade.gasPrice());	
    }

    /**
     * Returns true if we can afford both so many minerals and gas at the same time.
     */
    public static boolean canAfford(int minerals, int gas) {
        return hasMinerals(minerals) && hasGas(gas);
    }

    // =========================================================
    // Utility
    
    /**
     * Sends in-game message that will be visible by other players.
     */
    public static void sendMessage(String message) {
        getBwapi().sendText(message);
    }
    
}
