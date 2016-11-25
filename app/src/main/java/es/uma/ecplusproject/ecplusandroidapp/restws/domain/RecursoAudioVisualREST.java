package es.uma.ecplusproject.ecplusandroidapp.restws.domain;

/**
 * Created by francis on 25/11/16.
 */

public class RecursoAudioVisualREST {
    private Long id;
    private String type;
    private String hash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
