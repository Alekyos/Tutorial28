package app.alekyos.app.tutorial28;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button btnconsultar, btnGuardar;
    EditText etId, etNombres, etTelefono;

    //INICIO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnconsultar = (Button)findViewById(R.id.btnConsultar);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        etId = (EditText)findViewById(R.id.etId);
        etNombres = (EditText)findViewById(R.id.etNombres);
        etTelefono = (EditText)findViewById(R.id.etTelefono);

        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConsultarDatos().execute("http://10.0.3.2/CursoAndroid/consulta.php?id=" + etId.getText().toString());
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CargarDatos().execute("http://10.0.3.2/CursoAndroid/registro.php?nombres="+etNombres.getText().toString() + "&tel=" + etTelefono.getText().toString()); //Local host de los emuladores de genymotion. El emulador de android studio es: http://10.0.3.2/
                                                                        //CursoAndroid es el nombre de la carpeta donde estan los function, en xamp estan dentro de xampp/htdocs/CursoAndroid
                                                                        //registro es la function que conecta con mysql
                                                                        //No se pone ID ya que este era autoincrementado
            }
        });

    }


    /**Metodo que guarda informacion dentro de la base de datos
     *
     */
    private class CargarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();

        }
    }

    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                //Llama al proceso con una url en un arreglo
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                //Lo intenta pero falla, entra aqui
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Aqui se pondran todos los elementos a mostrar

            JSONArray ja = null;
            try {
                ja = new JSONArray(result);
                etNombres.setText(ja.getString(1));
                etNombres.setText(ja.getString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        //Esto de aqui remplazara los espacios por %20. Si no se hace esto solo ejectura la primera parte
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        //Longitud maxima
        int len = 500;

        try {
            //Se convierte myurl(string) por un objeto tipo URL
            URL url = new URL(myurl);
            //Se crea la coneccion tipo http
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //.open significa que la abre
            //PARAMETROS
            //Tiempo de lectura
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            //Metodo con el que se envian los datos
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //Se realiza la connecion
            conn.connect();
            //Entero de respuesta que devuelve un codigo de respuesta. Sera 200 si se efectuo la coneccion
            int response = conn.getResponseCode();
            //respuesta es DEBUGTAG, es la etiqueta. Se puede poner cualquier cosa
            //Esto es un mensaje, como un sout
            Log.d("respuesta", "The response is: " + response);
            //Lo que responda la URL me lo guardara en is
            is = conn.getInputStream();

            // Se convierte el is en un string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
