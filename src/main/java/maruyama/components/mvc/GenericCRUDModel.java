/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maruyama.components.mvc;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gustavo
 */
public abstract class GenericCRUDModel<T> {

    private T object;
    private List<T> lista;
    private Class<T> clazz;

    public GenericCRUDModel() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.lista = new ArrayList<T>();
        preencherLista(carregarLista());
    }   
    
    protected void preencherLista(List<T> objs){
        if(objs == null){
            return;
        }
        if(objs.size() < 1){
            return;
        }
        this.lista.clear();
        this.lista.addAll(objs);
    }

    public void novo() {
        try {
            object = clazz.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(GenericCRUDModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GenericCRUDModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salvarEmLista() {
        if (object == null) {
            return;
        }

        if (lista.contains(object)) {
            return;
        }

        lista.add(object);
    }

    public abstract void salvarEmBaseDeDados(T object);

    public abstract void removerEmBaseDeDados(T object);

    public abstract List<T> carregarLista();
    
    public abstract List<T> buscar(String campo, String param);

    public void remover() {
        if (object == null) {
            return;
        }
        lista.remove(object);
        object = null;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T t) {
        this.object = t;
    }

    public List<T> getLista() {
        return lista;
    }

    public void setLista(List<T> lista) {
        this.lista = lista;
    }

    public Class<T> getDataModelClass() {
        return clazz;
    }
}
