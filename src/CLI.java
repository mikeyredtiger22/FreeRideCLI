import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
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

        while (true) {

            printInfoPage();
            int mainMenuReply = getReply("", new String[]{"create", "info", "delete all tasks", "close"});
            switch (mainMenuReply) {
                case -1: //exit
                    System.out.println("Type 'close' to quit.");
                    break;

                case 0: //create



                    int amountReply = getReplyInt("How many tasks to create?", 100); //max 100
                    if (amountReply == -1) { //exit
                        continue;
                    }

                    createTasks(
                            amountReply, 1, 0,
                            amountReply, 1,
                            0, 1,
                            50.9368, -1.396, 100);
                    break;
                    /*
                    int notifyReply = getReply("Do you want to send a notification of the tasks to all available users?",
                            new String[]{"yes", "no"});
                    int availableReply = 0; //yes
                    if (notifyReply == -1) { //exit
                        continue;
                    }
                    if (notifyReply == 0) { //yes
                        availableReply = getReply("Do you want the tasks to be available (searchable) by all users?",
                                new String[]{"yes", "no"});
                        if (availableReply == -1) { //exit
                            continue;
                        }
                    }
                    int locationCount = getReplyInt("How many locations/steps do you want the tasks to have?", 10); //max 10
                    if (locationCount == -1) { //exit
                        continue;
                    }
                    int orderedReply = 1; //no
                    int directionsReply = 1; //no
                    int directionsTypeReply = 1; //driving
                    if (locationCount > 1) {
                        orderedReply = getReply("Do you want the locations/steps to be completed in order?",
                                new String[]{"yes", "no"});
                        if (orderedReply == -1) { //exit
                            continue;
                        }
                        String directionsQuestion = "Do you want to generate directions for the task?";
                        if (orderedReply == 1) { //no
                            directionsQuestion += "\n(Directions for unordered locations will take longer to calculate the optimum route)";
                        }
                        directionsReply = getReply(directionsQuestion,
                                new String[]{"yes", "no"});
                        if (directionsReply == -1) { //exit
                            continue;
                        }
                        directionsTypeReply = getReply("Do you want to generate walking or driving directions?",
                                new String[]{"walking", "driving"});
                        if (directionsTypeReply == -1) { //exit
                            continue;
                        }
                    }
                    double latitudeReply = getReplyDouble("Enter base latitude of tasks", 90);
                    if (latitudeReply == -1000) { //exit
                        continue;
                    }
                    double longitudeReply = getReplyDouble("Enter base longitude of tasks", 180);
                    if (longitudeReply == -1000) { //exit
                        continue;
                    }
                    int varianceReply = getReplyInt("Enter the variance of the location in metres", 10000); //10km max
                    if (varianceReply == -1) { //exit
                        continue;
                    }
                    createTasks(
                            amountReply, notifyReply, availableReply,
                            locationCount, orderedReply,
                            directionsReply, directionsTypeReply,
                            latitudeReply, longitudeReply, varianceReply);

                    System.out.println("Adding " + amountReply + " tasks to database.");
                    break;
*/
                case 1: //info (creating tasks)
                    //TODO
                    break;

                case 2: //delete all tasks
                    int deleteReply = getReply("Are you sure you want to delete all the tasks in the database?\n" +
                            "Type 'yes' to confirm or 'no' to cancel", new String[]{"yes", "no"});
                    if (deleteReply == -1) { //exit
                        continue;
                    } else if (deleteReply == 0) { //yes
                        databaseAdmin.deleteAllTasks();
                        System.out.println("Deleted all tasks from database.");
                    }
                    break;

                case 3: //close
                    System.exit(777);
                    break;
            }
        }
    }

    //Returns index of valid input that was input by the user, or -1 to exit
    private int getReply(String output, String[] validInputs) {
        //loops until user enters valid response or 'exit'
        while (true) {
            System.out.println(output);
            System.out.print(">");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("exit")) {
                return -1;
            } else {
                for (int index = 0; index < validInputs.length; index++) {
                    if (input.equals(validInputs[index])) {
                        return index;
                    }
                }
            }
            //Input doesn't match any recognised commands
            System.out.println("'" + input + "' is not a recognised command");

            StringBuilder validInputsString = new StringBuilder();
            validInputsString.append(validInputs[0]);
            for (int index = 1; index < validInputs.length; index++) {
                validInputsString.append(", ").append(validInputs[index]);
            }
            System.out.println("Please enter one of the following: " + validInputsString.toString());
            System.out.println("Or type 'exit' to cancel and return to the main menu");
        }
    }

    //Returns integer entered input by the user, or -1 to exit
    private int getReplyInt(String output, int limit) {
        while (true) {
            System.out.println(output);
            System.out.print(">");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                return -1;
            } else {
                int value;

                try {
                    value = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("'" + input + "' is not an integer");
                    System.out.println("Enter an integer or type 'exit' to cancel and return to the main menu");
                    continue;
                }

                if (value > 0 && value <= limit) {
                    return value;
                } else {
                    System.out.println("Please enter a value greater than 0 and at most " + limit);
                }
            }
        }
    }

    //Returns decimal entered input by the user, or -1 to exit
    private double getReplyDouble(String output, double limit) {
        while (true) {
            System.out.println(output);
            System.out.print(">");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                return -1000;
            } else {
                double value;

                try {
                    value = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    System.out.println("'" + input + "' is not a number");
                    System.out.println("Enter a decimal or type 'exit' to cancel and return to the main menu");
                    continue;
                }

                if (value < limit && value > -limit) {
                    return value;
                } else {
                    System.out.println("Please enter a value greater than " + limit + " and greater than -" + limit);
                }
            }
        }
    }

    private void printInfoPage() {

        System.out.println(" _______________________________________________________________________ ");
        System.out.println("|              FREERIDE NEW TASK - COMMAND LINE INTERFACE               |");
        System.out.println("|                                                                       |");
        System.out.println("|  type 'create' to generate tasks and add them to the database         |");
        System.out.println("|  type 'info' for an explanation of creating tasks                     |");
        System.out.println("|  type 'delete all tasks' to delete all the tasks in the database      |");
        System.out.println("|  type 'close' to close the CLI                                        |");
        System.out.println("|  type 'exit' during any operation to cancel and return to this menu   |");
        System.out.println("|_______________________________________________________________________|");
        //TODO explain what questions will be asked when creating tasks and how task state affects it.

    }


    private void createTasks(int amount, int notifyReply, int availableReply,
                             int locationCount, int orderedReply,
                             int directionsReply, int directionsTypeReply,
                             double latitude, double longitude, int variance) {
        //converting yes/no answers to boolean values
        boolean notify = notifyReply == 0;
        boolean available = availableReply == 0;
        boolean ordered = orderedReply == 0;
        boolean directions = directionsReply == 0;
        boolean walkingDirections = directionsTypeReply == 0;

        databaseAdmin.addTasksArrayToDatabase(
                generateRandomTasks(
                        amount, notify, available,
                        locationCount, ordered,
                        directions, walkingDirections,
                        latitude, longitude,  variance));
    }

    /**
     * Returns an Arraylist of a specified amount of randomly generated tasks.
     * State specified the state of the task when created.
     * Valid states: 'new', 'available'. Also: 'accepted', 'pending', 'completed'.
     * Location and incentive are randomly generated using guassian distribution.
     * @return list of tasks generated
     */
    public static ArrayList<Task> generateRandomTasks(int amount, boolean notify, boolean available,
                                                      int locationCount, boolean ordered,
                                                      boolean directions, boolean walkingDirections,
                                                      double latitude, double longitude, int variance) {
        logger.log(Level.INFO, "Generating " + amount + " Random Tasks");
        //TODO taking a long time to generate and add to db
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i=0; i<amount; i++) {
            tasks.add(
                    generateRandomTask(
                            notify, available,
                            locationCount, ordered,
                            directions, walkingDirections,
                            latitude, longitude,  variance));
        }
        return tasks;
    }

    /**
     * Location and incentive are randomly generated using guassian distribution.
     * @return generated task
     */
    public static Task generateRandomTask(boolean notify, boolean available,
                                          int locationCount, boolean ordered,
                                          boolean directions, boolean walkingDirections,
                                          double latitude, double longitude, int variance) {

        //Generate task locations
        Double[] locationLats = new Double[locationCount];
        Double[] locationLongs = new Double[locationCount];
        String[] locationInstructions = new String[locationCount];
        Boolean[] verified = new Boolean[locationCount];
        Arrays.fill(verified, false);

        Random random = new Random();
        for (int locationIndex = 0; locationIndex < locationCount; locationIndex++) {

            //Use Random to generate a distance (gaussian, metres) and a direction (bearing in radians)
            double distance = variance * Math.abs(random.nextGaussian());
            double bearing = random.nextDouble() * 2 * Math.PI;
            double cosBearing = Math.cos(bearing);
            double sinBearing = Math.sin(bearing);

            //Turn randomly generated distance and direction to lat/longitude difference
            double angularDistance = distance / 6371000.0;
            double cosAngularDistance = Math.cos(angularDistance);
            double sinAngularDistance = Math.sin(angularDistance);

            double baseLat = Math.toRadians(latitude);
            double baseLong = Math.toRadians(longitude);
            double cosBaseLat = Math.cos(baseLat);
            double sinBaseLat = Math.sin(baseLat);

            //These values represent the change in lat/longitude
            double latVariance =   Math.asin( cosAngularDistance * sinBaseLat
                                            + sinAngularDistance * cosBaseLat * cosBearing);
            double longVariance = Math.atan2( sinAngularDistance * cosBaseLat * sinBearing,
                                              cosAngularDistance - sinBaseLat * sinBaseLat ) + baseLong;

            //Adding generated distance and direction to base location
            System.out.println("lat: " + latitude + " var:" + Math.toDegrees(latVariance));
            System.out.println("long: " + longitude + " var:" + Math.toDegrees(longVariance));
            locationLats[locationIndex] = Math.toDegrees(latVariance);
            locationLongs[locationIndex] = Math.toDegrees(longVariance);
            locationInstructions[locationIndex] = "Step " + locationIndex + ". Instructions.";
        }


        //Set other task fields to defaults
        LocalDateTime creationLocalDateTime = LocalDateTime.now();
        LocalDateTime expirationLocalDateTime = null;
        String title = "Generated task";
        String description = "Generated task";
        String state = "";
        if (notify) {
            state += "notify";
        }
        if (available) {
            state += "available";
        }
        String user = null;
        Integer incentive = gaussianRandomInt(0, 1000);

        //Directions
        String[] locationAddresses = new String[locationCount];
        String directionsPath = null;
        String directionsDistance = null;
        String directionsDuration = null;


        //TODO start and last locations are not optimised
        //TODO directions..very slow? try to get address from directions result?
        for (int locationIndex = 0; locationIndex < locationCount; locationIndex++) {
            LatLng location = new LatLng(locationLats[locationIndex], locationLongs[locationIndex]);
            locationAddresses[locationIndex] = DirectionsLoader.getLocationAddress(location);
        }
        if (directions) {
            DirectionsResult directionsResult =
                    DirectionsLoader.getTaskDirections(locationLats, locationLongs, ordered, walkingDirections);
            if (directionsResult.routes.length > 0) {
                DirectionsLeg routeData = directionsResult.routes[0].legs[0];
                directionsPath = directionsResult.routes[0].overviewPolyline.getEncodedPath();
                directionsDistance = routeData.distance.humanReadable;
                directionsDuration = routeData.duration.humanReadable;
            }
        }

        return new Task(locationCount, ordered, locationLats, locationLongs, locationInstructions, locationAddresses,
                verified, creationLocalDateTime, expirationLocalDateTime,
                title, description, state, user, incentive,
                directions, directionsPath, directionsDistance, directionsDuration);

    }

    private static int gaussianRandomInt(int mean, int variance) {
        return mean + (int) Math.abs((new Random().nextGaussian() * variance));
    }
}
