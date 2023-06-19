/** @author Lucas da Silva Barbosa
 * Matrícula: 202120194
 * Fonte: https://code.tutsplus.com/pt/tutorials/android-from-scratch-using-rest-apis--cms-27117
 * "A persistência é o caminho do êxito." - Charles Chaplin
 */

package com.example.conversordecambioltpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // Variáveis relacionadas à API
    String endpoint = "convert";
    String access_key = "wLb8D1az4ukHYNYpNeYLsxeP57m9G1dT";
    String valorInicial = "BRL";
    String valorFinal = "USD";
    float quantidade = 0, retornoDaAPI = 0;

    // Variáveis da aplicação
    ConstraintLayout contextView;
    EditText inputUSD, inputBRL;
    TextView resultado;
    ImageView botaoSetas;
    boolean BRLouUSD = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        inputBRL = findViewById(R.id.id_edit_text_1);
        inputUSD = findViewById(R.id.id_edit_text_2);
        resultado = findViewById(R.id.id_text_view_2);
        botaoSetas = findViewById(R.id.id_image_view_1);
        contextView = findViewById(R.id.id_layout);


        habilitarBotoes();
        botaoSetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float entrada;
                if(BRLouUSD){
                    if(inputBRL.getText().toString().equals("") || inputBRL.getText().toString() == null){
                        Toast.makeText(MainActivity.this, "Insira algum valor no Real", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    entrada = Float.parseFloat(inputBRL.getText().toString());
                    if(entrada == 0){
                        BRLouUSD = !BRLouUSD;
                        habilitarBotoes();
                        return;
                    }
                    Snackbar.make(contextView, "Aguarde um momento até sua requisição ser processada", Snackbar.LENGTH_LONG).show();
                    botaoSetas.setEnabled(false);
                    quantidade = entrada;
                    consultarAPI();
                }else{
                    if(inputUSD.getText().toString().equals("") || inputUSD.getText().toString() == null){
                        Toast.makeText(MainActivity.this, "Insira algum valor no Dolar", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    entrada = Float.parseFloat(inputUSD.getText().toString());
                    if(entrada == 0){
                        BRLouUSD = !BRLouUSD;
                        habilitarBotoes();
                        return;
                    }
                    Snackbar.make(contextView, "Aguarde um momento até sua requisição ser processada", Snackbar.LENGTH_LONG).show();
                    botaoSetas.setEnabled(false);
                    quantidade = entrada;
                    consultarAPI();
                }
                consultarAPI();
            }
        });


    }
    private void habilitarBotoes(){
        inputBRL.setEnabled(BRLouUSD);
        inputUSD.setEnabled(!BRLouUSD);
        if(BRLouUSD){
            valorInicial = "BRL";
            valorFinal = "USD";
        }else{
            valorInicial = "USD";
            valorFinal = "BRL";
        }
    }

    private void consultarAPI(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BufferedReader leitorDeBuffer = null;
                try {
                    URL url = new URL("https://api.apilayer.com/exchangerates_data/" + endpoint + "?access_key=" + access_key
                            + "&from=" + valorInicial + "&to=" + valorFinal + "&amount=" + quantidade);

                    // Inicializar conexão HTTP
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestProperty("apikey", access_key);
                    conexao.setRequestMethod("GET");

                    if (conexao.getResponseCode() == 200) {
                        // Ler resposta da API
                        InputStream responseBody = conexao.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key
                            if (key.equals("result")) { // Check if desired key
                                float value = Float.parseFloat(jsonReader.nextString());
                                retornoDaAPI = value;
                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }
                        }
                        jsonReader.close();
                        conexao.disconnect();
                        if(BRLouUSD){
                            resultado.setText("BRL: "+quantidade+"\nUSD: "+retornoDaAPI);
                            inputUSD.setText(String.format("%f",(retornoDaAPI)));
                        }else{
                            resultado.setText("BRL: "+(retornoDaAPI)+"\nUSD: "+quantidade);
                            inputBRL.setText(String.format("%f",(retornoDaAPI)));
                        }
                        botaoSetas.setEnabled(true);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}