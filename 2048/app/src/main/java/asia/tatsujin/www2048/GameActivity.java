package asia.tatsujin.www2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import asia.tatsujin.www2048.client.data.Player;

public class GameActivity extends AppCompatActivity {

    static class Direction {
        final static int RIGHT = 0;
        final static int DOWN  = 1;
        final static int LEFT  = 2;
        final static int UP    = 3;
        final static int ORDER[][][] = {
                {{3, 2, 1, 0}, {7, 6, 5, 4}, {11, 10, 9, 8}, {15, 14, 13, 12}},
                {{12, 8, 4, 0}, {13, 9, 5, 1}, {14, 10, 6, 2}, {15, 11, 7, 3}},
                {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}},
                {{0, 4, 8, 12}, {1, 5, 9 ,13}, {2, 6, 10 ,14}, {3, 7, 11, 15}}
        };
    }

    private Random random;
    private GestureDetectorCompat gestureDetectorCompat;
    private List<Tile> tiles;

    private TextView scoreText;
    private TextView bestScoreText;
    private LinearLayout board;
    private Button newGameButton;

    private int score;
    private int bestScore;
    private boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initVariables();
        initViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isStart = sharedPreferences.getBoolean("is_start", false);
        if (isStart) {
            score = 0;
            addScore(sharedPreferences.getInt("score", 0));
            int i = 0;
            for (Tile tile : tiles) {
                tile.show(sharedPreferences.getInt("tile_" + i, 0));
                i++;
            }
        } else {
            startGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_start", isStart);
        if (isStart) {
            editor.putInt("score", score);
            int i = 0;
            for (Tile tile : tiles) {
                editor.putInt("tile_" + i, tile.getNumber());
                i++;
            }
        }
        editor.apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (isStart)
            gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initVariables() {
        random = new Random();
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                double degree = Math.toDegrees(Math.atan2(velocityY, velocityX));
                if (Math.abs(degree) < 30)
                    move(Direction.RIGHT);
                else if (Math.abs(degree) > 150)
                    move(Direction.LEFT);
                else if (degree > 60 && degree < 120)
                    move(Direction.DOWN);
                else if (degree < -60 && degree > -120)
                    move(Direction.UP);
                return true;
            }
        });
        tiles = new ArrayList<>();
        bestScore = PreferenceManager.getDefaultSharedPreferences(this).getInt("player.score", 0);
        isStart = false;
    }

    private void initViews() {
        board = (LinearLayout) findViewById(R.id.linear_layout_board);
        scoreText = (TextView) findViewById(R.id.text_score);
        bestScoreText = (TextView) findViewById(R.id.text_best_score);
        bestScoreText.setText(String.valueOf(bestScore));
        newGameButton = (Button) findViewById(R.id.button_new_game);
        for (int i = 0; i != 4; ++i) {
            LinearLayout tilesRow = new LinearLayout(this);
            tilesRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tilesRow.setLayoutParams(rowLayoutParams);
            for (int j = 0; j != 4; ++j) {
                Tile tile = new Tile(this);
                tile.clear();
                tile.setGravity(Gravity.CENTER);
                tile.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(24, 24, 24, 24);
                tilesRow.addView(tile, layoutParams);
                tiles.add(tile);
            }
            board.addView(tilesRow);
        }
    }

    private void setListeners() {
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }

    private void startGame() {
        scoreText.setText(R.string.zero);
        score = 0;
        for (Tile tile : tiles)
            tile.clear();
        int firstTiles[] = {random.nextInt(16), random.nextInt(15)};
        if (firstTiles[1] >= firstTiles[0])
            firstTiles[1]++;
        tiles.get(firstTiles[0]).show(2);
        tiles.get(firstTiles[1]).show(2);
        isStart = true;
    }

    private void move(int direction) {
        boolean newTileFlag = false;
        boolean addFlag;
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i != 4; ++i) {
            List<Integer> nums = new ArrayList<>();
            int order[] = Direction.ORDER[direction][i];
            addFlag = false;
            for (int j = 0; j != 4; ++j) {
                Tile tile = tiles.get(order[j]);
                int num = tile.getNumber();
                if (num == 0)
                    continue;
                tile.clear();
                int last = nums.size() - 1;
                if (addFlag && num == nums.get(last)) {
                    nums.remove(last);
                    nums.add(num * 2);
                    addScore(num * 2);
                    addFlag = false;
                    newTileFlag = true;
                } else {
                    if (nums.size() != j)
                        newTileFlag = true;
                    nums.add(num);
                    addFlag = true;
                }
            }
            int j = 0;
            for (int num : nums) {
                tiles.get(order[j]).show(num);
                j++;
            }
            for (; j != 4; ++j)
                emptyTiles.add(tiles.get(order[j]));
        }
        if (emptyTiles.size() == 1 && newTileFlag) {
            emptyTiles.get(0).show();
            boolean endFlag = true;
            for (int i = 0; i != 2; ++i) {
                for (int j = 0; j != 4; ++j) {
                    int order[] = Direction.ORDER[i][j];
                    for (int k = 0; k != 3; ++k)
                        if (tiles.get(order[k]).getNumber() == tiles.get(order[k + 1]).getNumber()) {
                            endFlag = false;
                            break;
                        }
                    if (!endFlag)
                        break;
                }
                if (!endFlag)
                    break;
            }
            if (endFlag)
                endGame();
        } else if (newTileFlag && !emptyTiles.isEmpty() ) {
            int newTileIndex = random.nextInt(emptyTiles.size());
            emptyTiles.get(newTileIndex).show();
        }
    }

    private void addScore(int add) {
        score += add;
        scoreText.setText(String.valueOf(score));
        if (score > bestScore) {
            bestScore = score;
            bestScoreText.setText(String.valueOf(bestScore));
        }
    }

    private void endGame() {
        isStart = false;
        if (score >= bestScore) {
            Player player = MainActivity.www2048.getPlayer();
            player.setScore(score);
            MainActivity.www2048.updatePlayer(player);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_over)
            .setMessage(getString(R.string.score_colon) + score)
            .setNegativeButton(R.string.close, null)
            .setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            })
            .show();
    }

    private static class TileColor {
        static int BACKGROUND[] = {
                Color.argb(91, 238, 228, 218),
                Color.parseColor("#eee4da"),
                Color.parseColor("#ede0c8"),
                Color.parseColor("#f2b179"),
                Color.parseColor("#f59563"),
                Color.parseColor("#f67c5f"),
                Color.parseColor("#f65e3b"),
                Color.parseColor("#edcf72"),
                Color.parseColor("#edcc61"),
                Color.parseColor("#edc850"),
                Color.parseColor("#edc53f"),
                Color.parseColor("#edc22e")
        };
        static int BACKGROUND_SUPER = Color.parseColor("#3c3a32");
    }

    class Tile extends TextView {

        private int number;

        public Tile(Context context) {
            super(context);
            number = 0;
        }

        public int getNumber() {
            return number;
        }

        public void show(int number) {
            if (number == 0)
                clear();
            else {
                setText(String.valueOf(number));
                this.number = number;
                if (number <= 2048)
                    setBackgroundColor(TileColor.BACKGROUND[(int) (Math.log(number) / Math.log(2))]);
                else
                    setBackgroundColor(TileColor.BACKGROUND_SUPER);
            }
        }

        public void show() {
            show(random.nextInt(10) == 0 ? 4 : 2);
        }

        public void clear() {
            setText("");
            setBackgroundColor(TileColor.BACKGROUND[0]);
            number = 0;
        }
    }
}
