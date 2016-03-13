package atlantis.constructing.position;

import atlantis.util.PositionUtil;
import atlantis.wrappers.SelectUnits;
import bwapi.Position;
import jnibwapi.Position.PosType;
import bwapi.Unit;
import bwapi.UnitType;

public class TerranPositionFinder extends AbstractPositionFinder {

    /**
     * Returns best position for given <b>building</b>, maximum <b>maxDistance</b> build tiles from
     * <b>nearTo</b>
     * position.<br />
     * It checks if buildings aren't too close one to another and things like that.
     *
     */
    public static Position findStandardPositionFor(Unit builder, UnitType building, Position nearTo, double maxDistance) {
        AtlantisPositionFinder.building = building;
        AtlantisPositionFinder.nearTo = nearTo;
        AtlantisPositionFinder.maxDistance = maxDistance;

        // =========================================================
        int searchRadius = building.equals(UnitType.Terran_Supply_Depot) ? 8 : 0;

        while (searchRadius < maxDistance) {
            int xCounter = 0;
            int yCounter = 0;
            int doubleRadius = searchRadius * 2;
            for (int tileX = nearTo.getBX() - searchRadius; tileX <= nearTo.getBX() + searchRadius; tileX++) {
                for (int tileY = nearTo.getBY() - searchRadius; tileY <= nearTo.getBY() + searchRadius; tileY++) {
                    if (xCounter == 0 || yCounter == 0 || xCounter == doubleRadius || yCounter == doubleRadius) {
                        Position position = new Position(tileX, tileY, PosType.BUILD);
                        if (doesPositionFulfillAllConditions(builder, position)) {
                            return position;
                        }
                    }

                    yCounter++;
                }
                xCounter++;
            }

            searchRadius++;
        }

        return null;
    }

    // =========================================================
    // Hi-level
    /**
     * Returns true if given position (treated as building position for our <b>UnitType building</b>) has all
     * necessary requirements like: doesn't collide with another building, isn't too close to minerals etc.
     */
    private static boolean doesPositionFulfillAllConditions(Unit builder, Position position) {
        if (builder == null) {
            return false;
        }
        if (position == null) {
            return false;
        }

        // If it's not physically possible to build here (e.g. rocks, other buildings etc)
        if (!canPhysicallyBuildHere(builder, AtlantisPositionFinder.building, position)) {
            return false;
        }

        // If other buildings too close
        if (otherBuildingsTooClose(builder, AtlantisPositionFinder.building, position)) {
            return false;
        }

        // Can't be too close to minerals or to geyser, because would slow down production
        if (isTooCloseToMineralsOrGeyser(AtlantisPositionFinder.building, position)) {
            return false;
        }

        // All conditions are fullfilled, return this position
        return true;
    }

    // =========================================================
    // Lo-level
    private static boolean isTooCloseToMineralsOrGeyser(UnitType building, Position position) {

        // We have problem only if building is both close to base and to minerals or to geyser
        Unit nearestBase = SelectUnits.ourBases().nearestTo(position);
        if (nearestBase != null && PositionUtil.distanceTo(nearestBase.getPosition(), position) <= 7) {
            for (Unit mineral : SelectUnits.minerals().inRadius(8, position).list()) {
                if (PositionUtil.distanceTo(mineral.getPosition(), position) <= 4) {
                    return true;
                }
            }
        }
        return false;
    }
}
