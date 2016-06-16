package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francis on 18/3/16.
 */
public class Palabra implements Serializable {
    private String nombre;
    private long id;
    private List<RecursoAV> recursos;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Palabra(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String texto) {
        this.nombre = texto;
    }

    public String toString() {
        return nombre;
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
