package atlantis.debug.tooltip;

import java.util.HashMap;
import bwapi.Unit;

public class TooltipManager {
	private static HashMap<Unit, Tooltip> tooltips = new HashMap<>();
	
	private TooltipManager() { }
	
	/**
	 * Sets the tooltip for a given unit
	 * @param unit
	 * @param tooltip
	 * @return
	 */
	public static void setTooltip(Unit unit, String tooltip){
		
		tooltips.put(unit, new Tooltip(unit, tooltip));
		
	}

	
	/**
	 * Returns the tooltip associated with a given unit
	 * @param unit
	 * @return
	 */
    public static String getTooltip(Unit unit) {
    	
    	if(! tooltips.containsKey(unit)) return null;
    	
    	return tooltips.get(unit).getTooltip();
    		
    }

    /**
     * Removes the tooltip associated with a unit
     * @param unit
     * @return
     */
	public static void removeTooltip(Unit unit) {
		if(! tooltips.containsKey(unit)) return;
		
		tooltips.get(unit).removeTooltip();

	}

	/**
	 * 
	 * @param unit
	 * @return
	 */
	public static boolean hasTooltip(Unit unit) {
		if(! tooltips.containsKey(unit)) return false;
		
		return tooltips.get(unit).hasTooltip();
	}
}
