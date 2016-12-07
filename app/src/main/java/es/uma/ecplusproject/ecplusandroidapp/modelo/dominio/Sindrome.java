package es.uma.ecplusproject.ecplusandroidapp.modelo.dominio;

import java.io.Serializable;

/**
 * Created by francis on 18/3/16.
 */
public class Sindrome implements Serializable {
    private String texto;
    private String descripcion;
    private long id;
    private String hash;
    private TipoDocumento tipo;

    public TipoDocumento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }

    public Sindrome(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }

    public String toString() {
        return texto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
