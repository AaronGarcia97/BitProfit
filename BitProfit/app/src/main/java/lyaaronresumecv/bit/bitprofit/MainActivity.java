package lyaaronresumecv.bit.bitprofit;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView BitcoinPrice_tv, HoraFecha_tv;
    private Button conversion_btn;
    private AppBarLayout mAppBarLayout;
    private int usdPrice = 0;

    /*
    * We los issues los ponemos en github... ahi te puse algunos comentarios (ELIMINAS ESTE)
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        if (toolbar != null) {
            toolbar.setTitle("Nombre App");
            toolbar.setTitleTextColor(getResources().getColor(R.color.secondaryDarkColor));
            setSupportActionBar(toolbar);
        }

        /*NO QUITAR Aún
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        HoraFecha_tv = (TextView) findViewById(R.id.fecha_hora_tv);
        BitcoinPrice_tv = (TextView) findViewById(R.id.bitcoinprice_tv);
        conversion_btn = (Button) findViewById(R.id.conversion_btn);
        conversion_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.conversion_btn:
                Toast.makeText(this, "Botón Conversion ", Toast.LENGTH_SHORT).show();
            break;

        }
    }



    private void load() {
        Request request = new Request.Builder().url(BPI_ENDPOINT).build();

        //HAY ALGO QUE SE ESTÁ HACIENDO MAL AQUI YA QUE NO  SE LLEGA A @ONFALIURE ó @ONRESPONSE
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Error while loading BPI : "
                                + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseBpiResponse(body);
                    }
                });
            }

        });
    }

    private void parseBpiResponse(String body) {
        try {
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = new JSONObject(body);
            JSONObject timeObject = jsonObject.getJSONObject("time");
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");

            //Aquí agarra el rate de USD, con el rate podemos crear int para hacer las operaciones despues
            String usdPriceRate = usdObject.getString("rate");
            usdPrice = Integer.parseInt(usdPriceRate); //Global variable la iguala al precio de USD
            builder.append("$").append(usdPriceRate).append("\n");

            /*
            builder ya tiene el e.rate en USD, solo que no encuentro el ID de donde mostrarlo
            */
            BitcoinPrice_tv.setText(builder.toString());//<---- ID para mostrarlo, pero nunca llega aquí

        } catch(Exception e) {

        }
    }




/*
* @onCreateOptionsMenu & @onOptionsItemSelected Se refieren al menú, en este caso al botón refresh...
* */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //BOTÓN REFRESH (ID)
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            load();
            /*
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("BPI Loading");     ESTO QUEDA PENDIENTE...
            progressDialog.setMessage("Wait ...");
            */
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
