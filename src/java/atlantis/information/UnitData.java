package atlantis.information;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * Stores information about units in order to retrieve them when 
 * they are out of sight
 * @author Anderson
 *
 */
public class UnitData {

	private Position position;
	private Unit unit;
	private UnitType type;
	
	public UnitData(Unit u){
		unit = u;
		position = u.getPosition();
		type = u.getType();
	}
	
	public UnitType getType(){
		return type;
	}
	
	public Unit getUnit(){
		return unit;
	}
	
	public Position getPosition(){
		return position;
	}
	
	public UnitData update(Unit updated){
		if (updated.getID() != unit.getID()){
			throw new RuntimeException(
				String.format("Unexpected unit ID. Expected %d, received %d", unit.getID(), updated.getID())
			);
		}
		position = updated.getPosition();
		type = unit.getType();
		
		return this;
	}
}
