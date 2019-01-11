package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Warranty;
import mindware.com.netbank.model.WarrantyNetbank;

import java.util.ArrayList;
import java.util.List;

public class WarrantyWindowForm extends Window {
    private TextField txtNumeroPizarra;
    private TextField txtNumeroCui;
    private TextField txtEntidadEmisora;
    private TextField txtTitular;
    private TextField txtNumeroDpf;

    private Button btnSave;
    private Button btnExit;

    private GridLayout mainGridLayout;

    public WarrantyWindowForm(Object warrantySelected, List<?> warrantyList, String origen){
        setContent(buildGridMainLayout());

        if (origen.equals("netbank")){
            fillDataNetBank((WarrantyNetbank) warrantySelected);
        }else{
            fillData((Warranty) warrantySelected);
        }

        postBuild(warrantySelected,warrantyList,origen);
    }

    private void postBuild(Object warrantySelected, List<?> warrantyList, String origen){
        btnSave.addClickListener(clickEvent ->{
            String result = validateData();
            if(result.equals("OK")){
                if (origen.equals("netbank")){
                    saveDataNetBank((WarrantyNetbank) warrantySelected, (List<WarrantyNetbank>) warrantyList);
                }else{
                    saveData((Warranty) warrantySelected, (List<Warranty>) warrantyList);
                }
                close();
            }else{
                new Notification("Datas incompletos",
                        result,
                        Notification.Type.ERROR_MESSAGE,true)
                        .show(Page.getCurrent());
            }
        });

        btnExit.addClickListener(clickEvent ->{
            close();
        });
    }

    private void saveDataNetBank(WarrantyNetbank warrantyNetbank, List<WarrantyNetbank> warrantyNetbankList){
        warrantyNetbank.setPrgarnpar(txtNumeroDpf.getValue());
        warrantyNetbank.setTitular(txtTitular.getValue());
        warrantyNetbank.setEntidadEmisora(txtEntidadEmisora.getValue());
        warrantyNetbank.setNumeroCUI(txtNumeroCui.getValue());
        warrantyNetbank.setNumeroPizarra(txtNumeroPizarra.getValue());

        List<WarrantyNetbank> aux = new ArrayList<>();
        for(WarrantyNetbank wn : warrantyNetbankList){
            if(wn.getPrgarcorr().equals(warrantyNetbank.getPrgarcorr()))
                aux.add(wn);
        }

        warrantyNetbankList = aux;
        warrantyNetbankList.add(warrantyNetbank);
    }

    private void saveData(Warranty warranty, List<Warranty> warrantyList){
        warranty.setMortageNumber(txtNumeroDpf.getValue());
        warranty.setTitular(txtTitular.getValue());
        warranty.setEntidadEmisora(txtEntidadEmisora.getValue());
        warranty.setNumeroCUI(txtNumeroCui.getValue());
        warranty.setNumeroPizarra(txtNumeroPizarra.getValue());

        List<Warranty> aux = new ArrayList<>();
        for (Warranty w : warrantyList){
            if (!w.getId().equals(warranty.getId()))
                aux.add(w);
        }

        warrantyList = aux;
        warrantyList.add(warranty);
    }

    private void fillDataNetBank(WarrantyNetbank warrantyNetbank){
        txtNumeroDpf.setValue(warrantyNetbank.getPrgarnpar() == null ? "" : warrantyNetbank.getPrgarnpar());
        txtTitular.setValue(warrantyNetbank.getTitular()==null?"" : warrantyNetbank.getTitular());
        txtEntidadEmisora.setValue(warrantyNetbank.getEntidadEmisora()==null ? "" : warrantyNetbank.getEntidadEmisora());
        txtNumeroCui.setValue(warrantyNetbank.getNumeroCUI() == null ? "" : warrantyNetbank.getNumeroCUI());
        txtNumeroPizarra.setValue(warrantyNetbank.getNumeroPizarra()==null ? "" : warrantyNetbank.getNumeroPizarra());
    }

    private void fillData(Warranty warranty){
        txtNumeroDpf.setValue(warranty.getMortageNumber() == null ? "" : warranty.getMortageNumber());
        txtTitular.setValue(warranty.getTitular()==null?"" : warranty.getTitular());
        txtEntidadEmisora.setValue(warranty.getEntidadEmisora()==null ? "" : warranty.getEntidadEmisora());
        txtNumeroCui.setValue(warranty.getNumeroCUI() == null ? "" : warranty.getNumeroCUI());
        txtNumeroPizarra.setValue(warranty.getNumeroPizarra()==null ? "" : warranty.getNumeroPizarra());

    }

    private String validateData(){
        if (txtNumeroDpf.isEmpty()) return "Numero de DPF no puede ser omitido";
        if(txtTitular.isEmpty()) return "Titular no puede ser omitido";
        if (txtEntidadEmisora.isEmpty()) return "Entidad Emisora no puede ser omitida";
        if (txtNumeroCui.isEmpty()) return "Numero CUI no puede ser omitido";
        if (txtNumeroPizarra.isEmpty()) return "Numero de Pizarra no puede ser omitido";
        return "OK";
    }

    private GridLayout buildGridMainLayout(){
        mainGridLayout = new GridLayout();
        mainGridLayout.setColumns(3);
        mainGridLayout.setRows(3);
        mainGridLayout.setWidth("100%");
        mainGridLayout.setSpacing(true);
        mainGridLayout.setMargin(true);

        txtNumeroDpf = new TextField("Numero DPF:");
        txtNumeroDpf.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtNumeroDpf,0,0);

        txtNumeroPizarra = new TextField("Numero de Pizarra:");
        txtNumeroPizarra.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtNumeroPizarra,1,0);

        txtNumeroCui = new TextField("Numero CUI:");
        txtNumeroCui.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtNumeroCui,2,0);

        txtEntidadEmisora = new TextField("Entidad emisora:");
        txtEntidadEmisora.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtEntidadEmisora,0,1);

        txtTitular = new TextField("Titular:");
        txtTitular.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtTitular,1,1);

        btnSave = new Button("Guardar");
        btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSave.setIcon(VaadinIcons.STORAGE);
        mainGridLayout.addComponent(btnSave,0,2);

        btnExit = new Button("Salir");
        btnExit.setStyleName(ValoTheme.BUTTON_DANGER);
        btnExit.setIcon(VaadinIcons.EXIT);
        mainGridLayout.addComponent(btnExit,1,2);

        return mainGridLayout;
    }
}
