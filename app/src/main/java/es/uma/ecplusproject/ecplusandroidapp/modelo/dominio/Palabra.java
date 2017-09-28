package es.uma.ecplusproject.ecplusandroidapp.modelo.dominio;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    private String iconoPersonalizado;
    private Boolean iconoReemplazable;
    private Boolean avanzada;
    private Long listaPalabrasId;

    private Long accesos;
    private Date ultimoUso;

    public Boolean getAvanzada() {
        return avanzada;
    }
    public void setAvanzada(Boolean avanzada) {
        this.avanzada = avanzada;
    }

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

    public String getIconoPersonalizado() {
        return iconoPersonalizado;
    }

    public void setIconoPersonalizado(String iconoPersonalizado) {
        this.iconoPersonalizado = iconoPersonalizado;
    }

    public Long getListaPalabrasId() {
        return listaPalabrasId;
    }

    public void setListaPalabrasId(Long listaPalabrasId) {
        this.listaPalabrasId = listaPalabrasId;
    }

    public Long getAccesos() {
        return accesos;
    }

    public void incrementAccesos() {
        if (accesos==null) {
            accesos = 0L;
        }
        accesos++;
    }

    public void setAccesos(Long accesos) {
        this.accesos = accesos;
    }

    public Date getUltimoUso() {
        return ultimoUso;
    }

    public void setUltimoUso(Date ultimoUso) {
        this.ultimoUso = ultimoUso;
    }
}
