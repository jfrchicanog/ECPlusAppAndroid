package es.uma.ecplusproject.ecplusandroidapp.modelo.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by francis on 18/3/16.
 */
public class Palabra implements Serializable {
    private String nombre;
    private Long id;
    private List<RecursoAV> recursos;
    private Map<Resolucion, String> hashes;
    private RecursoAV icono;
    private Boolean iconoReemplazable;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Palabra() {}
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

    public Map<Resolucion, String> getHashes() {
        if (hashes == null) {
            hashes = new HashMap<>();
        }
        return hashes;
    }


    public String getHash(Resolucion resolution) {
        return getHashes().get(resolution);
    }

    public void addRecurso(RecursoAV recurso) {
        getRecursos().add(recurso);
    }

    public RecursoAV getIcono() {
        return icono;
    }

    public void setIcono(RecursoAV icono) {
        this.icono = icono;
    }

    public Boolean getIconoReemplazable() {
        return iconoReemplazable;
    }

    public void setIconoReemplazable(Boolean iconoReemplazable) {
        this.iconoReemplazable = iconoReemplazable;
    }
}
