package es.uma.ecplusproject.ecplusandroidapp.restws;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Video;
import es.uma.ecplusproject.ecplusandroidapp.modelo.webservice.PalabraRes;

/**
 * Created by francis on 24/5/16.
 */
public class DescargaListaPalabras extends AsyncTask<String, Palabra, Video> {

    public static final String HOST = "192.168.57.1:8080";
    //public static final String HOST = "ecplusproject.uma.es";
    public static final String PROTOCOL = "http://";
    public static final String CONTEXT_PATH = "/academicPortal";
    public static final String REST_API_BASE = "/ecplus/api/v1";
    public static final String WORDS_RESOURCE = "/words";
    private ArrayAdapter<Palabra> adaptador;

    public DescargaListaPalabras(ArrayAdapter<Palabra> adao) {
        adaptador = adao;

    }


    @Override
    protected Video doInBackground(String... params) {

        String language = params[0];
        String resolution = null;

        if (params.length > 1) {
            resolution = params[1];
        }

        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE + WORDS_RESOURCE + "/" + language;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        PalabraRes [] palabras = restTemplate.getForObject(url, PalabraRes [].class);

        for (PalabraRes palabra: palabras) {
            publishProgress(new Palabra(palabra.getNombre()));
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Palabra... values) {
        adaptador.add(values[0]);
    }
}
