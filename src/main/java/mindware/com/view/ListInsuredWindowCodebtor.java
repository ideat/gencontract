package mindware.com.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.CoDebtorGuarantor;
import mindware.com.model.LoanData;
import mindware.com.service.LoanDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListInsuredWindowCodebtor extends Window {
    private GridLayout gridMainLayout;
    private Grid<CoDebtorGuarantor> gridCodebtor;
    private Panel panelGridCodebtor;
    private Button btnUpdate;


    public ListInsuredWindowCodebtor(LoanData loanData){
        setContent(buildMainGridLayout());
        ObjectMapper mapper = new ObjectMapper();
        List<CoDebtorGuarantor> coDebtorLocalList = new ArrayList<>();

        try {
            coDebtorLocalList = Arrays.asList(mapper.readValue(loanData.getCoDebtors(),CoDebtorGuarantor[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        postBuild(coDebtorLocalList, loanData);
    }

    private void postBuild(List<CoDebtorGuarantor> coDebtorGuarantorList, LoanData loanData){
        fillGrid(coDebtorGuarantorList);

        btnUpdate.addClickListener(clickEvent -> {
            ListDataProvider<CoDebtorGuarantor> coDebtorListDataProvider = (ListDataProvider<CoDebtorGuarantor>) gridCodebtor.getDataProvider();
            ObjectMapper mapper = new ObjectMapper();
            Collection<CoDebtorGuarantor> coDebtorListDataProvider2= coDebtorListDataProvider.getItems();
            String jsonCodebtor = null;
            try {
                jsonCodebtor = mapper.writeValueAsString(coDebtorListDataProvider2);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            LoanDataService loanDataService = new LoanDataService();
            loanData.setCoDebtors(jsonCodebtor);
            loanDataService.updateCodebtor(loanData);
            Notification.show("Codeudores",
                    "Datos actualizados!",
                    Notification.Type.HUMANIZED_MESSAGE);

        });
    }

    private void fillGrid(List<CoDebtorGuarantor> coDebtorGuarantorList){

        gridCodebtor.setItems(coDebtorGuarantorList);
        gridCodebtor.removeAllColumns();
        gridCodebtor.addColumn(CoDebtorGuarantor::getId).setCaption("ID");
        gridCodebtor.addColumn(CoDebtorGuarantor::getName).setCaption("Nombre completo");
        Binder<CoDebtorGuarantor> binder = gridCodebtor.getEditor().getBinder();
        ComboBox<String> cmbInsured = new ComboBox<>();
        cmbInsured.setEmptySelectionAllowed(false);
        cmbInsured.setItems("asegurado","noAsegurado");
        gridCodebtor.addColumn(CoDebtorGuarantor::getInsured)
                .setEditorBinding(binder
                .forField(cmbInsured).bind(CoDebtorGuarantor::getInsured,CoDebtorGuarantor::setInsured)
                ).setCaption("Asegurado");

    }

    private GridLayout buildMainGridLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setRows(5);
        gridMainLayout.setColumns(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        btnUpdate = new Button("Actualizar");
        btnUpdate.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnUpdate.setIcon(VaadinIcons.UPLOAD);
        gridMainLayout.addComponent(btnUpdate,0,0);
        gridMainLayout.setComponentAlignment(btnUpdate,Alignment.BOTTOM_LEFT);

        gridMainLayout.addComponent(buildPanelGrid(),0,1,4,4);

        return gridMainLayout;
    }

    private Panel buildPanelGrid(){
        panelGridCodebtor = new Panel();
        panelGridCodebtor.setStyleName(ValoTheme.PANEL_WELL);
        panelGridCodebtor.setSizeFull();

        gridCodebtor = new Grid<CoDebtorGuarantor>();
        gridCodebtor.setWidth("100%");
        gridCodebtor.getEditor().setEnabled(true);
        gridCodebtor.getEditor().setSaveCaption("Guardar");
        gridCodebtor.getEditor().setCancelCaption("Cancelar");

        panelGridCodebtor.setContent(gridCodebtor);

        return panelGridCodebtor;

    }


}
