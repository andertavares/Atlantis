package atlantis.production.strategies;

import atlantis.AtlantisConfig;
import atlantis.wrappers.SelectUnits;
import java.util.ArrayList;
import bwapi.Unit;
import bwapi.UnitType;

public class TerranProductionStrategy extends AtlantisProductionStrategy {

    @Override
    protected String getFilename() {
        return "TerranDefault.csv";
    }

    @Override
    public void produceWorker() {
        Unit building = SelectUnits.ourOneIdle(AtlantisConfig.BASE);
        if (building != null) {
            building.train(AtlantisConfig.WORKER);
        }
    }

    @Override
    public void produceInfantry(UnitType infantryType) {
        Unit building = SelectUnits.ourOneIdle(AtlantisConfig.BARRACKS);
        if (building != null) {
            building.train(infantryType);
        }
    }

    @Override
    public ArrayList<UnitType> produceWhenNoProductionOrders() {
        ArrayList<UnitType> units = new ArrayList<>();
        
        int marines = SelectUnits.our().countUnitsOfType(UnitType.Terran_Marine);
        int medics = SelectUnits.our().countUnitsOfType(UnitType.Terran_Medic);
        
        if ((double) marines / medics < 3) {
            units.add(UnitType.Terran_Marine);
        }
        else {
            units.add(UnitType.Terran_Medic);
        }
        return units;
    }

}
