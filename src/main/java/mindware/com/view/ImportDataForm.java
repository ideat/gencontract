package mindware.com.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.Action;
import com.vaadin.ui.renderers.TextRenderer;
import de.steinwedel.messagebox.MessageBox;
import mindware.com.model.BranchOffice;
import mindware.com.model.CoDebtorGuarantor;

import mindware.com.model.LoanData;
import mindware.com.model.Warranty;
import mindware.com.netbank.model.ClientLoanNetbank;
import mindware.com.netbank.model.CodebtorGuarantorNetbank;
import mindware.com.netbank.model.WarrantyNetbank;
import mindware.com.netbank.service.ClientLoanNetBankService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.netbank.service.CodebtorGuarantorNetbankService;
import mindware.com.netbank.service.WarrantyNetBankService;
import mindware.com.service.BranchOfficeService;
import mindware.com.service.LoanDataService;
import mindware.com.utilities.Util;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ImportDataForm extends CustomComponent implements View {
    private Button btnSearch;
    private Button btnImport;
//    private Button btnUpdate;

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
//    private TextField txtAgency;
    private TextField txtFixedPaymentDay;
    private TextField txtOfficial;
    private TextField txtDebtorName;
    private TextField txtIdentifyCardDebtor;
    private TextField txtAddressDebtor;
    private TextField txtCivilStatusDebtor;
    private TextField txtGenderDebtor;
    private TextField txtClientLoanId;
    private TextField txtSavingBox;
    private TextField txtSpread;
    private TextField txtLoanLine;
    private TextField txtLineSpread;
    private TextField txtLineRate;
    private TextField txtLineMount;
    private TextField txtLineTerm;
    private ComboBox<BranchOffice> cmbAgency;

    private Grid<?> gridCoDebtor;
    private Grid<?> gridGuarantor;

    private GridLayout gridLayout;
    private Panel panelLoan;
    private Panel panelClient;
    private Panel panelWarranty;
    private Panel panelSupplentaryData;
    private Panel panelCoDebtor;
    private Panel panelGuarantor;
    private Panel panelLineCredit;
    private TabSheet tabSupplementaryData;
    private GridLayout gridLayoutSupplementaryData;
    private GridLayout gridLayoutLineCredit;

    private  Grid<?> gridWarranty;

    private ClientLoanNetbank clientLoanNetbank;
    private List<WarrantyNetbank> warrantyNetbankList;
    private List<CodebtorGuarantorNetbank> codebtorNetbankList;
    private List<CodebtorGuarantorNetbank> guarantorNetbankList;
    private Integer loanDataId;
    private boolean local;

    public ImportDataForm(){
        setCompositionRoot(buildGridLayout());
        postBuild();
    }

    private void postBuild(){
        fillAgency();

        btnSearch.addClickListener(clickEvent -> {
            LoanDataService loanDataService = new LoanDataService();
            if (validateLoanNumber()) {
                LoanData loanData = loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txtLoanNumberSearch.getValue()));
                if (loanData != null){
                    MessageBox.createQuestion()
                            .withCaption("Advertencia")
                            .withMessage("Credito ya fue importado, desea volver a cargar desde el servidor? ")
                            .withYesButton(()->{
                                clearFieldData();
                                ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
                                clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
                                dataFromNetBank();
                                loanDataId = loanData.getLoanDataId();
//                                btnImport.setEnabled(true);
                            })
                            .withNoButton( ()->{
                                loadLoanDataLocal(loanData);
//                                btnImport.setEnabled(false);

                            })
                            .open();


                } else {
                    clearFieldData();
                    ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
                    clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
                    if (clientLoanNetbank != null) {
                        dataFromNetBank();
                        btnImport.setEnabled(true);
                    } else {
                        Notification.show("Datos credito",
                                "Credito no encontrado",
                                Notification.Type.WARNING_MESSAGE);
                    }
                }
            }
            else {
                Notification.show("Buscar credito",
                        "Ingrese un numero de credito valido",
                        Notification.Type.ERROR_MESSAGE);
                txtLoanNumberSearch.focus();

            }

        });

        btnImport.addClickListener(clickEvent -> {
            LoanDataService loanDataService = new LoanDataService();

            if (StringUtils.isNumeric(txtLoanNumberSearch.getValue())) {
                LoanData result = loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txtLoanNumberSearch.getValue()));
                if (result == null) {
                    if (validateLoanData()) {
                        if (validateDataType().equals("OK")) {
                            Util util = new Util();
                            LoanData loanData = new LoanData();
                            loanData.setDebtorName(txtDebtorName.getValue().toString().trim());
                            loanData.setIdentityCardDebtor(txtIdentifyCardDebtor.getValue());
                            loanData.setClientLoanId(Integer.parseInt(txtClientLoanId.getValue().toString().trim()));
                            loanData.setAddressDebtor(txtAddressDebtor.getValue().toString().trim());
                            loanData.setCivilStatusDebtor(txtCivilStatusDebtor.getValue().trim());
                            loanData.setGenderDebtor(txtGenderDebtor.getValue().trim());
                            loanData.setLoanNumber(Integer.parseInt(txtLoanNumber.getValue()));
                            loanData.setCurrency(txtCurrency.getValue().trim());
                            loanData.setLoanMount(Double.parseDouble(txtLoanMount.getValue().trim()));
                            loanData.setLoanTerm(Integer.parseInt(txtLoanTerm.getValue().trim()));
                            loanData.setInterestRate(Double.parseDouble(txtInterestRate.getValue().trim()));
                            loanData.setCreditLifeInsurance(Double.parseDouble(txtCreditLifeInsurance.getValue().trim()));
                            loanData.setFixedPaymentDay(Integer.parseInt(txtFixedPaymentDay.getValue().trim()));
                            loanData.setPaymentFrecuency(txtPaymentFrecuency.getValue().trim());
                            loanData.setBranchOfficeId(cmbAgency.getValue().getBranchOfficeId());
                            loanData.setOfficial(txtOfficial.getValue().trim());
                            loanData.setAgency(String.valueOf(cmbAgency.getValue().getBranchOfficeId()));

                            loanData.setTeacRate(Double.parseDouble(txtTeacRate.getValue().toString().trim()));
                            loanData.setTreRate(Double.parseDouble(txtTreRate.getValue().toString().trim()));
                            loanData.setFeePayment(Double.parseDouble(txtFeePayment.getValue().toString().trim()));
                            loanData.setLoanDestination(txtLoanDestination.getValue().toString().trim());
                            loanData.setLoanDate(util.stringToDate(dateLoanDate.getValue().toString(), "yyyy-MM-dd"));
                            loanData.setTotalPayment(Double.parseDouble(txtTotalPayment.getValue().toString().trim()));
                            loanData.setSavingBox(txtSavingBox.getValue());
                            loanData.setSpread(Double.parseDouble(txtSpread.getValue()));
                            loanData.setLoanLine(Integer.parseInt(txtLoanLine.getValue()));
                            loanData.setLineRate(Double.parseDouble(txtLineRate.getValue()));
                            loanData.setLineSpread(Double.parseDouble(txtLineSpread.getValue()));
                            loanData.setLineMount(Double.parseDouble(txtLineMount.getValue()));
                            loanData.setLineTerm(Integer.parseInt(txtLineTerm.getValue()));

                            ObjectMapper mapper = new ObjectMapper();
                            String jsonCodebtor = null;

                            try {
                                jsonCodebtor = mapper.writeValueAsString(getCoDebtorsGuarantors("codebtor", codebtorNetbankList));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }

                            String jsonGuarantor = null;
                            try {
                                jsonGuarantor = mapper.writeValueAsString(getCoDebtorsGuarantors("guarantor", guarantorNetbankList));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }

                            String jsonWarranty = null;
                            try {
                                jsonWarranty = mapper.writeValueAsString(getWarranty(warrantyNetbankList));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }

                            loanData.setGuarantors(jsonGuarantor);
                            loanData.setCoDebtors(jsonCodebtor);
                            loanData.setWarranty(jsonWarranty);

                            if (loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txtLoanNumberSearch.getValue())) == null) {
                                loanDataService.insertLoanData(loanData);
                                Notification.show("Importacion datos",
                                        "Datos del credito importados!",
                                        Notification.Type.HUMANIZED_MESSAGE);
                            } else {
                                loanData.setLoanDataId(loanDataId);
                                loanDataService.updateLoanData(loanData);
                                Notification.show("Importacion datos",
                                        "Datos del credito actualizados!",
                                        Notification.Type.HUMANIZED_MESSAGE);
                            }

                        } else {
                            Notification.show("ERROR",
                                    validateDataType(),
                                    Notification.Type.WARNING_MESSAGE);
                        }
                    } else {
                        Notification.show("ERROR",
                                "Datos incompeltos, complete la informacion",
                                Notification.Type.WARNING_MESSAGE);
                    }
                }else
                if (result != null && validateLoanData()) {
                    LoanData loanData = new LoanData();
                    loanData.setLoanDataId(loanDataId);
                    loanData.setTeacRate(Double.parseDouble(txtTeacRate.getValue().toString().trim()));
                    loanData.setTreRate(Double.parseDouble(txtTreRate.getValue().toString().trim()));
                    loanData.setFeePayment(Double.parseDouble(txtFeePayment.getValue().toString().trim()));
                    loanData.setLoanDestination(txtLoanDestination.getValue().toString().trim());
                    loanData.setTotalPayment(Double.parseDouble(txtTotalPayment.getValue().toString().trim()));
                    loanData.setSpread(Double.parseDouble(txtSpread.getValue().trim()));
                    loanData.setSavingBox(txtSavingBox.getValue());
                    loanData.setAddressDebtor(txtAddressDebtor.getValue());
                    loanData.setCivilStatusDebtor(txtCivilStatusDebtor.getValue());
                    loanData.setAgency(String.valueOf(cmbAgency.getValue().getBranchOfficeId()));
                    loanData.setLoanTerm(Integer.parseInt(txtLoanTerm.getValue()));
                    loanData.setBranchOfficeId(cmbAgency.getValue().getBranchOfficeId());
                    loanData.setCreditLifeInsurance(Double.parseDouble(txtCreditLifeInsurance.getValue()));

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonGuarantor = null;

//                    ListDataProvider<CoDebtorGuarantor> gurantorGuarantorList = (ListDataProvider<CoDebtorGuarantor>) gridGuarantor.getDataProvider();
                    List<?> gurantorGuarantorList = new ArrayList<>();

                        gurantorGuarantorList = (List<?>) gridGuarantor.getDataCommunicator().fetchItemsWithRange(0, gridGuarantor.getDataCommunicator().getDataProviderSize());
                        if (gurantorGuarantorList.size()>0)
                        if (gurantorGuarantorList.get(0).getClass().toString().contains("Netbank")){
                            gurantorGuarantorList = getCoDebtorsGuarantors("guarantor", (List<CodebtorGuarantorNetbank>) gurantorGuarantorList);
                        }
//                        else
//                            gurantorGuarantorList =  getCoDebtorsGuarantors("guarantor", (List<CodebtorGuarantorNetbank>) gridGuarantor.getDataCommunicator().fetchItemsWithRange(0,gridGuarantor.getDataCommunicator().getDataProviderSize()));


//                    List<CoDebtorGuarantor> guarantorList2= (List<CoDebtorGuarantor>) gurantorGuarantorList.getItems();
                    try {
                        jsonGuarantor = mapper.writeValueAsString(gurantorGuarantorList);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    loanData.setGuarantors(jsonGuarantor);

                    String jsonCodebtor = null;

                    List<?> coDebtorGuarantorList = new ArrayList<>();
                    coDebtorGuarantorList = (List<?>) gridCoDebtor.getDataCommunicator().fetchItemsWithRange(0,gridCoDebtor.getDataCommunicator().getDataProviderSize());
                    if (coDebtorGuarantorList.size()>0)
                    if ( coDebtorGuarantorList.get(0).getClass().toString().contains("Netbank"))
                        coDebtorGuarantorList = getCoDebtorsGuarantors("codebtor",  (List<CodebtorGuarantorNetbank>) coDebtorGuarantorList);
//


//                    List<CoDebtorGuarantor> coDebtorGuarantorList2= (List<CoDebtorGuarantor>) coDebtorGuarantorList.getItems();
                    try {
                        jsonCodebtor = mapper.writeValueAsString(coDebtorGuarantorList);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    loanData.setCoDebtors(jsonCodebtor);
                    loanData.setLoanDataId(result.getLoanDataId());
                    loanDataService.updateInputData(loanData);
                    Notification.show("Actualizar",
                            "Datos actualizado!",
                            Notification.Type.HUMANIZED_MESSAGE);

                }else {
                    Notification.show("ERROR",
                            "Datos incompeltos, complete la informacion",
                            Notification.Type.WARNING_MESSAGE);
                }

            }else {
                Notification.show("Error",
                        "Ingrese el numero de credito",
                        Notification.Type.ERROR_MESSAGE);
                txtLoanNumberSearch.focus();
            }

        });


        gridGuarantor.getEditor().addSaveListener(editorSaveEvent -> {
           if (guarantorNetbankList!=null) {
               ListDataProvider<CodebtorGuarantorNetbank> guarantorListDataProvider = (ListDataProvider<CodebtorGuarantorNetbank>) gridGuarantor.getDataProvider();
               ObjectMapper mapper = new ObjectMapper();
               Collection<CodebtorGuarantorNetbank> coDebtorListDataProvider2= guarantorListDataProvider.getItems();
               for (CodebtorGuarantorNetbank codebtorGuarantorNetbank1: coDebtorListDataProvider2){
                   if (codebtorGuarantorNetbank1.getGbagedir().isEmpty()){
                       Notification.show("Error",
                               "Direccion domicilio es obligatoria",
                               Notification.Type.ERROR_MESSAGE);
                       codebtorGuarantorNetbank1.setGbagedir("Ingrese direccion!");
                   }
               }

           }else{
               ListDataProvider<CoDebtorGuarantor> guarantorListDataProvider = (ListDataProvider<CoDebtorGuarantor>) gridGuarantor.getDataProvider();
               ObjectMapper mapper = new ObjectMapper();
               Collection<CoDebtorGuarantor> coDebtorListDataProvider2= guarantorListDataProvider.getItems();
               for (CoDebtorGuarantor coDebtorGuarantor: coDebtorListDataProvider2){
                   if (coDebtorGuarantor.getAddressHome().isEmpty()){
                       Notification.show("Error",
                               "Direccion domicilio es obligatoria",
                               Notification.Type.ERROR_MESSAGE);
                       coDebtorGuarantor.setAddressHome("Ingrese direccion!");
                   }
               }
           }

        });

        gridCoDebtor.getEditor().addSaveListener(editorSaveEvent ->{
            if (codebtorNetbankList!=null) {
                ListDataProvider<CodebtorGuarantorNetbank> codebtorListDataProvider = (ListDataProvider<CodebtorGuarantorNetbank>) gridCoDebtor.getDataProvider();
                ObjectMapper mapper = new ObjectMapper();
                Collection<CodebtorGuarantorNetbank> coDebtorListDataProvider2= codebtorListDataProvider.getItems();
                for (CodebtorGuarantorNetbank codebtorGuarantorNetbank1: coDebtorListDataProvider2){
                    if (codebtorGuarantorNetbank1.getGbagedir().isEmpty()){
                        Notification.show("Error",
                                "Direccion domicilio es obligatoria",
                                Notification.Type.ERROR_MESSAGE);
                        codebtorGuarantorNetbank1.setGbagedir("Ingrese direccion!");
                    }
                }

            }else{
                ListDataProvider<CoDebtorGuarantor> codebtorListDataProvider = (ListDataProvider<CoDebtorGuarantor>) gridCoDebtor.getDataProvider();
                ObjectMapper mapper = new ObjectMapper();
                Collection<CoDebtorGuarantor> coDebtorListDataProvider2= codebtorListDataProvider.getItems();
                for (CoDebtorGuarantor coDebtorGuarantor: coDebtorListDataProvider2){
                    if (coDebtorGuarantor.getAddressHome().isEmpty()){
                        Notification.show("Error",
                                "Direccion domicilio es obligatoria",
                                Notification.Type.ERROR_MESSAGE);
                        coDebtorGuarantor.setAddressHome("Ingrese direccion!");
                    }
                }
            }
        });


    }

    private boolean validateLoanNumber(){
        try {
           if (NumberUtils.toInt(txtLoanNumberSearch.getValue())==0)
           return false;
           else return true;

        }catch (Exception e){
            return false;
        }

    }

    private void fillAgency(){
        BranchOfficeService branchOfficeService = new BranchOfficeService();
        List<BranchOffice> branchOfficeList = branchOfficeService.findAllBranchOffice();
        cmbAgency.setItems(branchOfficeList);
        cmbAgency.setItemCaptionGenerator(BranchOffice::getBranchName);
    }

    private BranchOffice getBranchOffice(int branchOfficeId){
        BranchOfficeService branchOfficeService = new BranchOfficeService();
        return branchOfficeService.findBranchOfficeById(branchOfficeId);
    }

    private void dataFromNetBank() {

        WarrantyNetBankService warrantyNetBankService = new WarrantyNetBankService();
        warrantyNetbankList = warrantyNetBankService.findWarrantyNetbankByCreCod(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
        CodebtorGuarantorNetbankService codebtorGuarantorNetbankService = new CodebtorGuarantorNetbankService();
        codebtorNetbankList = codebtorGuarantorNetbankService.findCodeptorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
        guarantorNetbankList = codebtorGuarantorNetbankService.findGuarantorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));

        gridGuarantor = null;
        gridGuarantor = new Grid();
        gridGuarantor.setStyleName(ValoTheme.TABLE_SMALL);
        gridGuarantor.setSizeFull();
        gridGuarantor.getEditor().setEnabled(true);
        gridGuarantor.getEditor().setCancelCaption("Cancelar");
        gridGuarantor.getEditor().setSaveCaption("Guardar");
        panelGuarantor.setContent(gridGuarantor);

        gridCoDebtor = null;
        gridCoDebtor = new Grid();
        gridCoDebtor.setStyleName(ValoTheme.TABLE_SMALL);
        gridCoDebtor.setSizeFull();
        gridCoDebtor.getEditor().setEnabled(true);
        gridCoDebtor.getEditor().setCancelCaption("Cancelar");
        gridCoDebtor.getEditor().setSaveCaption("Guardar");
        panelCoDebtor.setContent(gridCoDebtor);

        replaceCharacters(codebtorNetbankList);
        replaceCharacters(guarantorNetbankList);
        loadCodebtorGuarantorNetbank(codebtorNetbankList, (Grid<CodebtorGuarantorNetbank>) gridCoDebtor);
        loadCodebtorGuarantorNetbank(guarantorNetbankList, (Grid<CodebtorGuarantorNetbank>) gridGuarantor);

        loadNetBankData(clientLoanNetbank);
        loadWarrantyNetBank(warrantyNetbankList, (Grid<WarrantyNetbank>) gridWarranty);
    }

    private void replaceCharacters(List<CodebtorGuarantorNetbank> codebtorGuarantorNetbankList) {
        List<CodebtorGuarantorNetbank> aux = new ArrayList<>();
        for (CodebtorGuarantorNetbank cn:codebtorGuarantorNetbankList){
            cn.setGbagenomb( cn.getGbagenomb().replace("¥","Ñ").trim());
            cn.setGbagedir(cn.getGbagedir().replace("¥","Ñ").trim());
            aux.add(cn);
        }
        codebtorGuarantorNetbankList = aux;
    }

    private boolean validateLoanData(){
        if (txtLoanNumber.isEmpty()) return false;
        if (dateLoanDate.isEmpty()) return false;
        if (txtTreRate.isEmpty()) return false;
        if (txtTeacRate.isEmpty()) return false;
        if (txtFeePayment.isEmpty()) return false;
        if (txtTotalPayment.isEmpty()) return false;
        if (txtLoanDestination.isEmpty()) return false;
        if (txtSavingBox.isEmpty()) return false;
        if (txtAddressDebtor.isEmpty()) return false;
        if (txtCivilStatusDebtor.isEmpty()) return false;
        if (cmbAgency.isEmpty()) return false;
        if (txtLoanTerm.isEmpty()) return false;
        return true;
    }

    private String validateDataType(){
        Util util = new Util();
        if (!util.isNumber(txtTreRate.getValue())) return "Tasa Base no es valida";
        if (!util.isNumber(txtTeacRate.getValue())) return "Tasa TEAC no es valida";
        if (!util.isNumber(txtFeePayment.getValue())) return "Monto cuota no es valido";
        if(!util.isNumber(txtTotalPayment.getValue())) return "Total a pagar no es valido";
        if (!util.isNumber(txtCreditLifeInsurance.getValue())) return "Seguro desgravamen no es valido";
        if (!util.isNumber(txtSpread.getValue())) return "Spread no es valido";
        if (!util.isNumber(txtLoanTerm.getValue())) return "Plazo no es valido";

        return "OK";
    }

    private void loadLoanDataLocal(LoanData loanData){
        loanDataId = loanData.getLoanDataId();
        txtLoanNumber.setValue(txtLoanNumberSearch.getValue());
        txtClientLoanId.setValue(loanData.getClientLoanId().toString());
        txtIdentifyCardDebtor.setValue(loanData.getIdentityCardDebtor());
        txtDebtorName.setValue(loanData.getDebtorName());
        txtCivilStatusDebtor.setValue(loanData.getCivilStatusDebtor());
        txtGenderDebtor.setValue(loanData.getGenderDebtor());
        txtAddressDebtor.setValue(loanData.getAddressDebtor());
        txtCurrency.setValue(loanData.getCurrency());
        txtLoanMount.setValue(loanData.getLoanMount().toString());
        txtInterestRate.setValue(loanData.getInterestRate().toString());
        txtLoanTerm.setValue(loanData.getLoanTerm().toString());
        txtFixedPaymentDay.setValue(loanData.getFixedPaymentDay().toString());
        txtPaymentFrecuency.setValue(loanData.getPaymentFrecuency());
        txtCreditLifeInsurance.setValue(loanData.getCreditLifeInsurance().toString());
        txtOfficial.setValue(loanData.getOfficial());
        cmbAgency.setValue(getBranchOffice(Integer.parseInt(loanData.getAgency())));

        txtTeacRate.setValue(loanData.getTeacRate().toString());
        txtTreRate.setValue(loanData.getTreRate().toString());
        txtFeePayment.setValue(loanData.getFeePayment().toString());
        txtLoanDestination.setValue(loanData.getLoanDestination());
        Date date =  loanData.getLoanDate();
        dateLoanDate.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        txtTotalPayment.setValue(loanData.getTotalPayment().toString());
        dateLoanDate.setValue(new Util().stringToLocalDate(loanData.getLoanDate().toString(),"dd-MM-yyyy"));
        txtSavingBox.setValue(String.valueOf(loanData.getSavingBox()));
        txtSpread.setValue(loanData.getSpread().toString());
        txtLoanLine.setValue(loanData.getLoanLine().toString());
        txtLineTerm.setValue(loanData.getLineTerm().toString());
        txtLineSpread.setValue(loanData.getLineSpread().toString());
        txtLineRate.setValue(loanData.getLineRate().toString());
        txtLineMount.setValue(loanData.getLineMount().toString());

        gridCoDebtor.removeAllColumns();
        gridGuarantor.removeAllColumns();
        gridWarranty.removeAllColumns();


        gridGuarantor = null;
        gridGuarantor = new Grid();
        gridGuarantor.setStyleName(ValoTheme.TABLE_SMALL);
        gridGuarantor.setSizeFull();
        gridGuarantor.getEditor().setEnabled(true);
        gridGuarantor.getEditor().setCancelCaption("Cancelar");
        gridGuarantor.getEditor().setSaveCaption("Guardar");
        panelGuarantor.setContent(gridGuarantor);

        gridCoDebtor = null;
        gridCoDebtor = new Grid();
        gridCoDebtor.setStyleName(ValoTheme.TABLE_SMALL);
        gridCoDebtor.setSizeFull();
        gridCoDebtor.getEditor().setEnabled(true);
        gridCoDebtor.getEditor().setCancelCaption("Cancelar");
        gridCoDebtor.getEditor().setSaveCaption("Guardar");
        panelCoDebtor.setContent(gridCoDebtor);

        loadDataCoDebtors(loanData.getCoDebtors(), (Grid<CoDebtorGuarantor>) gridCoDebtor);
        loadDataCoDebtors(loanData.getGuarantors(), (Grid<CoDebtorGuarantor>) gridGuarantor);
        loadWarranty(loanData.getWarranty(), (Grid<Warranty>) gridWarranty);
    }

    private void loadDataCoDebtors(String coDebtorGuarantor, Grid<CoDebtorGuarantor> gridCoDebtorGuarantor) {
        if (!coDebtorGuarantor.equals("[]")){
            ObjectMapper mapper = new ObjectMapper();
            List<CoDebtorGuarantor> coDebtorLocalList = new ArrayList<>();

            try {
                coDebtorLocalList = Arrays.asList(mapper.readValue(coDebtorGuarantor,CoDebtorGuarantor[].class));

               fillGridCoDebtorGuarantor(coDebtorLocalList,gridCoDebtorGuarantor);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadWarranty(String warranty, Grid<Warranty> gridWarranty){
        if (!warranty.equals("[]")){
            ObjectMapper mapper = new ObjectMapper();
            List<Warranty> warrantyList = new ArrayList<>();
            try {
                warrantyList = Arrays.asList(mapper.readValue(warranty,Warranty[].class));
                fillGridWarranty(warrantyList,gridWarranty);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillGridWarranty(List<Warranty> warrantyList, Grid<Warranty> gridWarranty){
        gridWarranty.removeAllColumns();
        gridWarranty.setItems(warrantyList);

        gridWarranty.addComponentColumn(warranty -> {
            Button button = new Button();
            button.setIcon(VaadinIcons.PENCIL);
            button.setStyleName(ValoTheme.BUTTON_PRIMARY);
            button.addClickListener(clickEvent -> {
                if(warranty.getCodeGuarantee()!=null)
                if (warranty.getCodeGuarantee().equals("BM2") || warranty.getCodeGuarantee().equals("BE2") ) {
                    WarrantyWindowForm warrantyWindowForm = new WarrantyWindowForm(warranty, warrantyList, "local");
                    warrantyWindowForm.setModal(true);
                    warrantyWindowForm.setWidth("550px");
                    warrantyWindowForm.setHeight("250px");
                    warrantyWindowForm.setResizable(true);
                    warrantyWindowForm.center();
                    UI.getCurrent().addWindow(warrantyWindowForm);
                    warrantyWindowForm.addCloseListener(closeEvent -> {
                        gridWarranty.setItems(warrantyList);
                    });
                }else{
                    Notification.show("DATOS GARANTIA",
                            "La garantia no es un DPF, no se puede editar",
                            Notification.Type.ERROR_MESSAGE);
                }
            });
            return button;
        });

        gridWarranty.addColumn(Warranty::getLoanNumber).setCaption("Nro prestamo");
        gridWarranty.addColumn(Warranty::getTypeGuarantee).setCaption("Tipo garantia");
        gridWarranty.addColumn(Warranty::getCurrency).setCaption("Moneda");
        gridWarranty.addColumn(Warranty::getAssessmentEntity).setCaption("Grav. Fav. Entidad");
        gridWarranty.addColumn(Warranty::getExpirationDate,new TextRenderer("-")).setCaption("Fecha venc");
        gridWarranty.addColumn(Warranty::getNumberRealRight,new TextRenderer("-")).setCaption("Nro. Part. DDRR");
        gridWarranty.addColumn(Warranty::getDateRealRight,new TextRenderer("-")).setCaption("Fecha partida DDRR");
        gridWarranty.addColumn(Warranty::getMortageNumber, new TextRenderer("-")).setCaption("Nro hip.");
        gridWarranty.addColumn(Warranty::getDateMortage, new TextRenderer("-")).setCaption("Fecha hip.");
        gridWarranty.addColumn(Warranty::getDescription, new TextRenderer("-")).setCaption("Descripcion");
        gridWarranty.addColumn(Warranty::getEnoughGuarante, new TextRenderer("-")).setCaption("Suf. liquida");


    }

    private void fillGridCoDebtorGuarantor(List<CoDebtorGuarantor> coDebtorGuarantorList, Grid<CoDebtorGuarantor> gridCoDebtorGuarantor){

        gridCoDebtorGuarantor.removeAllColumns();

        gridCoDebtorGuarantor.setItems(coDebtorGuarantorList);

        gridCoDebtorGuarantor.addComponentColumn(codebtorGuarantor ->{
           Button button = new Button();
           button.setIcon(VaadinIcons.PENCIL);
           button.setStyleName(ValoTheme.BUTTON_PRIMARY);
           button.addClickListener(clickEvent ->{

               CodebtorGuarantorWindowForm codebtorGuarantorWindowForm = new CodebtorGuarantorWindowForm(codebtorGuarantor,coDebtorGuarantorList,"");
               codebtorGuarantorWindowForm.setModal(true);
               codebtorGuarantorWindowForm.setWidth("650px");
               codebtorGuarantorWindowForm.setHeight("350px");
               codebtorGuarantorWindowForm.setResizable(true);
               codebtorGuarantorWindowForm.center();
               UI.getCurrent().addWindow(codebtorGuarantorWindowForm);
               codebtorGuarantorWindowForm.addCloseListener(closeEvent ->{
                    gridCoDebtorGuarantor.setItems(coDebtorGuarantorList);
              });
           });
           return button;
        });

        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getNumberLoan).setCaption("Nro Prestamo");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCodeMebership).setCaption("Nro agenda");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getName).setCaption("Nombre");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getIdentifyCard).setCaption("Carnet");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCivilStatus).setCaption("Estado civil");
        gridCoDebtorGuarantor.addComponentColumn(item -> {
            Label label = new Label();
            label.setValue(item.getAddressHome());
            label.setWidthUndefined();
            label.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
            return label;
        })
                .setCaption("Dir. domicilio")
                .setWidth(280.0);
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getNumeroCasa).setCaption("Nro domicilio");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getAdyacentes).setCaption("Adyacentes");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getZona).setCaption("Zona/Barrio");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCiudad).setCaption("Ciudad/Localidad");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getProvincia).setCaption("Provincia");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getDepartamento).setCaption("Departamento");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getTipoDireccion).setCaption("Tipo direccion");


