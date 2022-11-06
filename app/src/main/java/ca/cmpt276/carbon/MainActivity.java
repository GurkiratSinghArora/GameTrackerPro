package ca.cmpt276.carbon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.carbon.model.Achievements;
import ca.cmpt276.carbon.model.Game;
import ca.cmpt276.carbon.model.GameConfig;
import android.content.SharedPreferences;
import com.google.gson.Gson;

/**
 * Main activity that initializes a collection of game configurations
 * and lists them on screen. If no list is created, shows a nice welcome
 * message to prompt user to add games. Allows the user to add a new
 * game config or edit the game config by clicking on it once its created
 */
public class MainActivity extends AppCompatActivity {

    // initialize the game config
    private GameConfig gameConfiguration;

    // for displaying the games
    private ListView list;

    // Shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "Game Config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.gameConfigToolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar
        Objects.requireNonNull(getSupportActionBar()).setTitle("COOP Game!");

        // Load the game config
        loadData();

        // TODO REMOVE WHEN TESTING DONE
        addTemporaryGamesForTesting();

        // fill in the items in list view
        populateListView();
        //setupPlusButton(); CAN BE REPLACED WITH COMPACT CODE

        // click function to display info about the item clicked
        registerClickCallback();

        // When pressed, launches add game activity (passes in index of games)
        findViewById(R.id.btnAddGame).setOnClickListener( v -> {
            Intent i = GameConfigActivity.makeLaunchIntent(MainActivity.this, -1);
            startActivity(i);
        });

    }

    // Saves data for next launch
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gameConfiguration);
        editor.putString(NAME, json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME, "");

        if (!json.isEmpty()) {
            GameConfig newConfig = gson.fromJson(json, GameConfig.class);
            GameConfig.setInstance(newConfig);
        }
        gameConfiguration = GameConfig.getInstance();
    }

    // TEMP METHOD TO ADD RANDOM GAMES
    private void addTemporaryGamesForTesting() {
        char c = 'A';
        for (int i = 0; i < 5; i++) {

            Game game = new Game("Game: " + c , 100, 400);
            c++;
            gameConfiguration.addGame(game);
        }

    }

    // To override the start method and display the list refreshed
    @Override
    protected void onStart() {
        super.onStart();

        // Populate list view on startup, if any games
        populateListView();

        // Print welcome message if no games found
        showWelcomeScreen();

    }

    private void showWelcomeScreen() {

        // Assets for welcome image screen
        TextView welcomeScreenMsg = findViewById(R.id.tvPistolPete);
        ImageView welcomeImage = findViewById(R.id.imageViewPeanut);
        ImageView welcomePointer = findViewById(R.id.imgArrow);

        // If listview is empty, show welcome image
        if (gameConfiguration.size() == 0) {
            list.setVisibility(View.GONE);
            welcomeScreenMsg.setVisibility(View.VISIBLE);
            welcomeImage.setVisibility(View.VISIBLE);
            welcomePointer.setVisibility(View.VISIBLE);
        }
        // otherwise, show list
        else {
            list.setVisibility(View.VISIBLE);
            welcomeScreenMsg.setVisibility(View.GONE);
            welcomeImage.setVisibility(View.GONE);
            welcomePointer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Populate list view on startup, if any games
        populateListView();

    }

    // method to give info when item in the list is clicked
    private void registerClickCallback() {
        // Get the list
        ListView list = findViewById(R.id.listViewGameList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent i = GameConfigActivity.makeLaunchIntent(MainActivity.this, position);
                startActivity(i);
            }
        });
    }

    // List of things
    private void populateListView() {

        // make a list of games
        List<String> gameList = new ArrayList<>();

        // for all the games in the manager, convert to string to display on screen
        for (int i = 0; i < gameConfiguration.size(); i++) {

            String name = gameConfiguration.getGame(i).getGameName();
            int ls =  gameConfiguration.getGame(i).getLowScore();
            int hs = gameConfiguration.getGame(i).getHighScore();

            gameList.add(" " + name + ",  " + ls + ", " + hs + " ");
        }

        // adapter to connect between listview and items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                gameList
        );

        // Configure the list view
        list = findViewById(R.id.listViewGameList);
        list.setAdapter(adapter);

        //list.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

    }

    /*private void clickGameList() {

        ListView list = findViewById(R.id.listViewGameList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String message = "You clicked # " + position + ", which is string: " + textView.getText().toString();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                Intent intent = GameConfigActivity.makeLaunchIntent(MainActivity.this, gameConfiguration.getGamesList().indexOf(position));//gameConfiguration.getGamesList().get(position));
                startActivity(intent);
            }
        });
    }*/

    /*private void setupPlusButton() {
        FloatingActionButton btn = findViewById(R.id.btnAddGame);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,GameConfigActivity.class);
                startActivity(intent);
            }
        });
    }*/


    /*private void populateListView() {

        List<String> gameList = gameConfiguration.gameStr();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                gameList
        );

        // configure the list view
        list = findViewById(R.id.listViewGameList);
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }*/

    }

}