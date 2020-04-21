package rect_partition.utils;

public class Utils {
    public static void logError(Exception e) {
        System.out.println("Partition Problem Exception: " + e.getMessage());
        if (e.getCause() != null)
            System.out.println(e.getCause());
    }

    public static void clearWindow() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void clearWindow(String defaultText) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(defaultText);
    }
}