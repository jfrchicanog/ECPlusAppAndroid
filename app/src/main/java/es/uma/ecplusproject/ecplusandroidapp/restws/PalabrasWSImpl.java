package es.uma.ecplusproject.ecplusandroidapp.restws;

import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Category;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.Hash;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.PalabraRes;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.RecursoAudioVisualREST;

/**
 * Created by francis on 25/11/16.
 */

public class PalabrasWSImpl implements PalabrasWS {

    //private static final String HOST = "192.168.57.1:8080";
    private static final String HOST = "ecplusproject.uma.es";
    private static final String PROTOCOL = "https://";
    private static final String CONTEXT_PATH = "/academicPortal";
    private static final String REST_API_BASE = "/ecplus/api/v1";
    private static final String WORDS_RESOURCE = "/words";
    private static final String CATEGORIES_RESOURCE = "/categories";
    private static final String HASH = "/hash";
    private static final String RESOURCE = "/resource";
    private static final String RESOLUTION = "?resolution=";

    private RestTemplate restTemplate;

    public PalabrasWSImpl() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public String getHashForListOfWords(String language, Resolucion resolucion) {

        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE
                + WORDS_RESOURCE + "/" + language + HASH
                + RESOLUTION + resolucion.toString();

        Hash hash = restTemplate.getForObject(url, Hash.class);
        if (hash != null) {
            return hash.getHash();
        } else {
            return null;
        }
    }

    @Override
    public List<Palabra> getWords(String language, Resolucion resolution) {
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE
                + WORDS_RESOURCE + "/" + language
                + RESOLUTION + resolution.toString();

        PalabraRes[] palabras = restTemplate.getForObject(url, PalabraRes[].class);

        List<Palabra> resultado = new ArrayList<>();

        for (PalabraRes palabra : palabras) {
            Palabra nuevaPalabra = new Palabra();
            nuevaPalabra.setNombre(palabra.getNombre());
            nuevaPalabra.setId(palabra.getId());
            nuevaPalabra.getHashes().put(resolution, palabra.getHash());
            nuevaPalabra.setIconoReemplazable(palabra.getIconoReemplazable());
            nuevaPalabra.setAvanzada(palabra.getAvanzada());
            nuevaPalabra.setCategoria(new Category(palabra.getCategoria()));

            for (RecursoAudioVisualREST ravREST : palabra.getAudiovisuales()) {
                //Log.d(getClass().getSimpleName(), ravREST.getType());
                RecursoAV rav = RecursoAV.createRecursoAV(ravREST.getType());
                rav.setId(ravREST.getId());
                rav.getFicheros().put(resolution, ravREST.getHash());
                if (rav.getId() == palabra.getIcono()) {
                    nuevaPalabra.setIcono(rav);
                }
                nuevaPalabra.addRecurso(rav);
            }
            resultado.add(nuevaPalabra);
        }

        return resultado;
    }

    @Override
    public InputStream getResource(String hash) throws IOException {
        URL url = new URL(PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE
                + RESOURCE + "/" + hash);
        return url.openStream();
    }

    @Override
    public List<Category> getCategories(String language) {
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE
                + CATEGORIES_RESOURCE + "/" + language;

        Category[] categorias = restTemplate.getForObject(url, Category[].class);
        return Arrays.asList(categorias);
    }
}