//        Binder<CoDebtorGuarantor> binder = gridCoDebtorGuarantor.getEditor().getBinder();
//        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCivilStatus,new TextRenderer())
//                .setEditorBinding(binder
//                        .forField(new TextField())
//                        .bind(CoDebtorGuarantor::getCivilStatus,CoDebtorGuarantor::setCivilStatus)
//                ).setCaption("Estado civil");
//
//        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getAddressHome,new TextRenderer())
//                .setEditorBinding(binder
//                        .forField(new TextField())
//                        .bind(CoDebtorGuarantor::getAddressHome, CoDebtorGuarantor::setAddressHome)
//                ).setCaption("Dir. domicilio");

        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getAddressOffice).setCaption("Dir. oficina");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getGender).setCaption("Genero");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getPrioridad).setCaption("Prioridad");

        gridCoDebtorGuarantor.setFrozenColumnCount(1);
    }



    private List<Warranty> getWarranty(List<WarrantyNetbank> warrantyNetbanks){
        List<Warranty> warrantyList = new ArrayList<>();
        Util util = new Util();

        for(WarrantyNetbank warrantyNetbank:warrantyNetbanks){
            Warranty warranty = new Warranty();
            warranty.setId(warrantyNetbank.getPrgarcorr());
            warranty.setLoanNumber(warrantyNetbank.getPrgarnpre());
            warranty.setTypeGuarantee(warrantyNetbank.getGbtgadesc().trim());
            warranty.setCurrency(warrantyNetbank.getPrgarcmon().trim());
            warranty.setAssessmentEntity(warrantyNetbank.getPrgargfin());
            if (warrantyNetbank.getPrgarfvto()!=null)
                warranty.setExpirationDate(util.stringToDate(warrantyNetbank.getPrgarfvto(),"yyyy-MM-dd"));
            warranty.setNumberRealRight(warrantyNetbank.getPrgarnpar());
            if (warrantyNetbank.getPrgarfpar()!= null)
                warranty.setDateRealRight(util.stringToDate(warrantyNetbank.getPrgarfpar(),"yyyy-MM-dd"));
            warranty.setMortageNumber(warrantyNetbank.getPrgarnhip());
            if(warrantyNetbank.getPrgarfhip() != null)
                warranty.setDateMortage(util.stringToDate(warrantyNetbank.getPrgarfhip(),"yyyy-MM-dd"));
            if (warrantyNetbank.getPrgardesc()!= null)
                warranty.setDescription(warrantyNetbank.getPrgardesc().trim());
            if (warrantyNetbank.getPrgarsufl()!= null)
                warranty.setEnoughGuarante(warrantyNetbank.getPrgarsufl().trim());

            if (warrantyNetbank.getGbtgacsup() != null)
                warranty.setCodeGuarantee(warrantyNetbank.getGbtgacsup().trim());
            if (warrantyNetbank.getEntidadEmisora() != null)
                warranty.setEntidadEmisora(warrantyNetbank.getEntidadEmisora().trim());
            if (warrantyNetbank.getNumeroCUI() != null)
                warranty.setNumeroCUI(warrantyNetbank.getNumeroCUI().trim());
            if (warrantyNetbank.getNumeroPizarra() != null)
                warranty.setNumeroPizarra(warrantyNetbank.getNumeroPizarra().trim());
            if (warrantyNetbank.getTitular() != null)
                warranty.setTitular(warrantyNetbank.getTitular().trim());

            warrantyList.add(warranty);

        }

        return warrantyList;
    }

    private List<CoDebtorGuarantor> getCoDebtorsGuarantors(String type, List<CodebtorGuarantorNetbank> codebtorGuarantorNetbanks){
        List<CoDebtorGuarantor> coDebtorList = new ArrayList<>();
        int i = 1;
        for(CodebtorGuarantorNetbank codebtorGuarantorNetbank: codebtorGuarantorNetbanks){
            CoDebtorGuarantor coDebtorGuarantor = new CoDebtorGuarantor();
            coDebtorGuarantor.setCodeMebership(codebtorGuarantorNetbank.getPrdeucage());
            coDebtorGuarantor.setNumberLoan(codebtorGuarantorNetbank.getPrdeunpre());
            coDebtorGuarantor.setType(type.trim());
            coDebtorGuarantor.setName(codebtorGuarantorNetbank.getGbagenomb().trim());
            coDebtorGuarantor.setIdentifyCard(codebtorGuarantorNetbank.getGbagendid().trim());
            coDebtorGuarantor.setAddressOffice(codebtorGuarantorNetbank.getGbageddo().trim());
            coDebtorGuarantor.setAddressHome(codebtorGuarantorNetbank.getGbagedir().trim());
            coDebtorGuarantor.setCivilStatus(codebtorGuarantorNetbank.getGbageeciv().trim());
            coDebtorGuarantor.setGender(codebtorGuarantorNetbank.getGbagesexo().trim());
            coDebtorGuarantor.setInsured(type.equals("codebtor")?"asegurado":"noAsegurado");
            coDebtorGuarantor.setPrioridad(codebtorGuarantorNetbank.getPrioridad());
            coDebtorGuarantor.setAdyacentes(codebtorGuarantorNetbank.getAdyacentes());
            coDebtorGuarantor.setZona(codebtorGuarantorNetbank.getZona());
            coDebtorGuarantor.setCiudad(codebtorGuarantorNetbank.getCiudad());
            coDebtorGuarantor.setProvincia(codebtorGuarantorNetbank.getProvincia());
            coDebtorGuarantor.setDepartamento(codebtorGuarantorNetbank.getDepartamento());
            coDebtorGuarantor.setTipoDireccion(codebtorGuarantorNetbank.getTipoDireccion());
            coDebtorGuarantor.setNumeroCasa(codebtorGuarantorNetbank.getNumeroCasa());

            coDebtorGuarantor.setId(i);

            i++;

            coDebtorList.add(coDebtorGuarantor);

        }

        return coDebtorList;
    }

    private Grid loadWarrantyNetBank(List<WarrantyNetbank> warrantyNetbankList, Grid<WarrantyNetbank> gridWarranty){
        gridWarranty.removeAllColumns();
        gridWarranty.setItems(warrantyNetbankList);

        gridWarranty.addComponentColumn(warrantyNetbank -> {
           Button button = new Button();
           button.setIcon(VaadinIcons.PENCIL);
           button.setStyleName(ValoTheme.BUTTON_PRIMARY);
           button.addClickListener(clickEvent -> {
               if (warrantyNetbank.getGbtgacsup()!=null)
               if (warrantyNetbank.getGbtgacsup().equals("BM2") || warrantyNetbank.getGbtgacsup().equals("BE2") ) {
                   WarrantyWindowForm warrantyWindowForm = new WarrantyWindowForm(warrantyNetbank, warrantyNetbankList, "netbank");
                   warrantyWindowForm.setModal(true);
                   warrantyWindowForm.setWidth("550px");
                   warrantyWindowForm.setHeight("250px");
                   warrantyWindowForm.setResizable(true);
                   warrantyWindowForm.center();
                   UI.getCurrent().addWindow(warrantyWindowForm);
                   warrantyWindowForm.addCloseListener(closeEvent -> {
                       gridWarranty.setItems(warrantyNetbankList);
                   });
               }else{
                   Notification.show("DATOS GARANTIA",
                           "La garantia no es un DPF, no se puede editar",
                           Notification.Type.ERROR_MESSAGE);
               }
           });
           return button;
        });
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnpre).setCaption("Nro prestamo");
        gridWarranty.addColumn(WarrantyNetbank::getGbtgadesc).setCaption("Tipo garantia");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarcmon).setCaption("Moneda");
        gridWarranty.addColumn(WarrantyNetbank::getPrgargfin).setCaption("Grav. Fav. Entidad");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfvto,new TextRenderer("-")).setCaption("Fecha venc");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnpar,new TextRenderer("-")).setCaption("Nro. Part. DDRR");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfpar,new TextRenderer("-")).setCaption("Fecha partida DDRR");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnhip, new TextRenderer("-")).setCaption("Nro hip.");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfhip, new TextRenderer("-")).setCaption("Fecha hip.");
        gridWarranty.addColumn(WarrantyNetbank::getPrgardesc, new TextRenderer("-")).setCaption("Descripcion");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarsufl, new TextRenderer("-")).setCaption("Suf. liquida");
        return gridWarranty;
    }



    private void loadCodebtorGuarantorNetbank(List<CodebtorGuarantorNetbank> coDebtorList, Grid<CodebtorGuarantorNetbank> gridCoDebtor){

        gridCoDebtor.removeAllColumns();

        gridCoDebtor.setItems(coDebtorList);

        gridCoDebtor.addComponentColumn(codebtorGuarantorNetbank -> {
            Button button = new Button();
            button.setIcon(VaadinIcons.PENCIL);
            button.setStyleName(ValoTheme.BUTTON_PRIMARY);
            button.addClickListener(clickEvent ->{
                CodebtorGuarantorWindowForm codebtorGuarantorWindowForm = new CodebtorGuarantorWindowForm(codebtorGuarantorNetbank, coDebtorList,"netbank");
                codebtorGuarantorWindowForm.setModal(true);
                codebtorGuarantorWindowForm.setWidth("650px");
                codebtorGuarantorWindowForm.setHeight("350px");
                codebtorGuarantorWindowForm.setResizable(true);
                codebtorGuarantorWindowForm.center();
                UI.getCurrent().addWindow(codebtorGuarantorWindowForm);
                codebtorGuarantorWindowForm.addCloseListener(closeEvent ->{
                    gridCoDebtor.setItems(coDebtorList);
                });
            });
            return button;
        });

        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getPrdeunpre).setCaption("Nro prestamo").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getPrdeucage).setCaption("Nro agenda").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagenomb).setCaption("Nombre").toString().replace("¥","Ñ").trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagendid).setCaption("Carnet").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbageeciv).setCaption("Estado civil").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio").toString().trim().replace("¥","Ñ").trim();;

        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getNumeroCasa).setCaption("Nro domicilio").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getAdyacentes).setCaption("Adyacentes").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getZona).setCaption("Zona/Barrio").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getCiudad).setCaption("Ciudad/Localidad").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getProvincia).setCaption("Provincia").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getDepartamento).setCaption("Departamento").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getTipoDireccion).setCaption("Tipo direccion").toString().trim();

