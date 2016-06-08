package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francis on 18/3/16.
 */
public class Palabra {
    private String texto;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private List<RecursoAV> recursos;

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
