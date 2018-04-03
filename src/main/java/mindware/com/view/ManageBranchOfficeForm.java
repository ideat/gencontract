package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.BranchOffice;
import mindware.com.model.Signatories;
import mindware.com.netbank.model.BranchOfficeNetbank;
import mindware.com.netbank.service.BranchOfficeNetbankService;
import mindware.com.service.BranchOfficeService;

import java.util.ArrayList;
import java.util.List;

public class ManageBranchOfficeForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Grid<Signatories> gridSignatories;
    private Grid<BranchOffice> gridBranchOffice;
    private Button btnImport;
    private Button btnAddSignatories;
    private Panel panelBranchOffice;
    private Panel panelSignatories;



    public ManageBranchOfficeForm(){

        setCompositionRoot(buildMainLayout());
        postBuild();
    }

    private void postBuild(){
        btnImport.addClickListener(clickEvent -> {
            BranchOfficeNetbankService branchOfficeNetbankService = new BranchOfficeNetbankService();
            List<BranchOfficeNetbank> branchOfficeNetbanksList = branchOfficeNetbankService.findAllBranchOfficeNetbank();
            List<BranchOffice> branchOfficeList = new ArrayList<>();
            for(BranchOfficeNetbank branchOfficeNetbank:branchOfficeNetbanksList){
                BranchOffice branchOffice = new BranchOffice();
                branchOffice.setBranchOfficeId(branchOfficeNetbank.getGbofinofi());
                branchOffice.setBranchName(branchOfficeNetbank.getGbofidesc());
                branchOffice.setCityName(branchOfficeNetbank.getGbdptdesc());
                branchOffice.setProvinceName(branchOfficeNetbank.getGbprvdesc());
                branchOfficeList.add(branchOffice);
            }
            BranchOfficeService branchOfficeService = new BranchOfficeService();
            List<BranchOffice> branchOfficeListLocal = new ArrayList<>();
            branchOfficeListLocal.addAll(branchOfficeService.findAllBranchOffice());
            if (branchOfficeListLocal.isEmpty())
                branchOfficeService.insertListBranchOffice(branchOfficeList);
//TODO: Insert new branch_office when branchOfficeListLocal is not empty
            loadBranchOffice(branchOfficeService.findAllBranchOffice());



        });

        btnAddSignatories.addClickListener(clickEvent -> {
           SignatorieWindowForm signatorieWindowForm = new SignatorieWindowForm();
           signatorieWindowForm.setModal(true);
           signatorieWindowForm.setWidth("350px");
           signatorieWindowForm.setHeight("300px");
           signatorieWindowForm.center();
           UI.getCurrent().addWindow(signatorieWindowForm);
           signatorieWindowForm.addCloseListener(closeEvent -> {
                if (signatorieWindowForm.signatorie!=null){

                }
           });

        });
    }

    private void loadSignatorie(Signatories signatories){

    }


    private void loadBranchOffice(List<BranchOffice> branchOfficeList){
        gridBranchOffice.setItems(branchOfficeList);
        gridBranchOffice.addColumn(BranchOffice::getBranchOfficeId).setCaption("Cod. Agencia");
        gridBranchOffice.addColumn(BranchOffice::getBranchName).setCaption("Nombre");
        gridBranchOffice.addColumn(BranchOffice::getCityName).setCaption("Departamento");
        gridBranchOffice.addColumn(BranchOffice::getProvinceName).setCaption("Provincia");


    }



    private GridLayout buildMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(6);
        gridMainLayout.setRows(4);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        gridMainLayout.addComponent(buildPanelBranchOffice(),0,0,4,1);
        btnImport = new Button("Importar");
        btnImport.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnImport.setIcon(VaadinIcons.AUTOMATION);
        gridMainLayout.addComponent(btnImport,5,0);

        gridMainLayout.addComponent(buildPanelSiganotories(),0,2,4,3);
        btnAddSignatories = new Button("Guadar");
        btnAddSignatories.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnAddSignatories.setIcon(VaadinIcons.STORAGE);
        gridMainLayout.addComponent(btnAddSignatories,5,2);

        return gridMainLayout;
    }



    private Panel buildPanelBranchOffice(){
        panelBranchOffice = new Panel("<font size=3 color=#163759> Datos Agencias <font>");
        panelBranchOffice.setCaptionAsHtml(true);
        panelBranchOffice.setStyleName(ValoTheme.PANEL_WELL);
        panelBranchOffice.setHeight("215px");

        gridBranchOffice = new Grid();
        gridBranchOffice.setStyleName(ValoTheme.TABLE_SMALL);
        gridBranchOffice.setSizeFull();

        panelBranchOffice.setContent(gridBranchOffice);


        return panelBranchOffice;
    }

    private Panel buildPanelSiganotories(){
        panelSignatories = new Panel("<font size=3 color=#163759> Firmantes por la Agencia <font>");
        panelSignatories.setCaptionAsHtml(true);
        panelSignatories.setStyleName(ValoTheme.PANEL_WELL);
        panelSignatories.setHeight("155px");

        gridSignatories = new Grid();
        gridSignatories.setStyleName(ValoTheme.TABLE_SMALL);
        gridSignatories.setSizeFull();
        gridBranchOffice.getEditor().setEnabled(true);

        panelSignatories.setContent(gridSignatories);

        return panelSignatories;
    }

}
