package atlantis.combat.group.missions;

import atlantis.combat.micro.AtlantisRunManager;
import atlantis.combat.micro.AtlantisRunning;
import atlantis.information.AtlantisEnemyInformationManager;
import atlantis.information.AtlantisMap;
import atlantis.util.PositionUtil;
import atlantis.wrappers.SelectUnits;
import bwta.BaseLocation;
import bwapi.Position;
import bwapi.Unit;

/**
 * This is the mission object that is used by battle groups and it indicates that we should attack 
 * the enemy at the <b>getFocusPoint</b>.
 */
public class MissionAttack extends Mission {

    public MissionAttack(String name) {
        super(name);
    }
    
    // =========================================================
    
    @Override
    public boolean update(Unit unit) {
        Position focusPoint = getFocusPoint();

        // Focus point is well known
        if (focusPoint != null) {
            if (PositionUtil.distanceTo(focusPoint, unit.getPosition()) > 5) {
                unit.attack(focusPoint, false);
//                unit.setTooltip("Mission focus");
                return true;
            }
        } 

        // =========================================================
        // Invalid focus point, no enemy can be found, scatter
        else {
            Position position = AtlantisMap.getRandomInvisiblePosition(unit.getPosition());
            if (position != null) {
                unit.attack(position, false);
//                unit.setTooltip("Mission spread");
                return true;
            }
        }

        return false;
    }

    // =========================================================
    // =========================================================
    
    /**
     * Do not interrupt unit if it is engaged in combat.
     */
    @Override
    protected boolean canIssueOrderToUnit(Unit unit) {
        if (unit.isAttacking() || unit.isStartingAttack() || AtlantisRunning.isRunning(unit)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the <b>position</b> (not the unit itself) where we should point our units to in hope 
     * because as far as we know, the enemy is/can be there and it makes sense to attack in this region.
     */
    public static Position getFocusPoint() {

        // Try going near enemy base
        Position enemyBase = AtlantisEnemyInformationManager.getEnemyBase();
        if (enemyBase != null) {
            return enemyBase;
        }

        // Try going near any enemy building
        Unit enemyBuilding = AtlantisEnemyInformationManager.getNearestEnemyBuilding();
        if (enemyBuilding != null) {
            return enemyBuilding.getPosition();
        }

        // Try going to any known enemy unit
        Unit anyEnemyUnit = SelectUnits.enemy().first();
        if (anyEnemyUnit != null) {
            return anyEnemyUnit.getPosition();
        }
        
        // Try to go to some starting location, hoping to find enemy there.
        BaseLocation startLocation = AtlantisMap.getNearestUnexploredStartingLocation(SelectUnits.mainBase().getPosition());
        if (startLocation != null) {
            return startLocation.getPosition();
        }

        // Absolutely no enemy unit can be found
        return null;
    }

}