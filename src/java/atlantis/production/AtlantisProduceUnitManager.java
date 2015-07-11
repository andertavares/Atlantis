package atlantis.production;

import java.util.ArrayList;

import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.types.UpgradeType;
import atlantis.AtlantisConfig;
import atlantis.constructing.AtlantisConstructingManager;
import atlantis.production.strategies.AtlantisProductionStrategy;
import atlantis.wrappers.SelectUnits;

public class AtlantisProduceUnitManager {

	/**
	 * Is responsible for training new units and issuing construction requests for buildings.
	 */
	protected static void update() {
		AtlantisProductionStrategy productionStrategy = AtlantisConfig.getProductionStrategy();

		ArrayList<ProductionOrder> produceNow = productionStrategy.getThingsToProduceRightNow(false);
		for (ProductionOrder order : produceNow) {

			// Produce UNIT
			if (order.getUnitType() != null) {
				UnitType unitType = order.getUnitType();
				if (unitType.isBuilding()) {
					AtlantisConstructingManager.requestConstructionOf(unitType);
				} else {
					produceUnit(unitType);
				}
			}

			// Produce UPGRADE
			else if (order.getUpgrade() != null) {
				UpgradeType upgrade = order.getUpgrade();
				researchUpgrade(upgrade);
			}
		}
	}

	// =========================================================
	// Hi-level produce

	private static void produceUnit(UnitType unitType) {

		// Worker
		if (unitType.equals(AtlantisConfig.WORKER)) {
			produceWorker();
		}

		// Infantry
		else if (unitType.isTerranInfantry()) {
			produceInfantry(unitType);
		}

		// Unknown example
		else {
			System.err.println("UNHANDLED UNIT TO BUILD: " + unitType);
		}
	}

	private static void researchUpgrade(UpgradeType upgrade) {
		UnitType buildingType = UnitTypes.getUnitType(upgrade.getWhatUpgradesTypeID());
		if (buildingType != null) {
			Unit building = SelectUnits.ourBuildings().ofType(buildingType).first();
			if (building != null) {
				building.upgrade(upgrade);
			}
		}
	}

	// =========================================================
	// Lo-level produce

	private static void produceWorker() {
		Unit building = SelectUnits.ourOneIdle(AtlantisConfig.BASE);
		if (building != null) {
			building.train(AtlantisConfig.WORKER);
		}
	}

	private static void produceInfantry(UnitType infantryType) {
		Unit building = SelectUnits.ourOneIdle(AtlantisConfig.BARRACKS);
		if (building != null) {
			building.train(infantryType);
		}
	}

}
