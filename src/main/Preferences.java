package main;
public class Preferences {

    /** Controls whether flags should be shown or not. Default: true */
    private static boolean showFlags = true;

    /**
     * Controls whether the user can click on a bomb that they have already clicked
     * on before. False means that the user can. Default: false
     */
    private static boolean safeMode = false;

    public static boolean shouldShowFlags() {
        return showFlags;
    }

    public static void setShowFlags(boolean showFlags) {
        Preferences.showFlags = showFlags;
    }

    public static boolean isInSafeMode() {
        return safeMode;
    }

    public static void setSafeMode(boolean safeMode) {
        Preferences.safeMode = safeMode;
    }

}
