package atlantis.information;

import atlantis.util.UnitUtil;
import atlantis.wrappers.SelectUnits;
import bwapi.Position;
import bwapi.Unit;

/**
 * Provides various useful infromation about the enemy whereabouts or if even know any enemy building.
 */
public class AtlantisEnemyInformationManager {

    /**
     * Returns true if we learned the location of any still-existing enemy building.
     */
    public static boolean hasDiscoveredEnemyBuilding() {
        if (AtlantisUnitInformationManager.enemyUnitsDiscovered.isEmpty()) {
            return false;
        }

        for (Unit enemy : AtlantisUnitInformationManager.enemyUnitsDiscovered) {
            if (enemy.getType().isBuilding()) {
                return true;
            }
        }
        return false;
    }

    /**
     * If we learned about at least one still existing enemy base it returns first of them. Returns null
     * otherwise.
     */
    public static Unit hasDiscoveredEnemyBase() {
        if (!hasDiscoveredEnemyBuilding()) {
            return null;
        }

        for (Unit enemyUnit : AtlantisUnitInformationManager.enemyUnitsDiscovered) {
            if (UnitUtil.isBase(enemyUnit.getType())) {
                return enemyUnit;
            }
        }

        return null;
    }

    /**
     * Gets oldest known enemy base.
     */
    public static Position getEnemyBase() {
//        System.out.println(AtlantisUnitInformationManager.enemyUnitsDiscovered.size());
        for (Unit enemyUnit : AtlantisUnitInformationManager.enemyUnitsDiscovered) {
//            System.out.println(enemyUnit);
            if (UnitUtil.isBase(enemyUnit.getType()) && enemyUnit.exists()) {
                return enemyUnit.getPosition();
            }
        }

        return null;
    }

    /**
     *
     */
    public static Unit getNearestEnemyBuilding() {
        Unit mainBase = SelectUnits.mainBase();
        if (mainBase != null && !AtlantisUnitInformationManager.enemyUnitsDiscovered.isEmpty()) {
        	System.out.println("# all:" + SelectUnits.from(AtlantisUnitInformationManager.enemyUnitsDiscovered));
        	System.out.println("# bldgs: " + SelectUnits.from(AtlantisUnitInformationManager.enemyUnitsDiscovered).buildings().count());
        	System.out.println("Closest bldg: " + SelectUnits.from(AtlantisUnitInformationManager.enemyUnitsDiscovered).buildings().nearestTo(mainBase.getPosition()));
            return SelectUnits.from(AtlantisUnitInformationManager.enemyUnitsDiscovered).buildings().nearestTo(mainBase.getPosition());
        }
        return null;
    }

}
