package es.uma.ecplusproject.ecplusandroidapp.restws;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import es.uma.ecplusproject.ecplusandroidapp.modelo.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Video;
import es.uma.ecplusproject.ecplusandroidapp.restws.webservice.SindromeClass;

/**
 * Created by francis on 24/5/16.
 */
public class DescargaListaSindromes extends AsyncTask<String, Sindrome, Video> {

    //public static final String HOST = "192.168.57.1:8080";
    public static final String HOST = "ecplusproject.uma.es";
    public static final String PROTOCOL = "https://";
    public static final String CONTEXT_PATH = "/academicPortal";
    public static final String REST_API_BASE = "/ecplus/api/v1";
    public static final String SYNDROMES_RESOURCE = "/sindromes";
    private ArrayAdapter<Sindrome> adaptador;

    public DescargaListaSindromes(ArrayAdapter<Sindrome> adao) {
        adaptador = adao;

    }


    @Override
    protected Video doInBackground(String... params) {

        String language = params[0];
        String resolution = null;

        if (params.length > 1) {
            resolution = params[1];
        }

        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE + SYNDROMES_RESOURCE + "/" + language;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        SindromeClass [] sindromes = restTemplate.getForObject(url, SindromeClass[].class);

        for (SindromeClass sindrome: sindromes) {
            Sindrome nuevoSindrome = new Sindrome(sindrome.getNombre());
            nuevoSindrome.setDescripcion(new String(sindrome.getContenido(), Charset.forName("UTF-8")));
            publishProgress(nuevoSindrome);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Sindrome... values) {
        adaptador.add(values[0]);
    }
}
