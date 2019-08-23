/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maruyama.components.mvc;

import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;
import maruyama.components.swing.ObjectTableModel;

/**
 *
 * @author gustavo
 */
public abstract class GenericCRUDController<T> {

    private static final String EDITANDO = "EDITANDO";
    private static final String INICIAL = "INICIAL";
    private static final String OBJETO_SELECIONADO = "OBJETO_SELECIONADO";

    private GenericCRUDModel<T> model;
    private GenericCRUDView view;
    private ObjectTableModel tableModel;
    private String estado;

    public GenericCRUDController(GenericCRUDModel model, GenericCRUDView view) {
        this.model = model;
        this.view = view;
        this.estado = GenericCRUDController.INICIAL;
        inicializarTabela();
        inicializarAcoes();
        aplicarEstado();
    }

    public GenericCRUDController(GenericCRUDModel model, GenericCRUDView view, String... campos) {
        this.model = model;
        this.view = view;
        this.estado = GenericCRUDController.INICIAL;
        inicializarTabela(campos);
    }

    private void inicializarTabela() {
        Field[] campos = model.getDataModelClass().getDeclaredFields();
        String[] camposString = new String[campos.length];
        for (int i = 0; i < campos.length; i++) {
            camposString[i] = campos[i].getName();
        }
        tableModel = new ObjectTableModel(model.getDataModelClass(), model.getLista(), camposString);
        view.getTabela().setModel(tableModel);
    }

    private void inicializarTabela(String... campos) {
        tableModel = new ObjectTableModel(model.getDataModelClass(), model.getLista(), campos);
        view.getTabela().setModel(tableModel);
    }

    private void inicializarAcoes() {
        view.getBotaoNovo().addActionListener((e) -> {
            model.novo();
            limparCampos();
            this.estado = GenericCRUDController.EDITANDO;
            aplicarEstado();
        });
        view.getBotaoSalvar().addActionListener((e) -> {
            T object = (T) model.getObject();
            dadosViewParaModel(object, view.getFormulario());
            model.salvarEmLista();
            model.salvarEmBaseDeDados(object);
            tableModel.fireTableDataChanged();
            this.estado = GenericCRUDController.OBJETO_SELECIONADO;
            aplicarEstado();
        });
        view.getBotaoEditar().addActionListener((e) -> {
            this.estado = GenericCRUDController.EDITANDO;
            aplicarEstado();
        });
        view.getBotaoExcluir().addActionListener((e) -> {
            limparCampos();
            T object = (T) model.getObject();
            model.removerEmBaseDeDados(object);
            model.remover();
            tableModel.fireTableDataChanged();
            this.estado = GenericCRUDController.INICIAL;
            aplicarEstado();
        });
        view.getBotaoCancelar().addActionListener((e) -> {
            T obj = (T) model.getObject();
            dadosModelParaView(obj, view.getFormulario());

            if (model.getLista().contains(obj)) {
                this.estado = GenericCRUDController.OBJETO_SELECIONADO;
            } else {
                this.estado = GenericCRUDController.INICIAL;
            }
            aplicarEstado();
        });
        view.getTabela().getSelectionModel().addListSelectionListener((e) -> {
            int index = view.getTabela().getSelectedRow();
            if (index < 0) {
                return;
            }
            T obj = (T) tableModel.getObject(index);
            model.setObject(obj);
            dadosModelParaView(obj, view.getFormulario());
            this.estado = GenericCRUDController.OBJETO_SELECIONADO;
            aplicarEstado();
        });
    }

    private void limparCampos() {
        limparCampos(view.getFormulario().getComponents());
    }

    private void limparCampos(Component[] componentes) {
        for (Component c : componentes) {
            if (c instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) c;
                tc.setText("");
            }
            if (c instanceof JComboBox) {
                JComboBox cb = (JComboBox) c;
                cb.setSelectedIndex(0);
            }
            if (c instanceof JToggleButton) {
                JToggleButton tb = (JToggleButton) c;
                tb.setSelected(false);
            }
            if (c instanceof JPanel) {
                JPanel jp = (JPanel) c;
                limparCampos(jp.getComponents());
            }
        }
    }

    private void aplicarEstado() {
        if (estado.equals(GenericCRUDController.INICIAL)) {
            habilitarCampos(false);
            view.getBotaoCancelar().setEnabled(false);
            view.getBotaoEditar().setEnabled(false);
            view.getBotaoExcluir().setEnabled(false);
            view.getBotaoSalvar().setEnabled(false);
            view.getBotaoNovo().setEnabled(true);
        }

        if (estado.equals(GenericCRUDController.EDITANDO)) {
            habilitarCampos(true);
            view.getBotaoCancelar().setEnabled(true);
            view.getBotaoSalvar().setEnabled(true);
            view.getBotaoEditar().setEnabled(false);
            view.getBotaoExcluir().setEnabled(false);
            view.getBotaoNovo().setEnabled(false);
        }

        if (estado.equals(GenericCRUDController.OBJETO_SELECIONADO)) {
            habilitarCampos(false);
            view.getBotaoEditar().setEnabled(true);
            view.getBotaoSalvar().setEnabled(false);
            view.getBotaoExcluir().setEnabled(true);
            view.getBotaoCancelar().setEnabled(false);
            view.getBotaoNovo().setEnabled(true);
        }
    }

    private void habilitarCampos(boolean b) {
        habilitarCampos(view.getFormulario().getComponents(), b);
    }

    private void habilitarCampos(Component[] componentes, boolean b) {
        for (Component c : componentes) {
            c.setEnabled(b);
            if (c instanceof JPanel) {
                habilitarCampos(((JPanel) c).getComponents(), b);
            }
        }
    }

    public abstract void dadosViewParaModel(T objeto, JPanel formulario);

    public abstract void dadosModelParaView(T objeto, JPanel formulario);

}
