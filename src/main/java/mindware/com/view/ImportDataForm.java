package mindware.com.view;

import mindware.com.netbank.model.ClientLoanNetbank;
import mindware.com.netbank.service.ClientLoanNetBankService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ImportDataForm extends CustomComponent implements View {
    private Button btnImport;
    private TextField txtLoanNumberSearch;
    private TextField txtLoanNumber;
    private DateField dateLoanDate; //*
    private TextField txtCurrency;
    private TextField txtLoanMount;
    private TextField txtLoanTerm;
    private TextField txtInterestRate;
    private TextField txtTreRate; //*
    private TextField txtTeacRate; //*
    private TextField txtFeePayment; //*
    private TextField txtPaymentFrecuency;
    private TextField txtCreditLifeInsurance;
    private TextField txtTotalPayment; //*
    private TextField txtLoanDestination; //*
    private TextField txtAgency;
    private TextField txtFixedPaymentDay;
    private TextField txtOfficial;
    private TextField txtDebtorName;
    private TextField txtIdentifyCardDebtor;
    private TextField txtAddressDebtor;
    private TextField txtCivilStatusDebtor;
    private TextField txtGenderDebtor;
    private TextField txtClientLoanId;

    private GridLayout gridLayout;
    private Panel panelLoan;
    private Panel panelClient;
    private TabSheet tabSupplementaryData;
    private GridLayout gridLayoutSupplementaryData;
    private Button btnSaveSupplementaryData;
    private VerticalLayout verticalLayoutCoDebtor;
    private VerticalLayout verticalLayoutWarranty;
    private VerticalLayout verticalLayoutGuarantors;


    public ImportDataForm(){
        setCompositionRoot(buildGridLayout());
        postBuild();
    }

    private void postBuild(){
        btnImport.addClickListener(clickEvent -> {
            ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
            ClientLoanNetbank clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
            loadNetBankData(clientLoanNetbank);
        });
    }

    private void loadNetBankData(ClientLoanNetbank clientLoanNetbank){
        fieldClientStatus(false);

        txtClientLoanId.setValue(clientLoanNetbank.getPrmprcage().toString());
        txtIdentifyCardDebtor.setValue(clientLoanNetbank.getGbagendid());
        txtDebtorName.setValue(clientLoanNetbank.getGbagenomb());
        String civilStatus = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbageeciv().toString())) {
            case 1:
                civilStatus = "SOLTERO";
                break;
            case 2:
                civilStatus = "CASADO";
                break;
            case 3:
                civilStatus = "DIVORCIADO";
                break;
            case 4:
                civilStatus = "VIUDO";
                break;
        }

        txtCivilStatusDebtor.setValue(civilStatus);
        String gender = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbagesexo().toString())){
            case 1:
                gender = "MASCULINO";
                break;
            case 2:
                gender = "FEMENINO";
                break;
        }
        txtGenderDebtor.setValue(gender);
        txtAddressDebtor.setValue(clientLoanNetbank.getGbagedir());

        //Load Loan Data
        txtLoanNumber.setValue(clientLoanNetbank.getPrmprnpre().toString());
        String currency=null;
        switch (Integer.parseInt(clientLoanNetbank.getPrmprcmon().toString())){
            case 1:
                currency = "BS";
                break;
            case 2:
                currency ="$us";
                break;
        }

        txtCurrency.setValue(currency);
        txtLoanMount.setValue(clientLoanNetbank.getPrmprmdes().toString());
        txtLoanTerm.setValue(clientLoanNetbank.getPrmprplzo().toString());
        txtInterestRate.setValue(clientLoanNetbank.getPrtsatbas().toString());
        txtFixedPaymentDay.setValue(clientLoanNetbank.getPrmprdiap().toString());
        txtPaymentFrecuency.setValue(clientLoanNetbank.getPrmprppgk().toString());
        fieldClientStatus(true);

    }

    private void fieldClientStatus(boolean read) {
        txtClientLoanId.setReadOnly(read);
        txtIdentifyCardDebtor.setReadOnly(read);
        txtDebtorName.setReadOnly(read);
        txtCivilStatusDebtor.setReadOnly(read);
        txtGenderDebtor.setReadOnly(read);
        txtAddressDebtor.setReadOnly(read);

        txtLoanNumber.setReadOnly(read);
        txtCurrency.setReadOnly(read);
        txtLoanMount.setReadOnly(read);
        txtLoanTerm.setReadOnly(read);
        txtInterestRate.setReadOnly(read);
        txtFixedPaymentDay.setReadOnly(read);
        txtPaymentFrecuency.setReadOnly(read);

    }

    private GridLayout buildGridLayout(){
        gridLayout = new GridLayout();
        gridLayout.setColumns(5);
        gridLayout.setRows(9);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();

        txtLoanNumberSearch =new TextField("Numero credito:");
        txtLoanNumberSearch.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        txtLoanNumberSearch.setPlaceholder("Ingrese credito");
        gridLayout.addComponent(txtLoanNumberSearch,0,0);

        gridLayout.addComponent(buildClientPanel(),0,1,4,3);
        gridLayout.addComponent(buildLoanPanel(),0,4,4,6);
        gridLayout.addComponent(buildTabSupplementaryData(),0,7,4,8);

        btnImport=new Button("Buscar");
        btnImport.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnImport.setIcon(VaadinIcons.SEARCH);
        gridLayout.addComponent(btnImport,1,0);
        gridLayout.setComponentAlignment(btnImport,Alignment.BOTTOM_LEFT);
        return gridLayout;
    }

    private Panel buildLoanPanel(){
        panelLoan = new Panel( );//"<font size=3 color=#163759> Datos generales del credito<font>");
        panelLoan.setHeight("145px");
        panelLoan.setCaptionAsHtml(true);
        panelLoan.setStyleName(ValoTheme.PANEL_WELL);
        GridLayout gridLayoutLoan = new GridLayout();
        gridLayoutLoan.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutLoan.setColumns(8);
        gridLayoutLoan.setRows(5);
        gridLayoutLoan.setHeight("110px");
        gridLayoutLoan.setSpacing(true);

        txtLoanNumber = new TextField("Nro. credito:");
        txtLoanNumber.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanNumber.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanNumber,0,0);

        txtCurrency = new TextField("Moneda:");
        txtCurrency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCurrency.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtCurrency,1,0);

        txtLoanMount = new TextField("Monto:");
        txtLoanMount.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanMount.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanMount,2,0);

        txtLoanTerm = new TextField("Plazo:");
        txtLoanTerm.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanTerm.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanTerm,3,0);

        txtInterestRate = new TextField("Tasa:");
        txtInterestRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtInterestRate.setReadOnly(true);;

        gridLayoutLoan.addComponent(txtInterestRate,4,0);

        txtCreditLifeInsurance = new TextField("Seguro desgravamen:");
        txtCreditLifeInsurance.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCreditLifeInsurance.setReadOnly(true);
        gridLayoutLoan.addComponent(txtCreditLifeInsurance,5,0);

        txtFixedPaymentDay = new TextField("Dia fijo de pago:");
        txtFixedPaymentDay.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtFixedPaymentDay.setReadOnly(true);
        gridLayoutLoan.addComponent(txtFixedPaymentDay,0,1);

        txtPaymentFrecuency = new TextField("Frecuencia pago:");
        txtPaymentFrecuency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtPaymentFrecuency.setReadOnly(true);
        gridLayoutLoan.addComponent(txtPaymentFrecuency,1,1);

        txtOfficial = new TextField("Oficial:");
        txtOfficial.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtOfficial.setReadOnly(true);
        gridLayoutLoan.addComponent(txtOfficial,2,1);

        txtAgency = new TextField("Agencia:");
        txtAgency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAgency.setReadOnly(true);
        gridLayoutLoan.addComponent(txtAgency,3,1);



        panelLoan.setContent(gridLayoutLoan);


        return panelLoan;
    }

    private Panel buildClientPanel(){
        panelClient = new Panel("<font size=3 color=#163759> Datos cliente y credito <font>");
        panelClient.setCaptionAsHtml(true);
        panelClient.setStyleName(ValoTheme.PANEL_WELL);
        panelClient.setHeight("105px");

        GridLayout gridLayoutClient = new GridLayout();
        gridLayoutClient.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutClient.setColumns(8);
        gridLayoutClient.setRows(5);
        gridLayoutClient.setSpacing(true);
        gridLayoutClient.setSizeFull();

        txtClientLoanId = new TextField("Codigo agenda:");
        txtClientLoanId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtClientLoanId.setReadOnly(true);
        gridLayoutClient.addComponent(txtClientLoanId,0,0);

        txtIdentifyCardDebtor = new TextField("Carnet:");
        txtIdentifyCardDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtIdentifyCardDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtIdentifyCardDebtor,1,0);

        txtDebtorName = new TextField("Nombre deudor:");
        txtDebtorName.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDebtorName.setReadOnly(true);
        gridLayoutClient.addComponent(txtDebtorName,2,0);

        txtCivilStatusDebtor = new TextField("Estado civil:");
        txtCivilStatusDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCivilStatusDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtCivilStatusDebtor,3,0);

        txtGenderDebtor = new TextField("Genero:");
        txtGenderDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtGenderDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtGenderDebtor,4,0);

        txtAddressDebtor = new TextField("Direccion:");
        txtAddressDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAddressDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtAddressDebtor,5,0);

        panelClient.setContent(gridLayoutClient);

        return panelClient;
    }

    private TabSheet buildTabSupplementaryData(){
        tabSupplementaryData = new TabSheet();
        tabSupplementaryData.setStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSupplementaryData.addTab(buildGriLayoutSupplentaryData(),"Datos complementarios");


        return tabSupplementaryData;
    }

    private GridLayout buildGriLayoutSupplentaryData(){
        gridLayoutSupplementaryData = new GridLayout();
        gridLayoutSupplementaryData.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutSupplementaryData.setRows(4);
        gridLayoutSupplementaryData.setColumns(7);
        gridLayoutSupplementaryData.setSpacing(true);

        dateLoanDate = new DateField("Fecha desembolso:");
        dateLoanDate.setStyleName(ValoTheme.DATEFIELD_TINY);
        dateLoanDate.setDateFormat("dd-MM-yyyy");
        gridLayoutSupplementaryData.addComponent(dateLoanDate,0,0);

        txtTreRate = new TextField("Tasa TRE:");
        txtTreRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTreRate,1,0);

        txtTeacRate = new TextField("Tasa TEAC:");
        txtTeacRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTeacRate,2,0);

        txtFeePayment = new TextField("Cuota:");
        txtFeePayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtFeePayment,3,0);

        txtTotalPayment = new TextField("Total a cancelar:");
        txtTotalPayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTotalPayment,4,0);

        txtLoanDestination = new TextField("Destino del credito:");
        txtLoanDestination.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanDestination.setWidth("360px");
        gridLayoutSupplementaryData.addComponent(txtLoanDestination,0,1,3,1);

        btnSaveSupplementaryData = new Button("Guardar");
        btnSaveSupplementaryData.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveSupplementaryData.setIcon(VaadinIcons.SAFE);
        gridLayoutSupplementaryData.addComponent(btnSaveSupplementaryData,0,2);

        return gridLayoutSupplementaryData;
    }

}
