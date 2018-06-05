package es.uma.ecplusproject.ecplusandroidapp.modelo.dominio;

import java.io.Serializable;

/**
 * Created by francis on 5/6/18.
 */

public class Category implements Serializable {
    private Long id;
    private String nombre;

    public Category() {
    }

    public Category(Long id) {
        this.id=id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
