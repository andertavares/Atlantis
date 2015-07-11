package atlantis.information;

import java.util.ArrayList;

import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import atlantis.AtlantisConfig;
import atlantis.wrappers.MappingCounter;

public class AtlantisUnitInformationManager {

	protected static MappingCounter<UnitType> ourUnitsFininised = new MappingCounter<>();
	protected static MappingCounter<UnitType> ourUnitsUnfininised = new MappingCounter<>();

	protected static MappingCounter<UnitType> enemyUnitsDiscoveredCounter = new MappingCounter<>();
	protected static MappingCounter<UnitType> enemyUnitsVisibleCounter = new MappingCounter<>();

	protected static ArrayList<Unit> enemyUnitsDiscovered = new ArrayList<>();
	protected static ArrayList<Unit> enemyUnitsVisible = new ArrayList<>();

	// =========================================================
	// Number of units changed

	/**
	 * Saves information about our new unit being trained, so counting units works properly.
	 */
	public static void addOurUnfinishedUnit(UnitType type) {
		ourUnitsUnfininised.incrementValueFor(type);
	}

	/**
	 * Saves information about new unit being created successfully, so counting units works properly.
	 */
	public static void addOurFinishedUnit(UnitType type) {
		ourUnitsFininised.incrementValueFor(type);
	}

	/**
	 * Saves information about enemy unit that we see for the first time.
	 */
	public static void discoveredEnemyUnit(Unit unit) {
		enemyUnitsDiscovered.add(unit);
		enemyUnitsDiscoveredCounter.incrementValueFor(unit.getType());
	}

	/**
	 * Saves information about given unit being destroyed, so counting units works properly.
	 */
	public static void unitDestroyed(Unit unit) {
		if (unit.getPlayer().isSelf()) {
			if (unit.isCompleted()) {
				ourUnitsFininised.decrementValueFor(unit.getType());
			} else {
				ourUnitsUnfininised.decrementValueFor(unit.getType());
			}
		} else if (unit.getPlayer().isEnemy()) {
			enemyUnitsDiscoveredCounter.decrementValueFor(unit.getType());
			enemyUnitsVisibleCounter.decrementValueFor(unit.getType());
			enemyUnitsDiscovered.remove(unit);
			enemyUnitsVisible.remove(unit);
		}
	}

	public static void addEnemyUnitVisible(Unit unit) {
		enemyUnitsVisible.add(unit);
		enemyUnitsVisibleCounter.incrementValueFor(unit.getType());
	}

	public static void removeEnemyUnitVisible(Unit unit) {
		enemyUnitsVisible.remove(unit);
		enemyUnitsVisibleCounter.decrementValueFor(unit.getType());
	}

	// =========================================================
	// COUNT

	/**
	 * Returns cached amount of our units of given type.
	 */
	public static int countOurUnitsOfType(UnitType type) {
		return ourUnitsUnfininised.getValueFor(type);
	}

	/**
	 * Returns number of discovered and alive enemy units of given type. Some of them (maybe even all of them) may not
	 * be visible right now.
	 */
	public static int countEnemyUnitsOfType(UnitType type) {
		return enemyUnitsDiscoveredCounter.getValueFor(type);
	}

	// =========================================================
	// Helper methods

	/**
	 * Returns cached amount of our worker units.
	 */
	public static int countOurWorkers() {
		return countOurUnitsOfType(AtlantisConfig.WORKER);
	}

	/**
	 * Returns cached amount of our bases.
	 */
	public static int countOurBases() {
		return countOurUnitsOfType(AtlantisConfig.BASE);
	}

}