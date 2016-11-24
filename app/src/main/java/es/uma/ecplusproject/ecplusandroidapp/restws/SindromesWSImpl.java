package es.uma.ecplusproject.ecplusandroidapp.restws;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.Hash;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.SindromeClass;

/**
 * Created by francis on 24/11/16.
 */

public class SindromesWSImpl implements SindromesWS {

    private static final String HOST = "ecplusproject.uma.es";
    private static final String PROTOCOL = "https://";
    private static final String CONTEXT_PATH = "/academicPortal";
    private static final String REST_API_BASE = "/ecplus/api/v1";
    private static final String SYNDROMES_RESOURCE = "/sindromes";
    private static final String HASH = "/hash";

    private RestTemplate restTemplate;

    public SindromesWSImpl() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public String getHashForListOfSindromes(String language) {
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE
                + SYNDROMES_RESOURCE + "/" + language + HASH;

        Hash hash = restTemplate.getForObject(url, Hash.class);
        if (hash != null) {
            return hash.getHash();
        } else {
            return null;
        }
    }

    @Override
    public List<Sindrome> getSindromes(String language) {
        List<Sindrome> resultado = new ArrayList<>();
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE + SYNDROMES_RESOURCE + "/" + language;

        SindromeClass[] sindromes = restTemplate.getForObject(url, SindromeClass[].class);

        for (SindromeClass sindrome: sindromes) {
            Sindrome nuevoSindrome = new Sindrome(sindrome.getNombre());
            nuevoSindrome.setDescripcion(new String(sindrome.getContenido(), Charset.forName("UTF-8")));
            nuevoSindrome.setHash(sindrome.getHash());
            nuevoSindrome.setId(sindrome.getId());
            resultado.add(nuevoSindrome);
        }

        return resultado;
    }
}
