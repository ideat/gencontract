package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.MyUI;
import mindware.com.model.BranchUser;
import mindware.com.model.Contract;
import mindware.com.model.ListContractLoan;
import mindware.com.service.BranchUserService;
import mindware.com.service.ContractService;
import mindware.com.utilities.Util;
import org.apache.commons.io.FilenameUtils;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListContractsForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Grid<ListContractLoan> gridListContract;
    private Panel panelListContract;
    private Button btnListContract;
    private GridCellFilter<Contract> filter;
    private String fileContract;
    private Upload uploadContract;
    private String fileNameContract="";


    public ListContractsForm(){
        Integer rolViewContract = ((MyUI) UI.getCurrent()).rolViewContractId2;
        setCompositionRoot(buildGridMainLayout());

        postBuild(rolViewContract);
        if (gridListContract.getHeaderRowCount()>1)
            gridListContract.removeAllColumns();

    }

    private void postBuild(Integer rolViewContract){
        btnListContract.addClickListener(clickEvent -> {
            loadGridContract(rolViewContract);
            fillGridListContract(gridListContract);

        });

        gridListContract.addItemClickListener( itemClick -> {
            fileContract = itemClick.getItem().getFileNameContract();
            File file = new File(fileContract);
            fileNameContract = file.getName();
            if (!file.exists()){
                Notification.show("Contrato",
                        "No existe el contrato generado",
                        Notification.Type.ERROR_MESSAGE);
//               uploadContract.setEnabled(false);
                uploadContract.setEnabled(true);
            }else {
                uploadContract.setEnabled(true);
            }

        });

    }


    private void loadGridContract(Integer rolViewContract) {


        gridListContract.setItems(getListContract(rolViewContract));

        if(!VaadinSession.getCurrent().getAttribute("rol").toString().equals("user")) {
            gridListContract.addComponentColumn(listContract -> {
                Link link = null;

                File file = new File(listContract.getFileNameContract());
                if (file.exists()) {
                    FileResource resource = new FileResource(new File(listContract.getFileNameContract()));
                    link = new Link("DOCX", resource);

                } else {
//                Notification.show("Contrato",
//                            "No existe el contrato generado",
//                            Notification.Type.ERROR_MESSAGE);
                }

                return link;
            }).setWidth(80);
        }
        gridListContract.addComponentColumn(listContract -> {
            Link link = null;

            File file = new File(listContract.getFileNameContract().replace(".docx",".pdf"));
            if (file.exists()) {
                FileResource resource = new FileResource(new File(listContract.getFileNameContract().replace(".docx",".pdf")));
                link = new Link("PDF",resource);
                link.setTargetName("_blank");

            }else {
//                Notification.show("Contrato",
//                            "No existe el contrato generado",
//                            Notification.Type.ERROR_MESSAGE);
            }

            return link;
        }).setWidth(80);


//        gridListContract.addComponentColumn(listContract ->{
//            Button button = new Button();
//            button.setIcon(VaadinIcons.DOWNLOAD);
//            button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
//            button.addClickListener(clickEvent -> {
//
//                File file = new File(listContract.getFileNameContract());
//                if (file.exists()) {
//
//                    final FileResource res = new FileResource(new File(listContract.getFileNameContract()));
//                    res.setCacheTime(0);
//                    FileDownloader fd = new FileDownloader(res) {
//                        @Override
//                        public boolean handleConnectorRequest(VaadinRequest request,
//                                                              VaadinResponse response, String path) throws IOException {
//
//                            boolean result = super.handleConnectorRequest(request, response, path);
//
//                            return result;
//                        }
//                    };
//
//                    fd.extend(button);
//                }else {
//                    Notification.show("Contrato",
//                            "No existe el contrato generado",
//                            Notification.Type.ERROR_MESSAGE);
//                }
//            });
//
//            return button;
//
//        });
    }


    private void fillGridListContract(final Grid grid){
        if(grid.getHeaderRowCount()>1)
            grid.removeHeaderRow(1);

        grid.getColumn("description").setHidden(true);
        grid.getColumn("fileNameContract").setHidden(true);

        grid.setColumnOrder("contractId","loanNumber","debtorName","dateContract", "currency","loanMount","description");
        grid.getColumn("loanNumber").setCaption("Nro prestamo").setWidth(170);
        grid.getColumn("contractId").setCaption("Id").setWidth(50);

        grid.getColumn("dateContract").setCaption("Fecha")
                .setRenderer(new DateRenderer(("%1$td-%1$tm-%1$tY"))).setWidth(180);
        grid.getColumn("loanMount").setCaption("Monto").setWidth(120);
        grid.getColumn("debtorName").setCaption("Deudor").setWidth(300);
        grid.getColumn("currency").setCaption("Moneda").setWidth(90);


        this.filter = new GridCellFilter(grid);

        this.filter.setDateFilter("dateContract",  new SimpleDateFormat("dd-MMM-yyyy"),true);
        this.filter.setComboBoxFilter("currency", String.class , Arrays.asList("BS", "$US"));
        this.filter.setTextFilter("debtorName", true,false);
        this.filter.setNumberFilter("loanNumber",Integer.class,"Prestamo invalido","","");
        this.filter.setNumberFilter("loanMount", Double.class, "Monto invalido", "desde", "hasta");

    }

    private List<ListContractLoan> getListContract(Integer rolViewContract){
        ContractService contractService = new ContractService();
        List<Integer> branchOfficeId = getBranchOffice(rolViewContract);

        List<Contract> contractList = new ArrayList<>();
        if(!branchOfficeId.isEmpty())
             contractList = contractService.findAllContractByBranch(branchOfficeId);

        List<ListContractLoan> listContractLoans = new ArrayList<>();
        Util util = new Util();
        for (Contract contract:contractList){
            ListContractLoan listContractLoan = new ListContractLoan();
            listContractLoan.setContractId(contract.getContractId());
            listContractLoan.setCurrency(contract.getLoanData().getCurrency());

            listContractLoan.setDateContract(contract.getDateContract());
            listContractLoan.setDebtorName(contract.getLoanData().getDebtorName());
            listContractLoan.setLoanMount(contract.getLoanData().getLoanMount());
            listContractLoan.setLoanNumber(contract.getLoanData().getLoanNumber());
            listContractLoan.setDescription(contract.getDescription());
            listContractLoan.setFileNameContract(contract.getFileNameContract());
            listContractLoans.add(listContractLoan);

        }

        return listContractLoans;
    }

    private List<Integer> getBranchOffice(Integer rolViewContract) {
        BranchUserService branchUserService = new BranchUserService();
        List<BranchUser> branchUserList = branchUserService.findBranchUserByRolViewerId(rolViewContract);
        List<Integer> branchOfficeId = new ArrayList<>();
        for(BranchUser branchUser: branchUserList){
            branchOfficeId.add(branchUser.getBranchOfficeId());
        }
        return branchOfficeId;
    }

    private GridLayout buildGridMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(7);
        gridMainLayout.setRows(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        uploadContract = new Upload(null, new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String fileName, String mimeType) {
                String extension = FilenameUtils.getExtension(fileName);
                if (extension.equals("doc") || extension.equals("docx") || extension.equals("odt")) {
                    try {
                        File fileContract = File.createTempFile(fileName, extension);
                        Path paths = Paths.get(System.getProperties().get("user.home").toString());
                        String path = paths.toString()+"/generated";
                        fileContract = new File(path, fileNameContract);

                        Notification.show("ACTUALIZACION",
                                "Contrato actualizado",
                                Notification.Type.HUMANIZED_MESSAGE);
                        return new FileOutputStream(fileContract);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Notification.show("ERROR",
                                e.getMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                }else{
                    Notification.show("ARCHIVO", "Extension incorrecta",Notification.Type.ERROR_MESSAGE);
                }
                return null;

            }
        });

        uploadContract.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(Upload.StartedEvent startedEvent) {
                if (startedEvent.getFilename().isEmpty())
                    Notification.show("ARCHIVO",
                            "Seleccione un archivo",
                            Notification.Type.WARNING_MESSAGE);
            }
        });

