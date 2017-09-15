import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI {
    private static final Logger logger = Logger.getLogger(CLI.class.getName());

    /**
     * Reads task data from file, add these tasks to database
     * If no file given, will print out example format of task to use
     * @param args name of file to read task data from
     */
    public static void main(String[] args) {
        new CLI();
    }

    public CLI() {

        DatabaseAdmin databaseAdmin = new DatabaseAdmin();
        System.out.println("**            FREERIDE NEW TASK - COMMAND LINE INTERFACE             **");
        System.out.println("**  type 'create' to generate tasks and add them to the database     **");
        System.out.println("**  type 'delete all tasks' to delete all the tasks in the database  **");
        System.out.println("**  type 'exit' to leave the CLI                                     **");
        System.out.println("**  or type 'info' to see these commands again                       **");
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        String userInput = scanner.nextLine();

        while (!userInput.equalsIgnoreCase("exit")) {
            switch (userInput) {
                case "":
                    System.out.println("Type 'exit' to quit.");
                    break;

                case "create":
                    System.out.println("How many tasks to create? Enter '0' to cancel.");
                    System.out.print(">");
                    String amountString = scanner.nextLine();
                    Integer amount = Integer.parseInt(amountString);
                    if (amount > 0) {
                        System.out.println("Choose tasks state? ('new' or 'available') Enter \"\" to cancel.");
                        System.out.print(">");
                        String state = scanner.nextLine();
                        if (state.equals("new") || state.equals("available")) {
                            databaseAdmin.addTasksArrayToDatabase(generateRandomTasks(amount, state));
                            System.out.println("Added " + amount + " " + state + " tasks to database.");
                        } else {
                            System.out.println(state + " not a valid task state. \uD83D\uDC80 ");
                        }
                    }
                    break;

                case "delete all tasks":
                    System.out.println("Are you sure you want to delete all the tasks in the database?");
                    System.out.println("Type 'yes' to confirm or 'no' to cancel");
                    System.out.print(">");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("yes")) {
                        databaseAdmin.deleteAllTasks();
                        System.out.println("Deleted all tasks from database.");
                    } else {
                        System.out.println("Cancelled");
                    }
                    break;

                case "info":
                    System.out.println("**                     ___CLI__INFO__PAGE___                         **");
                    System.out.println("**  type 'create' to generate tasks and add them to the database     **");
                    System.out.println("**  type 'delete all tasks' to delete all the tasks in the database  **");
                    System.out.println("**  type 'exit' to leave the CLI.                                    **");
                    System.out.println("**  or type 'info' to see these commands again                       **");
                    break;

                default:
                    System.out.println("'" + userInput + "' is not a recognised command.");
                    break;
            }
            System.out.print(">");
            userInput = scanner.nextLine();
        }
    }

    /**
     * Returns an Arraylist of a specified amount of randomly generated tasks.
     * State specified the state of the task when created.
     * Valid states: 'new', 'available'. Also: 'accepted', 'pending', 'completed'.
     * todo validation. add states as consts (to VALUES)
     * todo make task Builder
     * Location and incentive are randomly generated using guassian distribution.
     * @param amount of tasks to generate and return
     * @param state of generated tasks
     * @return list of tasks generated
     */
    public static ArrayList<Task> generateRandomTasks(int amount, String state) {
        logger.log(Level.INFO, "Generating " + amount + " Random Tasks");

        ArrayList<Task> tasks = new ArrayList<>();
        for (int i=0; i<amount; i++) {
            tasks.add(generateRandomTask(state));
        }
        return tasks;
    }

    /**
     * Location and incentive are randomly generated using guassian distribution.
     * @return generated task
     */
    public static Task generateRandomTask(String state) {
        logger.log(Level.INFO, "Generating Random Task");

        return new Task(
                gaussianRandom(51, 1, true, 5),
                gaussianRandom(-1, 1, false, 5),
                gaussianRandom(51, 1, true, 5),
                gaussianRandom(-1, 1, false, 5),
                LocalDateTime.now().toString(),
                null,
                "Generated",
                "Randomly generated task values.",
                state,
                null,
                gaussianRandomInt(0, 1000));
    }

    private static int gaussianRandomInt(int mean, int variance) {
        return mean + (int) Math.abs((new Random().nextGaussian() * variance));
    }

    private static double gaussianRandom(double mean, double variance, boolean oneDirection, int decimalPlaces){ //well that variable name will have to be changed
        double diffFromMean = new Random().nextGaussian() * variance;
        if (oneDirection) {
            diffFromMean = Math.abs(diffFromMean);
        }
        double result = mean + diffFromMean;
        BigDecimal bd = new BigDecimal(result);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
