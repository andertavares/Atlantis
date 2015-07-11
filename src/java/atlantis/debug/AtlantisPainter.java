package atlantis.debug;

import java.util.ArrayList;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.util.BWColor;
import atlantis.Atlantis;
import atlantis.AtlantisGame;
import atlantis.production.ProductionOrder;
import atlantis.util.RUtilities;
import atlantis.wrappers.SelectUnits;

/**
 * Here you can include code that will draw things over units etc.
 */
public class AtlantisPainter {

	private static JNIBWAPI bwapi;
	private static int sideMessageCounter = 0;

	// =========================================================

	/**
	 * Executed once per frame, at the end of all other actions.
	 */
	public static void paint() {
		sideMessageCounter = 0;
		bwapi = Atlantis.getBwapi();

		// =========================================================

		bwapi.drawTargets(true); // Draws line from unit to the target position
		// bwapi.getMap().drawTerrainData(bwapi);

		// =========================================================
		// Paint from least important to most important (last is on the top)

		paintColorCirclesAroundUnits();
		paintConstructionProgress();
		paintBuildingHealth();
		paintUnitsBeingTrainedInBuildings();
		paintSpecialsOverUnits();
		paintProductionQueue();

		// =========================================================
		// Paint TOOLTIPS over units
		for (Unit unit : SelectUnits.our().list()) {
			if (unit.hasTooltip()) {
				bwapi.drawText(unit, unit.getTooltip(), false);
			}
		}
	}

	// =========================================================
	// Hi-level

	/**
	 * Paints next units to build in top left corner.
	 */
	private static void paintProductionQueue() {

		// Display units currently in production
		for (Unit unit : SelectUnits.ourUnfinished().list()) {
			paintSideMessage(unit.getType().getShortName(), BWColor.Green);
		}

		// Display units that should be produced right now or any time
		ArrayList<ProductionOrder> produceNow = AtlantisGame.getProductionStrategy().getThingsToProduceRightNow(false);
		for (ProductionOrder order : produceNow) {
			paintSideMessage(order.getShortName(), BWColor.Yellow);
		}

		// Display next units to produce
		ArrayList<ProductionOrder> fullQueue = AtlantisGame.getProductionStrategy().getProductionQueueNext(
				6 - produceNow.size());
		for (int index = produceNow.size(); index < fullQueue.size(); index++) {
			ProductionOrder order = fullQueue.get(index);
			if (order != null && order.getShortName() != null) {
				paintSideMessage(order.getShortName(), BWColor.Red);
			}
		}
	}

	/**
	 * Paints circles around units which mean what's their mission.
	 */
	private static void paintColorCirclesAroundUnits() {
		for (Unit unit : SelectUnits.ourCombatUnits().list()) {

			// STARTING ATTACK
			if (unit.isStartingAttack()) {
				bwapi.drawCircle(unit, 12, BWColor.Red, false, false);
				bwapi.drawCircle(unit, 11, BWColor.Red, false, false);
			}

			// ATTACKING
			if (unit.isAttacking()) {
				bwapi.drawCircle(unit, 10, BWColor.Orange, false, false);
				bwapi.drawCircle(unit, 9, BWColor.Orange, false, false);
			}

			// MOVE
			if (unit.isMoving()) {
				bwapi.drawCircle(unit, 8, BWColor.Teal, false, false);
				bwapi.drawCircle(unit, 7, BWColor.Teal, false, false);
			}
		}
	}

	/**
	 * Paints small progress bars over units that have cooldown.
	 */
	private static void paintSpecialsOverUnits() {
		for (Unit unit : SelectUnits.ourCombatUnits().list()) {

			// =========================================================
			// === Paint cooldown progress bars over units
			// =========================================================
			if (unit.getGroundWeaponCooldown() > 0) {
				int cooldownWidth = 20;
				int cooldownHeight = 4;
				int cooldownLeft = unit.getPX() - cooldownWidth / 2;
				int cooldownTop = unit.getPY() + 23;
				String cooldown = BWColor.getColorString(BWColor.Yellow) + "(" + unit.getGroundWeaponCooldown() + ")";

				Position topLeft = new Position(cooldownLeft, cooldownTop);

				// =========================================================
				// Paint box
				int cooldownProgress = cooldownWidth * unit.getGroundWeaponCooldown()
						/ (unit.getType().getGroundWeapon().getDamageCooldown() + 1);
				bwapi.drawBox(topLeft, new Position(cooldownLeft + cooldownProgress, cooldownTop + cooldownHeight),
						BWColor.Red, true, false);

				// =========================================================
				// Paint box borders
				bwapi.drawBox(topLeft, new Position(cooldownLeft + cooldownWidth, cooldownTop + cooldownHeight),
						BWColor.Black, false, false);

				// =========================================================
				// Paint label
				bwapi.drawText(new Position(cooldownLeft + cooldownWidth - 4, cooldownTop), cooldown, false);
			}

			// =========================================================
			// === Paint battle group
			// =========================================================
			if (unit.getGroup() != null) {
				bwapi.drawText(new Position(unit.getPX() - 5, unit.getPY() - 7), BWColor.getColorString(BWColor.Grey)
						+ "#" + unit.getGroup().getID(), false);
			}

			// =========================================================
			// === Paint num of other units around this unit
			// =========================================================
			int ourAround = SelectUnits.ourCombatUnits().inRadius(1.7, unit).count();
			bwapi.drawText(new Position(unit.getPX() - 7, unit.getPY() - 15), BWColor.getColorString(BWColor.Orange)
					+ "(" + ourAround + ")", false);
		}
	}

