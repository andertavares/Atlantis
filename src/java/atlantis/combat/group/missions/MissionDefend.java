package atlantis.combat.group.missions;

import atlantis.debug.tooltip.TooltipManager;
import atlantis.information.AtlantisMap;
import atlantis.util.PositionUtil;
import atlantis.wrappers.SelectUnits;
import bwta.Chokepoint;
import bwapi.Unit;

public class MissionDefend extends Mission {

    public MissionDefend(String name) {
        super(name);
    }
    
    // =========================================================

    @Override
    public boolean update(Unit unit) {
        if (canIssueOrderToUnit(unit)) {
            if (moveUnitIfNeededNearChokePoint(unit)) {
                return true;
            }
        }

        return false;
    }

    // =========================================================
    /**
     * Unit will go towards important choke point near main base.
     */
    private boolean moveUnitIfNeededNearChokePoint(Unit unit) {
        Chokepoint chokepoint = getFocusPoint();
        if (chokepoint == null) {
            System.err.println("Couldn't define choke point.");
            return false;
        }

        // =========================================================
        // Normal orders
        // Check if shouldn't disturb unit
        if (canIssueOrderToUnit(unit)) {

            // Too close to
            if (isCriticallyCloseToChokePoint(unit, chokepoint)) {
                unit.moveAwayFrom(chokepoint, 1.0);
                TooltipManager.getInstance().setTooltip(unit, "Get back");
                //unit.setTooltip("Get back");
                return true;
            }

            // Unit is quite close to the choke point
            if (isCloseEnoughToChokePoint(unit, chokepoint)) {

                // Too many stacked units
                if (isTooManyUnitsAround(unit, chokepoint)) {
                    unit.moveAwayFrom(chokepoint, 1.0);
                    TooltipManager.getInstance().setTooltip(unit, "Stacked");
                    //unit.setTooltip("Stacked");
                } // Units aren't stacked too much
                else {
                }
            } // Unit is far from choke point
            else {
                unit.move(chokepoint.getCenter(), false);
            }
        }

        return false;
    }

    private boolean isTooManyUnitsAround(Unit unit, Chokepoint chokepoint) {
        return SelectUnits.ourCombatUnits().inRadius(1.0, unit.getPosition()).count() >= 3;
    }

    private boolean isCloseEnoughToChokePoint(Unit unit, Chokepoint chokepoint) {
        if (unit == null || chokepoint == null) {
            return false;
        }

        // Bigger this value is, further from choke will units stand
        double unitShootRangeExtra = +0.3;

     // Distance to the center of choke point. TODO: check whether getWidth()/100.0f has the same effect of getRadiusInTiles
        double distToChoke = PositionUtil.distanceTo(chokepoint.getCenter(), unit.getPosition()) - chokepoint.getWidth()/100.0f;	// getRadiusInTiles()

        // How far can the unit shoot
        double unitShootRange =  unit.getType().groundWeapon().maxRange() / 32; //getShootRangeGround();

        // Define max allowed distance from choke point to consider "still close"
        double maxDistanceAllowed = unitShootRange + unitShootRangeExtra;

        return distToChoke <= maxDistanceAllowed;
    }

    private boolean isCriticallyCloseToChokePoint(Unit unit, Chokepoint chokepoint) {
        if (unit == null || chokepoint == null) {
            return false;
        }

        // Distance to the center of choke point. TODO: check whether getWidth()/100.0f has the same effect of getRadiusInTiles
        double distToChoke = PositionUtil.distanceTo(chokepoint.getCenter(), unit.getPosition()) - chokepoint.getWidth()/100.0f;	// getRadiusInTiles()

        // Can't be closer than X from choke point
        if (distToChoke <= 3.8) {
            return true;
        }

        // Bigger this value is, further from choke will units stand
        double standFurther = 1;

        // How far can the unit shoot (in build tiles)
        double unitShootRange = unit.getType().groundWeapon().maxRange() / 32; //getShootRangeGround();

        // Define max distance
        double maxDistance = unitShootRange + standFurther;

        return distToChoke <= maxDistance;
    }

    // =========================================================
    public static Chokepoint getFocusPoint() {
        return AtlantisMap.getMainBaseChokepoint();
    }

    /**
     * Do not interrupt unit if it is engaged in combat.
     */
    @Override
    protected boolean canIssueOrderToUnit(Unit unit) {

        // If unit has far more important actions than fucking positioning, disallow any actions here.
        if (unit.isAttacking() || unit.isStartingAttack() || unit.isRunning() || unit.isAttackFrame() || unit.isMoving()) {
            return false;
        }

        // If enemy is close, also it's dumb to do proper positioning. Let the MicroManager decide.
        Unit nearestEnemy = SelectUnits.enemy().nearestTo(unit.getPosition());
        if (nearestEnemy != null && PositionUtil.distanceTo(nearestEnemy, unit) < 15) {
            return false;
        }

        return true;
    }
}