//        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio");
//        Binder<CodebtorGuarantorNetbank> binder = gridCoDebtor.getEditor().getBinder();
//        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbageeciv,new TextRenderer())
//                .setEditorBinding(binder
//                        .forField(new TextField())
//                        .bind(CodebtorGuarantorNetbank::getGbageeciv,CodebtorGuarantorNetbank::setGbageeciv)
//                ).setCaption("Estado civil");
//
//        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagedir,new TextRenderer())
//                .setEditorBinding(binder
//                        .forField(new TextField())
//                        .bind(CodebtorGuarantorNetbank::getGbagedir, CodebtorGuarantorNetbank::setGbagedir)
//                ).setCaption("Dir. domicilio");

        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbageddo).setCaption("Dir. oficina").toString().trim().replace("¥","Ñ").trim();;
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagesexo).setCaption("Genero").toString().trim();
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getPrioridad).setCaption("Prioridad");
        gridCoDebtor.setFrozenColumnCount(1);

    }

    private void loadNetBankData(ClientLoanNetbank clientLoanNetbank){
        fieldClientStatus(false);

        txtClientLoanId.setValue(clientLoanNetbank.getPrmprcage().toString());
        txtIdentifyCardDebtor.setValue(String.valueOf(clientLoanNetbank.getGbagendid()));
        txtDebtorName.setValue(String.valueOf(clientLoanNetbank.getGbagenomb().toString().replace("¥","Ñ")));
        String civilStatus = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbageeciv() == null ? "0" : clientLoanNetbank.getGbageeciv().toString())) {
            case 1:
                civilStatus = "SOLTERO(A)";
                break;
            case 2:
                civilStatus = "CASADO(A)";
                break;
            case 3:
                civilStatus = "DIVORCIADO(A)";
                break;
            case 4:
                civilStatus = "VIUDO(A)";
                break;
            case 5:
                civilStatus = "UNION LIBRE";
                break;
            case 6:
                civilStatus = "SEPARADO(A)";
                break;
            default: civilStatus= "DESCONOCIDO";
        }

        txtCivilStatusDebtor.setValue(civilStatus);
        String gender = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbagesexo() == null ? "0" : clientLoanNetbank.getGbagesexo().toString() )){
            case 1:
                gender = "MASCULINO";
                break;
            case 2:
                gender = "FEMENINO";
                break;
            default: gender= "DESCONOCIDO";
        }
        txtGenderDebtor.setValue(gender);
        txtAddressDebtor.setValue(String.valueOf(clientLoanNetbank.getGbagedir()));

        //Load Loan Data
        txtLoanNumber.setValue(clientLoanNetbank.getPrmprnpre().toString());
        String currency=null;
        switch (Integer.parseInt(Integer.valueOf(clientLoanNetbank.getPrmprcmon()).toString())){
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
        txtFixedPaymentDay.setValue(clientLoanNetbank.getPrmprdiap() == null ? "0" : clientLoanNetbank.getPrmprdiap().toString());
        txtPaymentFrecuency.setValue(clientLoanNetbank.getPrmprppgk().toString());
        dateLoanDate.setValue(new Util().stringToLocalDate(clientLoanNetbank.getPrmprfreg().toString(),"dd-MM-yyyy"));
        txtCreditLifeInsurance.setValue("0");
        txtSpread.setValue("0");
        txtOfficial.setValue("");
        txtFeePayment.setValue("0");
        cmbAgency.setValue(getBranchOffice(clientLoanNetbank.getPrmpragen()));
//        txtAgency.setValue(clientLoanNetbank.getPrmpragen().toString());
        txtLoanLine.setValue(clientLoanNetbank.getPrmprlncr().toString());
        txtLineRate.setValue(clientLoanNetbank.getLcmlctasa().toString());
        txtLineMount.setValue(clientLoanNetbank.getLcmlcmapr().toString());
        txtLineSpread.setValue(clientLoanNetbank.getLcmlcsprd().toString());
        txtLineTerm.setValue(clientLoanNetbank.getLcmlcplzo().toString());
        fieldClientStatus(true);

    }

    private void clearFieldData(){
        fieldClientStatus(false);
        txtClientLoanId.clear();
        txtIdentifyCardDebtor.clear();
        txtDebtorName.clear();
        txtCivilStatusDebtor.clear();
        txtGenderDebtor.clear();
        txtAddressDebtor.clear();
        txtLoanNumber.clear();
        txtCurrency.clear();
        txtLoanMount.clear();
        txtLoanTerm.clear();
        txtInterestRate.clear();
        txtFixedPaymentDay.clear();
        txtPaymentFrecuency.clear();

        txtTeacRate.clear();
        txtTreRate.clear();
        txtFeePayment.clear();
        txtLoanDestination.clear();
        dateLoanDate.clear();
        txtTotalPayment.clear();
        txtSavingBox.clear();

        txtLoanLine.clear();
        txtLineRate.clear();
        txtLineMount.clear();
        txtLineSpread.clear();
        txtLineTerm.clear();
        fieldClientStatus(true);
    }

    private void fieldClientStatus(boolean read) {
        dateLoanDate.setReadOnly(read);

        txtClientLoanId.setReadOnly(read);
        txtIdentifyCardDebtor.setReadOnly(read);
        txtDebtorName.setReadOnly(read);
//        txtCivilStatusDebtor.setReadOnly(read);
        txtGenderDebtor.setReadOnly(read);
//        txtAddressDebtor.setReadOnly(read);

        txtLoanNumber.setReadOnly(read);
        txtCurrency.setReadOnly(read);
        txtLoanMount.setReadOnly(read);
//        txtLoanTerm.setReadOnly(read);
        txtInterestRate.setReadOnly(read);
        txtFixedPaymentDay.setReadOnly(read);
        txtPaymentFrecuency.setReadOnly(read);

        txtLoanLine.setReadOnly(read);
        txtLineSpread.setReadOnly(read);
        txtLineRate.setReadOnly(read);
        txtLineMount.setReadOnly(read);
//        txtLineTerm.setReadOnly(read);

    }

    private GridLayout buildGridLayout(){
        gridLayout = new GridLayout();
        gridLayout.setColumns(10);
        gridLayout.setRows(9);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();

        txtLoanNumberSearch =new TextField("Numero credito:");
        txtLoanNumberSearch.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        txtLoanNumberSearch.setPlaceholder("Ingrese credito");

        gridLayout.addComponent(txtLoanNumberSearch,0,0);

        btnSearch =new Button("Buscar");
        btnSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSearch.setIcon(VaadinIcons.SEARCH);
        gridLayout.addComponent(btnSearch,1,0);
        gridLayout.setComponentAlignment(btnSearch,Alignment.BOTTOM_LEFT);

        btnImport = new Button("Guardar");
        btnImport.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnImport.setIcon(VaadinIcons.DATABASE);
        gridLayout.addComponent(btnImport,2,0);
        gridLayout.setComponentAlignment(btnImport,Alignment.BOTTOM_LEFT);

//        btnUpdate = new Button("Actualizar");
//        btnUpdate.setStyleName(ValoTheme.BUTTON_FRIENDLY);
//        btnUpdate.setIcon(VaadinIcons.DATABASE);
//        gridLayout.addComponent(btnUpdate,3,0);
//        gridLayout.setComponentAlignment(btnUpdate,Alignment.BOTTOM_LEFT);

        gridLayout.addComponent(buildClientPanel(),0,1,9,3);
        gridLayout.addComponent(buildLoanPanel(),0,4,9,6);
        gridLayout.addComponent(buildTabSupplementaryData(),0,7,9,8);


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

        txtLoanTerm = new TextField("Plazo (Meses):");
        txtLoanTerm.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanTerm.addStyleName("my_bg_style");
        txtLoanTerm.setReadOnly(false);;
        gridLayoutLoan.addComponent(txtLoanTerm,3,0);

        txtInterestRate = new TextField("Tasa inicial :");
        txtInterestRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtInterestRate.setReadOnly(true);;

        gridLayoutLoan.addComponent(txtInterestRate,4,0);

        txtCreditLifeInsurance = new TextField("Seguro desgra. (Mensual):");
        txtCreditLifeInsurance.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCreditLifeInsurance.addStyleName("my_bg_style");
        txtCreditLifeInsurance.setReadOnly(false);
        gridLayoutLoan.addComponent(txtCreditLifeInsurance,5,0);

        txtSpread = new TextField("Spread:");
        txtSpread.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtSpread.addStyleName("my_bg_style");
        txtSpread.setReadOnly(false);
        gridLayoutLoan.addComponent(txtSpread,6,0);

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

//        txtAgency = new TextField("Agencia:");
//        txtAgency.setStyleName(ValoTheme.TEXTFIELD_TINY);
//        txtAgency.addStyleName("my_bg_style");
//        txtAgency.setReadOnly(false);
//        gridLayoutLoan.addComponent(txtAgency,3,1);

        cmbAgency = new ComboBox<>("Agencia contrato:");
        cmbAgency.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbAgency.setEmptySelectionAllowed(false);
        cmbAgency.setRequiredIndicatorVisible(true);
        cmbAgency.addStyleName("my_bg_style");
        cmbAgency.setWidth("100%");
        gridLayoutLoan.addComponent(cmbAgency,3,1,4,1);

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
        txtCivilStatusDebtor.addStyleName("my_bg_style");
        txtCivilStatusDebtor.setReadOnly(false);
        txtCivilStatusDebtor.setVisible(false);
        gridLayoutClient.addComponent(txtCivilStatusDebtor,3,0);

        txtGenderDebtor = new TextField("Genero:");
        txtGenderDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtGenderDebtor.setReadOnly(true);
        txtGenderDebtor.setVisible(false);
        gridLayoutClient.addComponent(txtGenderDebtor,4,0);

        txtAddressDebtor = new TextField("Direccion:");
        txtAddressDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAddressDebtor.addStyleName("my_bg_style");
        txtAddressDebtor.setReadOnly(false);
        txtAddressDebtor.setVisible(false);
        gridLayoutClient.addComponent(txtAddressDebtor,5,0);

        panelClient.setContent(gridLayoutClient);

        return panelClient;
    }

    private TabSheet buildTabSupplementaryData(){
        tabSupplementaryData = new TabSheet();
        tabSupplementaryData.setStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSupplementaryData.addTab(buildPanelSupplentaryData(),"Datos complementarios");
        tabSupplementaryData.addTab(buildPanelCodDebtor(),"Codeudores");
        tabSupplementaryData.addTab(buildPanelWarranty(),"Garantias");
        tabSupplementaryData.addTab(buildPanelGuarantor(),"Garantes");
        tabSupplementaryData.addTab(buildPanelLineCredit(),"Linea de credito");
        return tabSupplementaryData;
    }


    private Panel buildPanelLineCredit(){
        panelLineCredit = new Panel();
        panelLineCredit.setStyleName(ValoTheme.PANEL_WELL);
        panelLineCredit.setWidth("100%");

        gridLayoutLineCredit = new GridLayout();
        gridLayoutLineCredit.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutLineCredit.setRows(2);
        gridLayoutLineCredit.setColumns(5);
        gridLayoutLineCredit.setSpacing(true);

        txtLoanLine = new TextField("Nro linea:");
        txtLoanLine.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanLine.setReadOnly(true);
        gridLayoutLineCredit.addComponent(txtLoanLine,0,0);

        txtLineMount = new TextField("Monto linea:");
        txtLineMount.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLineMount.setReadOnly(true);
        gridLayoutLineCredit.addComponent(txtLineMount,1,0);

        txtLineRate = new TextField("Tasa:");
        txtLineRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLineRate.setReadOnly(true);
        gridLayoutLineCredit.addComponent(txtLineRate,2,0);

        txtLineSpread = new TextField("Spread:");
        txtLineSpread.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLineSpread.setReadOnly(true);
        gridLayoutLineCredit.addComponent(txtLineSpread,3,0);

        txtLineTerm = new TextField("Plazo:");
        txtLineTerm.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLineTerm.setReadOnly(true);
        gridLayoutLineCredit.addComponent(txtLineTerm,4,0);

        panelLineCredit.setContent(gridLayoutLineCredit);

        return panelLineCredit;
    }

    private Panel buildPanelSupplentaryData(){
        panelSupplentaryData = new Panel();
        panelSupplentaryData.setStyleName(ValoTheme.PANEL_WELL);
        panelSupplentaryData.setWidth("100%");

        gridLayoutSupplementaryData = new GridLayout();
        gridLayoutSupplementaryData.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutSupplementaryData.setRows(4);
        gridLayoutSupplementaryData.setColumns(7);
        gridLayoutSupplementaryData.setSpacing(true);

        dateLoanDate = new DateField("Fecha registro:");
        dateLoanDate.setStyleName(ValoTheme.DATEFIELD_TINY);
        dateLoanDate.setDateFormat("dd-MM-yyyy");
        gridLayoutSupplementaryData.addComponent(dateLoanDate,0,0);

        txtTreRate = new TextField("Tasa Base:");
        txtTreRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtTreRate.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtTreRate,1,0);

        txtTeacRate = new TextField("Tasa TEAC:");
        txtTeacRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtTeacRate.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtTeacRate,2,0);

        txtFeePayment = new TextField("Monto cuota:");
        txtFeePayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtFeePayment.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtFeePayment,3,0);

        txtTotalPayment = new TextField("Total a cancelar:");
        txtTotalPayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtTotalPayment.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtTotalPayment,4,0);

        txtSavingBox = new TextField("Caja ahorro:");
        txtSavingBox.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtSavingBox.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtSavingBox,5,0);

        txtLoanDestination = new TextField("Destino del credito:");
        txtLoanDestination.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanDestination.setWidth("360px");
        txtLoanDestination.addStyleName("my_bg_style");
        gridLayoutSupplementaryData.addComponent(txtLoanDestination,0,1,3,1);