	/**
	 * Paints progress bar with percent of completion over all buildings under construction.
	 */
	private static void paintConstructionProgress() {
		for (Unit unit : SelectUnits.ourBuildingsIncludingUnfinished().list()) {
			if (unit.isCompleted()) {
				continue;
			}

			String stringToDisplay;

			int labelMaxWidth = 56;
			int labelHeight = 6;
			int labelLeft = unit.getPX() - labelMaxWidth / 2;
			int labelTop = unit.getPY() + 13;

			double progress = (double) unit.getHP() / unit.getType().getMaxHitPoints();
			int labelProgress = (int) (1 + 99 * progress);
			String color = RUtilities.assignStringForValue(
					progress,
					1.0,
					0.0,
					new String[] { BWColor.getColorString(BWColor.Red), BWColor.getColorString(BWColor.Yellow),
							BWColor.getColorString(BWColor.Green) });
			stringToDisplay = color + labelProgress + "%";

			// Paint box
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth * labelProgress
					/ 100, labelTop + labelHeight), BWColor.Blue, true, false);

			// Paint box borders
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth, labelTop
					+ labelHeight), BWColor.Black, false, false);

			// Paint label
			bwapi.drawText(new Position(labelLeft + labelMaxWidth / 2 - 8, labelTop - 3), stringToDisplay, false);

			// Display name of unit
			String name = unit.getBuildType().getShortName();
			bwapi.drawText(new Position(unit.getPX() - 25, unit.getPY() - 4), BWColor.getColorString(BWColor.Green)
					+ name, false);
		}
	}

	/**
	 * For buildings not 100% healthy, paints its hit points using progress bar.
	 */
	private static void paintBuildingHealth() {
		for (Unit unit : SelectUnits.ourBuildings().list()) {
			if (!unit.isWounded()) {
				continue;
			}
			int labelMaxWidth = 56;
			int labelHeight = 6;
			int labelLeft = unit.getPX() - labelMaxWidth / 2;
			int labelTop = unit.getPY() + 13;

			double hpRatio = (double) unit.getHP() / unit.getType().getMaxHitPoints();
			int hpProgress = (int) (1 + 99 * hpRatio);

			BWColor color = BWColor.Green;
			if (hpRatio < 0.66) {
				color = BWColor.Yellow;
				if (hpRatio < 0.33) {
					color = BWColor.Red;
				}
			}

			// Paint box
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth * hpProgress / 100,
					labelTop + labelHeight), color, true, false);

			// Paint box borders
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth, labelTop
					+ labelHeight), BWColor.Black, false, false);
		}
	}

	/**
	 * If buildings are training units, it paints what unit is trained and the progress.
	 */
	private static void paintUnitsBeingTrainedInBuildings() {
		for (Unit unit : SelectUnits.ourBuildingsIncludingUnfinished().list()) {
			if (!unit.isBuilding() || !unit.isTraining()) {
				continue;
			}

			int labelMaxWidth = 100;
			int labelHeight = 10;
			int labelLeft = unit.getPX() - labelMaxWidth / 2;
			int labelTop = unit.getPY() + 5;

			int operationProgress = 1;
			Unit trained = unit.getBuildUnit();
			String trainedUnitString = "";
			if (trained != null) {
				operationProgress = trained.getHP() * 100 / trained.getMaxHP();
				trainedUnitString = trained.getShortName();
			}

			// Paint box
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth * operationProgress
					/ 100, labelTop + labelHeight), BWColor.White, true, false);

			// Paint box borders
			bwapi.drawBox(new Position(labelLeft, labelTop), new Position(labelLeft + labelMaxWidth, labelTop
					+ labelHeight), BWColor.Black, false, false);

			// =========================================================
			// Display label

			bwapi.drawText(new Position(unit.getPX() - 4 * trainedUnitString.length(), unit.getPY() + 16),
					BWColor.getColorString(BWColor.White) + trainedUnitString, false);
		}
	}

	// =========================================================
	// Lo-level

	private static void paintSideMessage(String text, BWColor color) {
		int screenX = 10;
		int screenY = 10 + 15 * sideMessageCounter;
		paintMessage(text, color, screenX, screenY, true);

		sideMessageCounter++;
	}

	private static void paintMessage(String text, BWColor color, int x, int y, boolean screenCoord) {
		bwapi.drawText(new Position(x, y), BWColor.getColorString(color) + text, screenCoord);
	}

}