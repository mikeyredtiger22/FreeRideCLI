import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI {
    private static final Logger logger = Logger.getLogger(CLI.class.getName());
    private Scanner scanner;
    private DatabaseAdmin databaseAdmin;

    /**
     * Reads task data from file, add these tasks to database
     * If no file given, will print out example format of task to use
     * @param args name of file to read task data from
     */
    public static void main(String[] args) {
        new CLI();
    }

    public CLI() {

        //TODO, read json file
        this.databaseAdmin = new DatabaseAdmin();
        this.scanner = new Scanner(System.in);


        printInfoPage();
        String userInput = receiveInput("");

        while (!userInput.equalsIgnoreCase("exit")) {
            switch (userInput) {
                case "":
                    System.out.println("Type 'exit' to quit.");
                    break;

                case "create":
                    String amountString = receiveInput("How many tasks to create? Enter '0' to cancel.");
                    Integer amount = Integer.parseInt(amountString);
                    if (amount > 0) {
                        String state = receiveInput("Choose task state ('available' or 'new'). Press Enter to cancel.");
                        if (!state.equals("available") && !state.equals("new")) {
                            System.out.println(state + " not a valid task state. \uD83D\uDC80 ");
                        } else {
                            String locationType = receiveInput("Should tasks have 'one' location or 'two' (start and end) locations?");
                            if (!locationType.equals("one") && !locationType.equals("two")) {
                                System.out.println("Location type can only be 'one' or 'two, you have entered '" + locationType + "'");
                            } else {
                                boolean oneLocationTask = locationType.equals("one");
                                String location = receiveInput("Press Enter to randomly generate task location");
                                //TODO user specify location and variance in miles
                                createTasks(amount, oneLocationTask, state);
                                System.out.println("Added " + amount + " " + state + " tasks to database.");
                            }
                        }
                    }
                    break;

                case "delete all tasks":
                    String response = receiveInput("Are you sure you want to delete all the tasks in the database?\n" +
                                                    "Type 'yes' to confirm or 'no' to cancel");
                    if (response.equalsIgnoreCase("yes")) {
                        databaseAdmin.deleteAllTasks();
                        System.out.println("Deleted all tasks from database.");
                    } else {
                        System.out.println("Cancelled");
                    }
                    break;

                default:
                    System.out.println("'" + userInput + "' is not a recognised command.");
                    break;
            }

            printInfoPage();
            System.out.print(">");
            userInput = receiveInput("");
        }
        System.exit(777);
    }

    private String receiveInput(String output) {
        System.out.println(output);
        System.out.print(">");
        return scanner.nextLine().trim();
    }

    private void printInfoPage() {
        System.out.println("**            FREERIDE NEW TASK - COMMAND LINE INTERFACE             **");
        System.out.println("**  type 'create' to generate tasks and add them to the database     **");
        System.out.println("**  type 'delete all tasks' to delete all the tasks in the database  **");
        System.out.println("**  type 'exit' to leave the CLI                                     **");
        System.out.println("**  or type 'info' to see these commands again                       **");
        //TODO explain what questions will be asked when creating tasks and how task state affects it.

    }

    private void createTasks(int amount, boolean oneLocationTask, String state) {
        databaseAdmin.addTasksArrayToDatabase(oneLocationTask, generateRandomTasks(amount, oneLocationTask, state));
    }

    /**
     * Returns an Arraylist of a specified amount of randomly generated tasks.
     * State specified the state of the task when created.
     * Valid states: 'new', 'available'. Also: 'accepted', 'pending', 'completed'.
     * todo validation. add states as consts (to VALUES)
     * todo make task Builder
     * Location and incentive are randomly generated using guassian distribution.
     * @param amount of tasks to generate and return
     * @param oneLocationTask true for 1 location task, false for 2 location (start and end) task
     * @param state of generated tasks
     * @return list of tasks generated
     */
    public static ArrayList<Task> generateRandomTasks(int amount, boolean oneLocationTask, String state) {
        logger.log(Level.INFO, "Generating " + amount + " Random Tasks");

        ArrayList<Task> tasks = new ArrayList<>();
        for (int i=0; i<amount; i++) {
            tasks.add(generateRandomTask(oneLocationTask, state));
        }
        return tasks;
    }

    /**
     * Location and incentive are randomly generated using guassian distribution.
     * @param oneLocationTask true for 1 location task, false for 2 location (start and end) task
     * @param state of generated tasks
     * @return generated task
     */
    public static Task generateRandomTask(boolean oneLocationTask, String state) {

        Double startLat = gaussianRandom(50.9355, 0.001, false, 5);
        Double startLon = gaussianRandom(-1.397, 0.001, false, 5);
        String startAddress = null;

        Double endLat = null;
        Double endLon = null;
        String endAddress = null;
        String directionsPath = null;
        String directionsDistance = null;
        String directionsDuration = null;

        if (oneLocationTask) {
            startAddress = DirectionsLoader.getLocationAddress(startLat, startLon);
        } else {
            endLat = gaussianRandom(50.9355, 0.001, false, 5);
            endLon = gaussianRandom(-1.397, 0.001, false, 5);

            DirectionsResult directionsResult =
                    DirectionsLoader.getTaskDirections(startLat, startLon, endLat, endLon);

            if (directionsResult.routes.length > 0) {
                DirectionsLeg routeData = directionsResult.routes[0].legs[0];

                directionsPath = directionsResult.routes[0].overviewPolyline.getEncodedPath();
                directionsDistance = routeData.distance.humanReadable;
                directionsDuration = routeData.duration.humanReadable;
                startAddress = routeData.startAddress;
                endAddress = routeData.endAddress;
            }
        }

        return new Task(
                oneLocationTask,
                startLat, startLon, endLat, endLon,
                startAddress, endAddress,
                LocalDateTime.now(), null,
                "Generated", "Randomly generated task values.",
                state,
                null,
                directionsPath, directionsDistance, directionsDuration,
                gaussianRandomInt(0, 1000));
    }

    private static int gaussianRandomInt(int mean, int variance) {
        return mean + (int) Math.abs((new Random().nextGaussian() * variance));
    }

    private static double gaussianRandom(double mean, double variance, boolean absoluteValue, int decimalPlaces){
        double diffFromMean = new Random().nextGaussian() * variance;
        if (absoluteValue) {
            diffFromMean = Math.abs(diffFromMean);
        }
        double result = mean + diffFromMean;
        BigDecimal bd = new BigDecimal(result);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
