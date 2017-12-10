package lyaaronresumecv.bit.bitprofit;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt;

    private int usdPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        In the case we want to add another View
        txt = (TextView) findViewById(R.id.txt);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait ...");
        */
    }


    //AGREGAR BOTON DE ACTUALIZAR(las flechitas dando vueltas) PARA QUE JALE EL NUEVO PRECIO CUANDO EL USUARIO QUIERA
    private void load() {
        Request request = new Request.Builder().url(BPI_ENDPOINT).build();
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
                        //progressDialog.dismiss(); //No usé progress dialog
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
            txt.setText(builder.toString());
            */
        } catch(Exception e) {

        }
    }
}
