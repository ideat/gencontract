package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.MessageBox;
import mindware.com.model.Contract;
import mindware.com.model.LoanData;
import mindware.com.model.Parameter;
import mindware.com.service.ContractService;
import mindware.com.service.LoanDataService;
import mindware.com.service.ParameterService;
import mindware.com.utilities.Util;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateContractsForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private HorizontalLayout horizontalLayout;
    private Panel panelGridLoanData;
    private Panel panelGridTemplateContract;
    private Panel panelSearch;
    private Panel panelGenerateContract;
    private Grid<LoanData> gridLoanData;
    private Grid<Parameter> gridContractTemplate;

    private TextField txtTextSearch;
    private TextField txtLoanDataId;
    private TextField txtFileNameContract;
    private TextField txtDescription;
    private TextField txtContractId;
    private ComboBox cmbSearchBy;
    private DateField dateContractGenerate;
    private Button btnGenerateContract;
    private Button btnDownloadContract;
    private Button btnSearch;
    private Integer numberLoan;
    private String pathGenerate="";

    public GenerateContractsForm(){

        setCompositionRoot(buildMainGridLayout());
        fillGridTemplateContract();
        postBuild();
    }

    private void postBuild(){
        btnSearch.addClickListener(clickEvent -> {
            if (validateSearchCriteria()){
                fillGridLoanData(cmbSearchBy.getValue().toString(), txtTextSearch.getValue() );
            }else{
                Notification.show("Buscar",
                        "Sleccione e ingrese criterios de busqueda",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        btnGenerateContract.addClickListener(clickEvent -> {
            pathGenerate = "";
            if (validateData()){
                String path = this.getClass().getClassLoader().getResource("/contract/template").getPath() + txtFileNameContract.getValue();
                pathGenerate = this.getClass().getClassLoader().getResource("/contract/generated").getPath() + numberLoan.toString()+".docx";
                if (verifyExistLoanContract(pathGenerate)) {
                    MessageBox
                            .createQuestion()
                            .withCaption("Contrato")
                            .withMessage("Prestamo ya tiene contrato, desea reemplazarlo?")
                            .withYesButton(() -> {
                                createContract(path, pathGenerate);
                                Notification.show("Contrato",
                                        "Contrato creado",
                                        Notification.Type.HUMANIZED_MESSAGE);
                                insertUpdateContract(pathGenerate,"update");
                            })
                            .withNoButton(() -> {  })
                            .open();
                } else {
                    createContract(path, pathGenerate);
                    Notification.show("Contrato",
                            "Contrato creado",
                            Notification.Type.HUMANIZED_MESSAGE);
                    insertUpdateContract(pathGenerate,"insert");
                }
            }else {
                Notification.show("Contrato",
                        "Ingrese la fecha de elaboracion del contrato",
                        Notification.Type.ERROR_MESSAGE);
                dateContractGenerate.focus();
            }
        });

        gridLoanData.addItemClickListener(itemClick -> {
            txtLoanDataId.setValue(itemClick.getItem().getLoanDataId().toString());
            numberLoan = itemClick.getItem().getLoanNumber();
        });

        gridContractTemplate.addItemClickListener(itemClick -> {
           txtFileNameContract.setValue(itemClick.getItem().getValueParameter());
        });

        btnDownloadContract.addClickListener(clickEvent -> {
            if (!pathGenerate.equals("") ) {
                downloadContract(pathGenerate);
            } else {
                Notification.show("ERROR",
                        "No se genero un contrato, genere uno y pruebe nuevamente",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private void downloadContract(String file){

        final Resource res = new FileResource(new File(file));
        FileDownloader fd = new FileDownloader(res);
        fd.extend(btnDownloadContract);

    }

    private void createContract(String path, String pathGenerate) {
        OutputStream contract = null;
        try {
            contract = new FileOutputStream(pathGenerate);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            replace(path, getFieldValuesLoanData(numberLoan), contract);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertUpdateContract(String nameContract, String type){
        Contract contract = new Contract();
        Util util = new Util();
        contract.setDateContract(util.stringToDate(dateContractGenerate.getValue().toString(),"yyyy-MM-dd"));
        contract.setFileNameContract(nameContract);
        contract.setLoanDataId(Integer.parseInt(txtLoanDataId.getValue()));
        contract.setDescription(txtDescription.getValue());

        ContractService contractService = new ContractService();
        if (type.equals("insert")) {
            contractService.insertContract(contract);
            txtContractId.setValue(contract.getContractId().toString());
        }
        else {
            contract.setContractId(Integer.parseInt(txtContractId.getValue()));
            contractService.updateContract(contract);
        }
    }

    private boolean verifyExistLoanContract(String fileContract){

        return new File(fileContract).exists();
    }

    private void replace(String inFile, Map<String, String> data, OutputStream out) throws Exception, IOException {
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(inFile));
        for (XWPFParagraph p : doc.getParagraphs()) {
            replace2(p, data);
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replace2(p, data);
                    }
                }
            }
        }


        doc.write(out);
    }

    private void replace2(XWPFParagraph p, Map<String, String> data) {
        String pText = p.getText(); // complete paragraph as string
        if (pText.contains("${")) { // if paragraph does not include our pattern, ignore
            TreeMap<Integer, XWPFRun> posRuns = getPosToRuns(p);
            Pattern pat = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher m = pat.matcher(pText);
            while (m.find()) { // for all patterns in the paragraph
                String g = m.group(1);  // extract key start and end pos
                int s = m.start(1);
                int e = m.end(1);
                String key = g;
                String x = data.get(key).trim();
                if (x == null)
                    x = "";
                SortedMap<Integer, XWPFRun> range = posRuns.subMap(s - 2, true, e + 1, true); // get runs which contain the pattern
                boolean found1 = false; // found $
                boolean found2 = false; // found {
                boolean found3 = false; // found }
                XWPFRun prevRun = null; // previous run handled in the loop
                XWPFRun found2Run = null; // run in which { was found
                int found2Pos = -1; // pos of { within above run
                for (XWPFRun r : range.values())
                {
                    if (r == prevRun)
                        continue; // this run has already been handled
                    if (found3)
                        break; // done working on current key pattern
                    prevRun = r;
                    for (int k = 0;; k++) { // iterate over texts of run r
                        if (found3)
                            break;
                        String txt = null;
                        try {
                            txt = r.getText(k); // note: should return null, but throws exception if the text does not exist
                        } catch (Exception ex) {

                        }
                        if (txt == null)
                            break; // no more texts in the run, exit loop
                        if (txt.contains("$") && !found1) {  // found $, replace it with value from data map
                            txt = txt.replaceFirst("\\$", x);
                            found1 = true;
                        }
                        if (txt.contains("{") && !found2 && found1) {
                            found2Run = r; // found { replace it with empty string and remember location
                            found2Pos = txt.indexOf('{');
                            txt = txt.replaceFirst("\\{", "");
                            found2 = true;
                        }
                        if (found1 && found2 && !found3) { // find } and set all chars between { and } to blank
                            if (txt.contains("}"))
                            {
                                if (r == found2Run)
                                { // complete pattern was within a single run
                                    txt = txt.substring(0, found2Pos)+txt.substring(txt.indexOf('}'));
                                }
                                else // pattern spread across multiple runs
                                    txt = txt.substring(txt.indexOf('}'));
                            }
                            else if (r == found2Run) // same run as { but no }, remove all text starting at {
                                txt = txt.substring(0,  found2Pos);
                            else
                                txt = ""; // run between { and }, set text to blank
                        }
                        if (txt.contains("}") && !found3) {
                            txt = txt.replaceFirst("\\}", "");
                            found3 = true;
                        }
                        r.setText(txt, k);
                    }
                }
            }
            System.out.println(p.getText());

        }

    }

    private TreeMap<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph) {
        int pos = 0;
        TreeMap<Integer, XWPFRun> map = new TreeMap<Integer, XWPFRun>();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.text();
            if (runText != null && runText.length() > 0) {
                for (int i = 0; i < runText.length(); i++) {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }

        }
        return map;
    }


    private Map<String, String> getFieldValuesLoanData(Integer loanNumber){
        Map<String,String> stringMapVariables = new HashMap<>();
        ParameterService parameterService = new ParameterService();
        List<Parameter> listVariableContract = parameterService.findParameterByType("variable");
        LoanData loanData = new LoanData();
        LoanDataService loanDataService = new LoanDataService();
        loanData = loanDataService.findLoanDataByLoanNumber(loanNumber);

        for(Parameter parameter:listVariableContract){
            if (parameter.getValueParameter().equals("loanNumber"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanNumber().toString());
            if (parameter.getValueParameter().equals("loanDate"))
                stringMapVariables.put(parameter.getValueParameter(),dateContractGenerate.getValue().toString());
            if(parameter.getValueParameter().equals("currency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCurrency());
            if(parameter.getValueParameter().equals("loanMount"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanMount().toString());
            if(parameter.getValueParameter().equals("loanTerm"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanTerm().toString());
            if(parameter.getValueParameter().equals("interestRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getInterestRate().toString());
            if(parameter.getValueParameter().equals("treRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTreRate().toString());
            if(parameter.getValueParameter().equals("teacRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTeacRate().toString());
            if(parameter.getValueParameter().equals("feePayment"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getFeePayment().toString());
            if(parameter.getValueParameter().equals("paymentFrecuency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getPaymentFrecuency());
            if(parameter.getValueParameter().equals("creditLifeInsurance"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCreditLifeInsurance().toString());
            if(parameter.getValueParameter().equals("totalPayment"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTotalPayment().toString());
            if(parameter.getValueParameter().equals("loanDestination"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanDestination());
            if(parameter.getValueParameter().equals("agency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getAgency());
            if(parameter.getValueParameter().equals("official"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getOfficial());
            if(parameter.getValueParameter().equals("debtorName"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getDebtorName());
            if(parameter.getValueParameter().equals("identityCardDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getIdentityCardDebtor());
            if(parameter.getValueParameter().equals("addressDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getAddressDebtor());
            if(parameter.getValueParameter().equals("civilStatusDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCivilStatusDebtor());
            if(parameter.getValueParameter().equals("genderDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getGenderDebtor());
            if(parameter.getValueParameter().equals("fixedPaymentDay"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getFixedPaymentDay().toString());
        }


        return stringMapVariables;
    }



    private boolean validateData(){
        if (dateContractGenerate.isEmpty()) return false;

        return true;
    }

    private boolean validateSearchCriteria(){
        if (cmbSearchBy.isEmpty()) return false;
        if (txtTextSearch.isEmpty()) return false;
        return true;
    }

    private void fillGridLoanData(String criteria, String textSearch){
        LoanDataService loanDataService = new LoanDataService();
        List<LoanData> loanDataList = new ArrayList<>();
        if (criteria.equals("Numero credito")){
            loanDataList.add(loanDataService.findLoanDataByLoanNumber(Integer.parseInt(textSearch)));
        } else if (criteria.equals("Nombre deudor")){
            loanDataList = loanDataService.findLoanDataByDebtorName('%'+textSearch+'%');
        }
        gridLoanData.removeAllColumns();
        gridLoanData.setItems(loanDataList);
        gridLoanData.addColumn(LoanData::getLoanDataId).setCaption("ID");
        gridLoanData.addColumn(LoanData::getLoanNumber).setCaption("Nro credito");
        gridLoanData.addColumn(LoanData::getDebtorName).setCaption("Deudor");

    }

    private void fillGridTemplateContract(){
        ParameterService parameterService = new ParameterService();
        gridContractTemplate.removeAllColumns();
        gridContractTemplate.setItems(parameterService.findParameterByType("contract"));
        gridContractTemplate.addColumn(Parameter::getParameterId).setCaption("ID");
        gridContractTemplate.addColumn(Parameter::getValueParameter).setCaption("Contrato");
        gridContractTemplate.addColumn(Parameter::getDescriptionParameter).setCaption("Descripcion");
    }


    private GridLayout buildMainGridLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(10);
        gridMainLayout.setRows(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        panelSearch = new Panel();
        panelSearch.setStyleName(ValoTheme.PANEL_WELL);
        panelSearch.setSizeFull();
        panelSearch.setContent(buildHorizontalLayout());
        gridMainLayout.addComponent(panelSearch,0,0,4,0);

        txtContractId = new TextField("Id:");
        txtContractId.setEnabled(false);
        txtContractId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridMainLayout.addComponent(txtContractId,9,0);
        gridMainLayout.setComponentAlignment(txtContractId,Alignment.BOTTOM_RIGHT);

        gridMainLayout.addComponent(buildPanelGridLoanData(),0,1,4,1);
        gridMainLayout.addComponent(buildPanelGridTemplateContract(),5,1,9,1);
        gridMainLayout.addComponent(buildGenerateContract(),0,2,9,2);

        return gridMainLayout;
    }

    private Panel buildPanelGridLoanData(){
        panelGridLoanData = new Panel();
        panelGridLoanData.setStyleName(ValoTheme.PANEL_WELL);
        panelGridLoanData.setWidth("100%");
        panelGridLoanData.setHeight("80%");

        gridLoanData = new Grid<>();
        gridLoanData.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLoanData.setWidth("100%");
        gridLoanData.setHeight("350px");
        panelGridLoanData.setContent(gridLoanData);

        return panelGridLoanData;
    }

    private Panel buildPanelGridTemplateContract(){
        panelGridTemplateContract = new Panel();
        panelGridTemplateContract.setStyleName(ValoTheme.PANEL_WELL);
        panelGridTemplateContract.setWidth("100%");
        panelGridTemplateContract.setHeight("80%");

        gridContractTemplate = new Grid<>();
        gridContractTemplate.setStyleName(ValoTheme.TABLE_COMPACT);
        gridContractTemplate.setWidth("100%");
        gridContractTemplate.setHeight("350px");
        panelGridTemplateContract.setContent(gridContractTemplate);

        return panelGridTemplateContract;
    }

    private Panel buildGenerateContract(){
        panelGenerateContract = new Panel();
        panelGenerateContract.setStyleName(ValoTheme.PANEL_WELL);
        panelGenerateContract.setWidth("100%");
        panelGenerateContract.setHeight("80%");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        txtLoanDataId = new TextField("ID credito:");
        txtLoanDataId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanDataId.setEnabled(false);
        horizontalLayout.addComponent(txtLoanDataId);

        txtFileNameContract = new TextField("Archivo contrato:");
        txtFileNameContract.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtFileNameContract.setEnabled(false);
        horizontalLayout.addComponent(txtFileNameContract);

        dateContractGenerate = new DateField("Fecha contrato:");
        dateContractGenerate.setStyleName(ValoTheme.DATEFIELD_TINY);
        dateContractGenerate.setDateFormat("yyyy-MM-dd");
        horizontalLayout.addComponent(dateContractGenerate);

        txtDescription = new TextField("Descripcion");
        txtDescription.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(txtDescription);

        btnGenerateContract = new Button("Generar");
        btnGenerateContract.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGenerateContract.setIcon(VaadinIcons.DATABASE);
        horizontalLayout.addComponent(btnGenerateContract);
        horizontalLayout.setComponentAlignment(btnGenerateContract,Alignment.BOTTOM_LEFT);

        btnDownloadContract = new Button("Descargar");
        btnDownloadContract.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnDownloadContract.setIcon(VaadinIcons.DOWNLOAD);
        horizontalLayout.addComponent(btnDownloadContract);
        horizontalLayout.setComponentAlignment(btnDownloadContract,Alignment.BOTTOM_LEFT);

        panelGenerateContract.setContent(horizontalLayout);

        return panelGenerateContract;
    }

    private HorizontalLayout buildHorizontalLayout(){
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        cmbSearchBy = new ComboBox("Buscar por:");
        cmbSearchBy.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbSearchBy.setEmptySelectionAllowed(false);
        cmbSearchBy.setItems("Numero credito","Nombre deudor");
        horizontalLayout.addComponent(cmbSearchBy);

        txtTextSearch = new TextField();
        txtTextSearch.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(txtTextSearch);
        horizontalLayout.setComponentAlignment(txtTextSearch,Alignment.BOTTOM_LEFT);

        btnSearch = new Button("Buscar");
        btnSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSearch.setIcon(VaadinIcons.SEARCH);
        horizontalLayout.addComponent(btnSearch);
        horizontalLayout.setComponentAlignment(btnSearch,Alignment.BOTTOM_LEFT);

        return horizontalLayout;
    }

}
