package asia.tatsujin.www2048;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import asia.tatsujin.www2048.client.data.Player;
import asia.tatsujin.www2048.client.data.Players;
import asia.tatsujin.www2048.client.tool.ResponseListener;

public class RankActivity extends AppCompatActivity {

    private LinearLayout scoresView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        scoresView = (LinearLayout) findViewById(R.id.view_scores);
        MainActivity.www2048.getPlayers(new ResponseListener<Players>() {
            @Override
            public void onResponse(Players players) {
                int i = 1;
                for (Player player : players.getPlayers()) {
                    RelativeLayout scoreRow = (RelativeLayout) getLayoutInflater().inflate(R.layout.row_score, null);
                    ((TextView) scoreRow.findViewById(R.id.text_rank)).setText(i++ + ".");
                    ((TextView) scoreRow.findViewById(R.id.text_name)).setText(player.getName());
                    ((TextView) scoreRow.findViewById(R.id.text_score)).setText(String.valueOf(player.getScore()));
                    scoresView.addView(scoreRow);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
