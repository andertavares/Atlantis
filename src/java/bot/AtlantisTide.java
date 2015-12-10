package bot;

import atlantis.Atlantis;

/**
 * This is the main class of the bot. Here everything starts.
 *
 * "A journey of a thousand miles begins with a single step." - Lao Tse
 */
public class AtlantisTide {

    /**
     * Sets up Atlantis config and runs the bot.
     */
    public static void main(String[] args) {

        // =============================================================
        // =============================================================
        // ==== See AtlantisConfig class to customize execution ========
        // =============================================================
        // =============================================================
        //
        // Create Atlantis object to use for this bot. It wraps JNIBWAPI functionality.
        Atlantis atlantis = new Atlantis();

        // Starts bot.
        atlantis.start();
    }

}
