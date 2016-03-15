package atlantis.debug.tooltip;

import java.util.HashMap;
import bwapi.Unit;

public class TooltipManager {
	HashMap<Unit, Tooltip> tooltips;
	
	private static TooltipManager instance = null; //singleton
	
	private TooltipManager() {
		tooltips = new HashMap<>();
	}
	
	public static TooltipManager getInstance(){
		if (instance == null){
			instance = new TooltipManager();
		}
		
		return instance;
	}
	
	/**
	 * Sets the tooltip for a given unit
	 * @param unit
	 * @param tooltip
	 * @return
	 */
	public TooltipManager setTooltip(Unit unit, String tooltip){
		
		tooltips.put(unit, new Tooltip(unit, tooltip));
		
		return this;
	}

	
	/**
	 * Returns the tooltip associated with a given unit
	 * @param unit
	 * @return
	 */
    public String getTooltip(Unit unit) {
    	
    	if(! tooltips.containsKey(unit)) return null;
    	
    	return tooltips.get(unit).getTooltip();
    		
    }

    /**
     * Removes the tooltip associated with a unit
     * @param unit
     * @return
     */
	public TooltipManager removeTooltip(Unit unit) {
		if(! tooltips.containsKey(unit)) return null;
		
		tooltips.get(unit).removeTooltip();

		return this;
	}

	/**
	 * 
	 * @param unit
	 * @return
	 */
	public boolean hasTooltip(Unit unit) {
		if(! tooltips.containsKey(unit)) return false;
		
		return tooltips.get(unit).hasTooltip();
	}
}
