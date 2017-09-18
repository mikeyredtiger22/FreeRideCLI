import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseAdmin {

    private static final Logger logger = Logger.getLogger(DatabaseAdmin.class.getName());
    private DatabaseReference oneLocTasksRef;
    private DatabaseReference twoLocTasksRef;

    /**
     * Authenticate Server - Using Database Admin API
     */
    public DatabaseAdmin() {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(VALUES.FILE_PATH_TO_DB_CREDENTIALS);
            // Initialize the app with a custom auth variable
            Map<String, Object> auth = new HashMap<String, Object>();
            auth.put("uid", VALUES.DB_ADMIN_AUTH_VALUE);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl(VALUES.FIREBASE_DB_URL)
                    .setDatabaseAuthVariableOverride(auth)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {e.printStackTrace();}
        FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
        oneLocTasksRef = databaseInstance.getReference("tasks/oneLoc");
        twoLocTasksRef = databaseInstance.getReference("tasks/twoLoc");
    }

    /**
     * Receives an array of tasks, adds them to the same path in the database individually
     * @param oneLocationTasks true for one location tasks, false for two location (start and end) tasks
     * @param taskArray tasks
     */
    public void addTasksArrayToDatabase(boolean oneLocationTasks, ArrayList<Task> taskArray) {
        DatabaseReference tasksRef = oneLocationTasks ? oneLocTasksRef : twoLocTasksRef;
        Gson gson = getGson();
        for (Task task :taskArray) {
            addTaskToDatabase(tasksRef, task, gson);
        }
    }

    /**
     * Adds task object to the database under a unique generated key. This key is created by the
     * database. This key is then set as the taskId of the object before committing it to the
     * database.
     * @param databasePath to store task
     * @param newTask to add to database
     * @param gson with DateTime serializer
     */
    private void addTaskToDatabase(DatabaseReference databasePath, Task newTask, Gson gson) {
        logger.log(Level.INFO, "Adding task object to DB");
        //Generates a unique key to store the task under (becomes the taskId)
        DatabaseReference newTaskRef = databasePath.push();
        String taskId = newTaskRef.getKey();
        newTask.setTaskId(taskId);
        String taskJson = gson.toJson(newTask);
        newTaskRef.setValue(taskJson);
    }

    public void deleteAllTasks() {
        oneLocTasksRef.removeValue();
        twoLocTasksRef.removeValue();
    }

    private Gson getGson(){
        return Converters.registerLocalDateTime(new GsonBuilder()).create();
    }
}