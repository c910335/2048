package asia.tatsujin.www2048.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import asia.tatsujin.www2048.R;
import asia.tatsujin.www2048.client.data.Player;
import asia.tatsujin.www2048.client.data.Players;
import asia.tatsujin.www2048.client.tool.ResponseListener;

/**
 * Created by tatsujin on 2016/3/17.
 */
public class WWW2048 {

    private String baseURL;
    private Player player;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    public WWW2048(final Context context, String baseURL) {
        this.baseURL = baseURL;
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        player = new Player();
        player.setToken(sharedPreferences.getString("player.token", null));
        if (player.getToken() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_name, null);
            builder.setTitle(R.string.title_input_name)
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createPlayer(((EditText) dialogView.findViewById(R.id.edit_text_name)).getText().toString());
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ((AppCompatActivity) context).finish();
                        }
                    })
                    .show();
        } else {
            player.setName(sharedPreferences.getString("player.name", null));
            player.setScore(sharedPreferences.getInt("player.score", 0));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void getPlayers(ResponseListener<Players> responseListener) {
        get("/player", responseListener, new TypeToken<Players>(){});
    }

    public void updatePlayer(Player player) {
        Map<String, String> params = new HashMap<>();
        params.put("score", String.valueOf(player.getScore()));
        params.put("name", player.getName());
        put("/player/" + this.player.getToken(), params, new ResponseListener<Player>() {
            @Override
            public void onResponse(Player player) {
                setPlayer(player);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        }, new TypeToken<Player>() {
        });
    }

    private void setToken(String token) {
        player.setToken(token);
        sharedPreferences.edit()
                .putString("player.token", token)
                .apply();
    }

    private void setPlayer(Player player) {
        player.setToken(this.player.getToken());
        this.player = player;
        sharedPreferences.edit()
                .putString("player.name", player.getName())
                .putInt("player.score", player.getScore())
                .apply();
    }

    private void createPlayer(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("score", "0");
        params.put("name", name);
        post("/player", params, new ResponseListener<Player>() {
            @Override
            public void onResponse(Player player) {
                setToken(player.getToken());
                setPlayer(player);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        }, new TypeToken<Player>() {
        });
    }

    private <T> void get(String path, final ResponseListener< T > responseListener, final TypeToken typeToken) {
        request(Request.Method.GET, path, null, responseListener, typeToken);
    }

    private <T> void post(String path, Map<String, String> params, final ResponseListener< T > responseListener, final TypeToken typeToken) {
        request(Request.Method.POST, path, params, responseListener, typeToken);
    }

    private <T> void put(String path, Map<String, String> params, final ResponseListener< T > responseListener, final TypeToken typeToken) {
        request(Request.Method.PUT, path, params, responseListener, typeToken);
    }

    private <T> void request(int method, String path, final Map<String, String> params, final ResponseListener< T > responseListener, final TypeToken typeToken) {
        requestQueue.add(new StringRequest(method, baseURL + path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        T data = new Gson().fromJson(response, typeToken.getType());
                        responseListener.onResponse(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.onError(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() {
                return params;
            }
        });
    }

}
