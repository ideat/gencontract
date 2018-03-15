package mindware.com.view;

import mindware.com.netbank.model.Client;
import mindware.com.netbank.service.ClientService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ImportDataForm extends CustomComponent implements View {
    private Button btnImport;
    private TextField txtLoanNumber;
    private GridLayout gridLayout;
    public ImportDataForm(){
        setCompositionRoot(buildGridLayout());
        postBuild();
    }

    private void postBuild(){
        btnImport.addClickListener(clickEvent -> {
            ClientService clientService = new ClientService();
            Client client = clientService.findClientNetbankById(Integer.parseInt(txtLoanNumber.getValue().toString()));
            client.getAddress();
        });
    }

    private GridLayout buildGridLayout(){
        gridLayout = new GridLayout();
        gridLayout.setColumns(5);
        gridLayout.setRows(4);
        gridLayout.setSpacing(true);

        txtLoanNumber=new TextField("Numero credito:");
        txtLoanNumber.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        txtLoanNumber.setPlaceholder("Ingrese credito");
        gridLayout.addComponent(txtLoanNumber,0,0);

        btnImport=new Button("Buscar");
        btnImport.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnImport.setIcon(VaadinIcons.SEARCH);
        gridLayout.addComponent(btnImport,1,0);
        gridLayout.setComponentAlignment(btnImport,Alignment.BOTTOM_LEFT);
        return gridLayout;
    }
}
