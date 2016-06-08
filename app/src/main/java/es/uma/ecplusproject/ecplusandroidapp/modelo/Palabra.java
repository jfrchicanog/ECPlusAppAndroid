package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francis on 18/3/16.
 */
public class Palabra implements Serializable {
    private String texto;
    private long id;
    private List<RecursoAV> recursos;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Palabra(String texto) {
        this.texto = texto;
    }

    public String toString() {
        return texto;
    }
    public List<RecursoAV> getRecursos() {
        if (recursos==null) {
            recursos = new ArrayList<RecursoAV>();
        }
        return recursos;
    }

    public void addRecurso(RecursoAV recurso) {
        getRecursos().add(recurso);
    }

}