//        uploadContract.addFailedListener(new Upload.FailedListener() {
//            @Override
//            public void uploadFailed(Upload.FailedEvent failedEvent) {
//
//                Notification.show("ERROR",
//                        "Error al cargar el archivo",
//                        Notification.Type.ERROR_MESSAGE);
//
//
//            }
//        });

        uploadContract.setButtonCaption("Cargar contrato");
        uploadContract.setWidth("100%");
        uploadContract.setImmediateMode(false);
        gridMainLayout.addComponent(uploadContract,0,0);
        gridMainLayout.setComponentAlignment(uploadContract,Alignment.BOTTOM_RIGHT);

        btnListContract = new Button("Listar contratos");
        btnListContract.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnListContract.setIcon(VaadinIcons.LIST);
        gridMainLayout.addComponent(btnListContract,0,1);

        panelListContract = new Panel("<font size=3 color=#163759> Contratos generados <font>");
        panelListContract.setCaptionAsHtml(true);
        panelListContract.setStyleName(ValoTheme.PANEL_WELL);
        panelListContract.setSizeFull();

        gridListContract = new Grid<>(ListContractLoan.class);
        gridListContract.setStyleName(ValoTheme.TABLE_SMALL);
        gridListContract.setWidth("100%");
        panelListContract.setContent(gridListContract);


        gridMainLayout.addComponent(panelListContract,0,2,6,4);

        return gridMainLayout;
    }


}
