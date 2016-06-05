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
public class DescargaListaPalabras extends AsyncTask<Void, Palabra, Video> {

    private ArrayAdapter<Palabra> adaptador;

    public DescargaListaPalabras(ArrayAdapter<Palabra> adao) {
        adaptador = adao;

    }


    @Override
    protected Video doInBackground(Void... params) {

        final String url = "http://ecplusproject.uma.es/ECplusRS/ecplus/api/v1/words/cat";
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
