package asia.tatsujin.www2048;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import asia.tatsujin.www2048.client.WWW2048;

public class MainActivity extends AppCompatActivity {

    private ImageButton playButton;
    private ImageButton rankButton;
    private ImageButton exitButton;

    public static WWW2048 www2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListeners();
        www2048 = new WWW2048(this, getString(R.string.api_base_url));
    }

    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        playButton = (ImageButton) findViewById(R.id.button_play);
        rankButton = (ImageButton) findViewById(R.id.button_rank);
        exitButton = (ImageButton) findViewById(R.id.button_exit);
    }

    private void setListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), GameActivity.class));
            }
        });
        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RankActivity.class));
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.action_about)
                .setView(R.layout.dialog_about)
                .show();
        ((TextView) alertDialog.findViewById(R.id.contributors))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) alertDialog.findViewById(R.id.github))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
