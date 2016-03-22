package atlantis.wrappers;

import atlantis.Atlantis;
import atlantis.AtlantisConfig;
import atlantis.information.UnitData;
import atlantis.util.PositionUtil;
import atlantis.util.UnitUtil;

import java.util.Collection;
import java.util.Iterator;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * This class allows to easily select units e.g. to select one of your Marines, nearest to given location, you
 * would run:<br />
 * <p>
 * <b> SelectUnits.our().ofType(UnitType.Terran_Marine).nearestTo(SelectUnits.mainBase()) </b>
 * </p>
 * It uses nice flow and every next method filters out units that do not fulfill certain conditions.<br />
 * Unless clearly specified otherwise, this class returns <b>ONLY COMPLETED</b> units.
 */
public class Select<C> {

    // =====================================================================
    // Collection<Unit> wrapper with extra methods
    private C units;

    // CACHED variables
    private static Unit _cached_mainBase = null;

    // =====================================================================
    // Constructor is private, use our(), enemy() or neutral() methods
    private Select(C units) {
        this.units = units;
    }

    // =====================================================================
    // Create base object
    /**
     * Selects all of our finished and existing units (units, buildings, but no spider mines etc).
     */
    public static Select<Units> our() {
        Units units = new Units();

        //self().getUnits() replaces getMyUnits()
        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && unit.isCompleted() && ! UnitUtil.isType(unit.getType(), UnitType.Terran_Vulture_Spider_Mine, UnitType.Zerg_Larva, UnitType.Zerg_Egg) ) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
        
    }

    /**
     * Selects all of our finished combat units (no buildings, workers, spider mines etc).
     */
    public static Select<Units> ourCombatUnits() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && unit.isCompleted() && ! UnitUtil.isNotActuallyUnit(unit.getType()) && !unit.getType().isBuilding()
                    && !unit.getType().equals(AtlantisConfig.WORKER)) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all of our units (units, buildings, but no spider mines etc), <b>even those unfinished</b>.
     */
    public static Select<Units> ourIncludingUnfinished() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && !unit.getType().equals(UnitType.Terran_Vulture_Spider_Mine)) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects our unfinished units.
     */
    public static Select<Units> ourUnfinished() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && !unit.isCompleted()) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects our unfinished units.
     */
    public static Select<Units> ourRealUnits() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && unit.isCompleted() && !unit.getType().isBuilding() && ! UnitUtil.isNotActuallyUnit(unit.getType())) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects our unfinished units.
     */
    public static Select<Units> ourUnfinishedRealUnits() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.exists() && !unit.isCompleted() && !unit.getType().isBuilding() && ! UnitUtil.isNotActuallyUnit(unit.getType())) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all visible enemy units.
     */
    public static Select<Units> enemy() {
        Units units = new Units();

        //TODO: check whether enemy().getUnits() has the same behavior as  getEnemyUnits()
        for (Unit unit : Atlantis.getBwapi().enemy().getUnits()) {
            if (unit.isVisible() && unit.getHitPoints() >= 1) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all visible enemy units.
     */
    public static Select<Units> enemy(boolean includeGroundUnits, boolean includeAirUnits) {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().enemy().getUnits()) {
            if (unit.isVisible() && unit.getHitPoints() >= 1) {
                if ((!unit.getType().isFlyer() && includeGroundUnits) || (unit.getType().isFlyer() && includeAirUnits)) {
                    units.addUnit(unit);
                }
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all visible enemy units.
     */
    public static Select<Units> enemyRealUnits() {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().enemy().getUnits()) {
            if (unit.exists() && unit.isVisible() && !unit.getType().isBuilding() && ! UnitUtil.isNotActuallyUnit(unit.getType())) {
                units.addUnit(unit);
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all visible enemy units.
     */
    public static Select<Units> enemyRealUnits(boolean includeGroundUnits, boolean includeAirUnits) {
        Units units = new Units();

        for (Unit unit : Atlantis.getBwapi().enemy().getUnits()) {
            if (unit.exists() && unit.isVisible() && !unit.getType().isBuilding() && !UnitUtil.isType(unit.getType(), UnitType.Zerg_Larva, UnitType.Zerg_Egg)) {
                if ((!unit.getType().isFlyer() && includeGroundUnits) || (unit.getType().isFlyer() && includeAirUnits)) {
                    units.addUnit(unit);
                }
            }
        }

        return new Select<Units>(units);
    }

    /**
     * Selects all visible neutral units (minerals, geysers, critters).
     */
    public static Select<Units> neutral() {
        Units units = new Units();

        units.addUnits(Atlantis.getBwapi().getNeutralUnits());

        return new Select<Units>(units);
    }

    /**
     * Selects all minerals on the map.
     */
    public static Select minerals() {
        Units units = new Units();

        units.addUnits(Atlantis.getBwapi().getNeutralUnits());
        Select selectUnits = new Select(units);

        return selectUnits.ofType(UnitType.Resource_Mineral_Field);
    }

    /**
     * Selects all geysers on the map.
     */
    public static Select geysers() {
        Units units = new Units();

        units.addUnits(Atlantis.getBwapi().getNeutralUnits());
        Select selectUnits = new Select(units);

        return selectUnits.ofType(UnitType.Resource_Vespene_Geyser);
    }

    /**
     * Create initial search-pool of units from given collection of units.
     */
    public static Select<Units> from(Units units) {
        Select<Units> selectUnits = new Select<>(units);
        return selectUnits;
    }

    /**
     * Create initial search-pool of units from given collection of units.
     */
    public static Select<UnitsData> from(Collection<UnitData> unitsCollection) {
        UnitsData units = new UnitsData();
        for (UnitData unit : unitsCollection) {
            units.addUnit(unit);
        }

        Select<UnitsData> selectUnits = new Select<>(units);
        return selectUnits;
    }

    /**
     * Returns all units that are closer than <b>maxDist</b> tiles from given <b>position</b>.
     */
    public Select inRadius(double maxDist, Position position) {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (PositionUtil.distanceTo(unit.getPosition(),position) > maxDist) {
                unitsIterator.remove();
            }
        }

        return this;
    }

    // =====================================================================
    // Filter units
    /**
     * Selects only units of given type(s).
     */
    public Select ofType(UnitType... types) {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            boolean typeMatches = false;
            for (UnitType type : types) {
                if (unit.getType().equals(type) 
                        || (unit.getType().equals(UnitType.Zerg_Egg) && unit.getBuildType().equals(type))) {
                    typeMatches = true;
                    break;
                }
            }
            if (!typeMatches) {
                unitsIterator.remove();
            }
        }

        return this;
    }
    
    /**
     * Selects only units of given type(s).
     */
    public int countUnitsOfType(UnitType... types) {
        int total = 0;
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            boolean typeMatches = false;
            for (UnitType type : types) {
                if (unit.getType().equals(type) 
                        || (unit.getType().equals(UnitType.Zerg_Egg) && unit.getBuildType().equals(type))) {
                    typeMatches = true;
                    break;
                }
            }
            if (typeMatches) {
                total++;
            }
        }

        return total;
    }

    /**
     * Selects only those units which are idle. Idle is unit's class flag so be careful with that.
     */
    public Select idle() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (!unit.isIdle()) {
                unitsIterator.remove();
            }
        }

        return this;
    }

    /**
     * Selects units that are gathering minerals.
     */
    public Select gatheringMinerals(boolean onlyNotCarryingMinerals) {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (!unit.isGatheringMinerals()) {
                if (onlyNotCarryingMinerals && !unit.isCarryingMinerals()) {
                    unitsIterator.remove();
                } else {
                    unitsIterator.remove();
                }
            }
        }

        return this;
    }

    /**
     * Selects units being infantry.
     */
    public Select infantry() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (!unit.getType().isOrganic()) { //replaced  isInfantry()
                unitsIterator.remove();
            }
        }

        return this;
    }

    /**
     * Selects only units that do not currently have max hit points.
     */
    public Select wounded() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            // unit.getHitPoints() >= unit.getType().maxHitPoints() replaces !isWounded()
            if (unit.getHitPoints() >= unit.getType().maxHitPoints()) {
                unitsIterator.remove();
            }
        }

        return this;
    }

    /**
     * Selects only buildings.
     */
    public Select buildings() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (!unit.getType().isBuilding()) {
                unitsIterator.remove();
            }
        }
        return this;
    }

    /**
     * Selects only units that can fight in any way including: - infantry including Terran Medics, but not
     * workers - military buildings like Photon Cannon, Bunker, Spore Colony, Sunken Colony
     */
    public Select combatUnits() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            boolean isMilitaryBuilding = UnitUtil.isType(unit.getType(),
                    UnitType.Terran_Bunker,
                    UnitType.Protoss_Photon_Cannon,
                    UnitType.Zerg_Sunken_Colony,
                    UnitType.Zerg_Spore_Colony
            );
            if (!unit.isCompleted() || !unit.exists() || (unit.getType().isBuilding() && !isMilitaryBuilding)) {
                unitsIterator.remove();
            }
        }
        return this;
    }

    /**
     * Selects only those Terran vehicles that can be repaired so it has to be:<br />
     * - mechanical<br />
     * - not 100% healthy<br />
     */
    public Select toRepair() {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            
            //isMechanical replaces  isRepairableMechanically
            //unit.getHitPoints() >= unit.getType().maxHitPoints() replaces isFullyHealthy
            if (!unit.getType().isMechanical() || unit.getHitPoints() >= unit.getType().maxHitPoints()  || !unit.isCompleted()) {
                unitsIterator.remove();
            }
        }
        return this;
    }

    // =========================================================
    // Hi-level auxiliary methods
    /**
     * Selects all of our bases.
     */
    public static Select ourBases() {
        return our().ofType(AtlantisConfig.BASE);
    }

    /**
     * Selects our workers (that is of type Terran SCV or Zerg Drone or Protoss Probe).
     */
    public static Select ourWorkers() {
        Select selectedUnits = Select.our();
        for (Unit unit : selectedUnits.list()) {
            if (!unit.getType().isWorker() && unit.exists()) {
                selectedUnits.units.removeUnit(unit);
            }
        }
        return selectedUnits;
    }

    /**
     * Selects our workers (that is of type Terran SCV or Zerg Drone or Protoss Probe) that are either 
     * gathering minerals or gas.
     */
    public static Select ourWorkersThatGather() {
        Select selectedUnits = Select.our();
        for (Unit unit : selectedUnits.list()) {
            if (!unit.getType().isWorker() || (!unit.isGatheringGas() && !unit.isGatheringMinerals())) {
                selectedUnits.units.removeUnit(unit);
            }
        }
        return selectedUnits;
    }

    /**
     * Selects our workers that are free to construct building or repair a unit. That means they mustn't
     * repait any other unit or construct other building.
     */
    public static Select ourWorkersFreeToBuildOrRepair() {
        Select selectedUnits = ourWorkers();

        for (Unit unit : selectedUnits.list()) {
            if (unit.isConstructing() || unit.isRepairing()) {
                selectedUnits.units.removeUnit(unit);
            }
        }

        return selectedUnits;
    }

    /**
     * Selects all our finished buildings.
     */
    public static Select ourBuildings() {
        return our().buildings();
    }

    /**
     * Selects all our buildings including those unfinished.
     */
    public static Select ourBuildingsIncludingUnfinished() {
        Select selectedUnits = Select.ourIncludingUnfinished();
        for (Unit unit : selectedUnits.list()) {
            if (!unit.getType().isBuilding()) {
                selectedUnits.units.removeUnit(unit);
            }
        }
        return selectedUnits;
    }

    /**
     * Selects all our tanks, both sieged and unsieged.
     */
    public static Select ourTanks() {
        return our().ofType(UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Siege_Tank_Tank_Mode);
    }

    /**
     * Selects all our sieged tanks.
     */
    public static Select ourTanksSieged() {
        return our().ofType(UnitType.Terran_Siege_Tank_Siege_Mode);
    }

    /**
     * Selects all of our Marines, Firebats, Ghosts and Medics.
     */
    public static Select ourTerranInfantry() {
        return our().ofType(UnitType.Terran_Marine, UnitType.Terran_Medic,
                    UnitType.Terran_Firebat, UnitType.Terran_Ghost);
    }

    /**
     * Selects all of our Marines, Firebats, Ghosts.
     */
    public static Select ourTerranInfantryWithoutMedics() {
        return our().ofType(UnitType.Terran_Marine,
                    UnitType.Terran_Firebat, UnitType.Terran_Ghost);
    }

    /**
     * Selects all of our Zerg Larvas.
     */
    public static Select ourLarva() {
        Select selectedUnits = Select.ourIncludingUnfinished();
        for (Unit unit : selectedUnits.list()) {
            if (!unit.getType().equals(UnitType.Zerg_Larva)) {
                selectedUnits.units.removeUnit(unit);
            }
        }
        return selectedUnits;
    }

    /**
     * Selects all of our Zerg Eggs.
     */
    public static Select ourEggs() {
        Select selectedUnits = Select.ourIncludingUnfinished();
        for (Unit unit : selectedUnits.list()) {
            if (!unit.getType().equals(UnitType.Zerg_Egg)) {
                selectedUnits.units.removeUnit(unit);
            }
        }
        return selectedUnits;
    }

    // =========================================================
    // Localization-related methods
    /**
     * From all units currently in selection, returns closest unit to given <b>position</b>.
     */
    public Unit nearestTo(Position position) {
        if (units.isEmpty() || position == null) {
            return null;
        }

        units.sortByDistanceTo(position, true);
        return units.first();
    }

    /**
     * Returns first unit being base. For your units this is most likely your main base, for enemy it will be
     * first discovered base.
     */
    public static Unit mainBase() {
        if (_cached_mainBase == null) {
            Units bases = ourBases().units();
            _cached_mainBase = bases.isEmpty() ? null : bases.first();
        }

        return _cached_mainBase;
    }

    /**
     * Returns second (natural) base <b>or if we have only one base</b>, it returns the only base we have.
     */
    public static Unit secondBaseOrMainIfNoSecond() {
        Collection<Unit> bases = Select.ourBases().list();
        if (bases.size() <= 1) {
            return bases.iterator().next();
        }
        else {
            Iterator<Unit> iterator = bases.iterator();
            iterator.next();
            return iterator.next();
        }
    }

    /**
     * Returns first idle our unit of given type or null if no idle units found.
     */
    public static Unit ourOneIdle(UnitType type) {
        for (Unit unit : Atlantis.getBwapi().self().getUnits()) {
            if (unit.isCompleted() && unit.isIdle() && unit.getType().equals(type)) {
                return unit;
            }
        }
        return null;
    }
    
    /**
     * Returns nearest enemy to the given position (or unit).
     */
    public static Unit nearestEnemy(Position position) {
        return Select.enemy().nearestTo(position);
    }

    // =========================================================
    // Auxiliary methods
    /**
     * Returns <b>true</b> if current selection contains at least one unit.
     */
    public boolean anyExists() {
        return !units.isEmpty();
    }

    /**
     * Returns first unit that matches previous conditions or null if no units match conditions.
     */
    public Unit first() {
        return units.isEmpty() ? null : units.first();
    }

    /**
     * Returns random unit that matches previous conditions or null if no units matched all conditions.
     */
    public Unit random() {
        return units.random();
    }

    // =========================================================
    // Operations on set of units
    /**
     * @return all units except for the given one
     */
    public Select exclude(Unit unitToExclude) {
        units.removeUnit(unitToExclude);
        return this;
    }

    @SuppressWarnings("unused")
    private Select filterOut(Collection<Unit> unitsToRemove) {
        units.removeUnits(unitsToRemove);
        return this;
    }

    // private SelectUnits filterOut(Unit unitToRemove) {
    // // units.removeUnit(unitToRemove);
    // Iterator<Unit> unitsIterator = units.iterator();
    // while (unitsIterator.hasNext()) {
    // Unit unit = unitsIterator.next();
    // if (unitToRemove.equals(unit)) {
    // units.removeUnit(unit);
    // }
    // }
    // return this;
    // }
    @SuppressWarnings("unused")
    private Select filterAllBut(Unit unitToLeave) {
        Iterator<Unit> unitsIterator = units.iterator();
        while (unitsIterator.hasNext()) {
            Unit unit = unitsIterator.next();
            if (unitToLeave != unit) {
                units.removeUnit(unit);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return units.toString();
    }

    // =========================================================
    // Get results
    /**
     * Selects units that match all previous criteria. <b>Units</b> class is used as a wrapper for result. See
     * its javadoc too learn what it can do.
     */
    public Units units() {
        return units;
    }

    /**
     * Selects units as an iterable collection (list).
     */
    public Collection<Unit> list() {
        return units().list();
    }

    /**
     * Returns number of units matching all previous conditions.
     */
    public int count() {
        return units.size();
    }

}