//        btnSaveSupplementaryData = new Button("Guardar");
//        btnSaveSupplementaryData.setStyleName(ValoTheme.BUTTON_PRIMARY);
//        btnSaveSupplementaryData.setIcon(VaadinIcons.SAFE);
//        gridLayoutSupplementaryData.addComponent(btnSaveSupplementaryData,0,2);

        panelSupplentaryData.setContent(gridLayoutSupplementaryData);

        return panelSupplentaryData;
    }

    private Panel buildPanelWarranty(){
        panelWarranty = new Panel();
        panelWarranty.setStyleName(ValoTheme.PANEL_WELL);
        panelWarranty.setWidth("100%");
        panelWarranty.setHeight("180px");

        gridWarranty = new Grid<>();
        gridWarranty.setSizeFull();
        gridWarranty.setStyleName(ValoTheme.TABLE_COMPACT);

        panelWarranty.setContent(gridWarranty);

        return panelWarranty;
    }

    private Panel buildPanelCodDebtor(){
        panelCoDebtor = new Panel();
        panelCoDebtor.setStyleName(ValoTheme.PANEL_WELL);
        panelCoDebtor.setWidth("100%");
        panelCoDebtor.setHeight("180px");

        gridCoDebtor = new Grid();
        gridCoDebtor.setStyleName(ValoTheme.TABLE_SMALL);
        gridCoDebtor.setSizeFull();
        gridCoDebtor.getEditor().setEnabled(true);
        gridCoDebtor.getEditor().setCancelCaption("Cancelar");
        gridCoDebtor.getEditor().setSaveCaption("Guardar");

        panelCoDebtor.setContent(gridCoDebtor);

        return panelCoDebtor;
    }

    private Panel buildPanelGuarantor(){
        panelGuarantor = new Panel();
        panelGuarantor.setStyleName(ValoTheme.PANEL_WELL);
        panelGuarantor.setWidth("100%");
        panelGuarantor.setHeight("180px");

        gridGuarantor = new Grid();
        gridGuarantor.setStyleName(ValoTheme.TABLE_SMALL);
        gridGuarantor.setSizeFull();
        gridGuarantor.getEditor().setEnabled(true);
        gridGuarantor.getEditor().setCancelCaption("Cancelar");
        gridGuarantor.getEditor().setSaveCaption("Guardar");


        panelGuarantor.setContent(gridGuarantor);
        return panelGuarantor;
    }

}
