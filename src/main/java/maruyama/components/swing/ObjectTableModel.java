/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maruyama.components.swing;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import maruyama.components.annotations.Tabela;
import maruyama.components.utils.ReflectionsUtils;

/**
 * Essa é uma implementação de um {@code TableModel} que manipula objetos.
 * @author Gustavo Y. M. {gustavo.maruyama@ifms.edu.br}
 */
public class ObjectTableModel extends AbstractTableModel {

    private Class clazz;
    private List lista;
    private String[] atributos;

    /**
     * 
     * @param clazz A classe que representa os objetos que serão manipulados pelo {@code  TableModel}
     * @param lista Um {@link List} contendo os objetos que serão manipulados pelo {@code TableModel}
     * @param atributos Atributos que serão mostrados na tabela
     */
    public ObjectTableModel(Class clazz, List lista, String... atributos) {
        this.clazz = clazz;
        this.lista = lista;
        this.atributos = atributos;
    }
    
     public ObjectTableModel(Class clazz, List lista) {
        this.clazz = clazz;
        this.lista = lista;
        this.atributos = ReflectionsUtils.getAtributesFromFields(clazz.getDeclaredFields());
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return atributos.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object obj = lista.get(rowIndex);
        String col = atributos[columnIndex];

        try {
            Field f = clazz.getDeclaredField(col);
            f.setAccessible(true);
            return f.get(obj);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        try {
            Field field = clazz.getDeclaredField(atributos[column]);
            Tabela annotation = field.getAnnotation(Tabela.class);
            if(annotation == null){
                return atributos[column]; 
            }
            return annotation.descricaoColuna();
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ObjectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return atributos[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    public Object getObject(int index){
        return lista.get(index);
    }
}
