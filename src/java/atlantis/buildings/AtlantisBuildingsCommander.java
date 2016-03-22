package atlantis.buildings;

import atlantis.AtlantisConfig;
import atlantis.buildings.managers.AtlantisBarracksManager;
import atlantis.buildings.managers.AtlantisBaseManager;
import atlantis.util.UnitUtil;
import atlantis.wrappers.Select;
import bwapi.Unit;

/**
 * Manages all existing-buildings actions, but training new units depends on AtlantisProductionCommander.
 */
public class AtlantisBuildingsCommander {

    /**
     * Executed once every frame.
     */
    public static void update() {
        for (Unit building : Select.ourBuildings().list()) {

            // If building is busy, don't disturb.
            if (building.getTrainingQueue().size() > 0 || building.isUpgrading()) {
                continue;
            }

            // =========================================================
            // BASE (Command Center / Nexus / Hatchery / Lair / Hive)
            if (UnitUtil.isBase(building.getType())) {
                AtlantisBaseManager.update(building);
            } 

            // =========================================================
            // BARRACKS (Barracks, Gateway, Spawning Pool)
            else if (building.getType().equals((AtlantisConfig.BARRACKS))) {
                AtlantisBarracksManager.update(building);
            }
        }
    }

}
