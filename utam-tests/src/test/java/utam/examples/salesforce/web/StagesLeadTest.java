package utam.examples.salesforce.web;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utam.action.pageobjects.ActionRenderer;
import utam.action.pageobjects.ActionsRibbon;
import utam.core.element.BasicElement;
import utam.core.selenium.element.LocatorBy;
import utam.global.pageobjects.impl.RecordHomeTemplateDesktopImpl;
import utam.helpers.pageobjects.Login;
import utam.tests.pageobjects.*;
import utam.utils.salesforce.TestEnvironment;

import java.util.ArrayList;
import java.util.List;


public class StagesLeadTest extends SalesforceWebTestBase {
    private final TestEnvironment testEnvironment = getTestEnvironment("sandbox");


    @BeforeTest
    public void setup() {
        setupChrome();
        //getDriver().manage().window().maximize();
        final String baseUrl = testEnvironment.getBaseUrl();
        final String userName = testEnvironment.getUserName();
        log("Navigate to login URL: " + baseUrl);
        getDriver().get(baseUrl);
        Login loginPage = from(Login.class);
        loginPage.login(userName, testEnvironment.getPassword());

    }

    @Test(description = "Conversion de un lead: se verificara que los campos obligatorios esten completos," +
            "en caso de que falte completar un campo no se deberia poder convertir el lead, se verificara que se muestre el menasage de error correspondiente" +
            "('Empresa', 'Línea de negocio', 'aceptación de uso de datos personales' y 'Cita pactada para visita a tienda')" +
            "Se verificara el tamaño del numero de celular sea 12 o 9 segun corresponda y que se muestre el mensaje de error correspondiente" +
            "Se verificara si existe duplicdo y el mensaje de error correspondiente" +
            "PRECONDICIONES: Los registros  a probar deben estar en estado de prospeccion y ser de tipo de unidades de ventas"
    )

    public void convertLead() {
        int cantReg = 2;
        List<String> leadsNoConvert = new ArrayList<>();
        ItemsList itemsList = loader.load(ItemsList.class);
        itemsList.waitForTitleYamaha();
        itemsList.getButton().click();
        itemsList.getRevealedNavMenuItem("Candidatos").click();
        itemsList.waitForTitleYamaha();
        itemsList.waitForRecords();

        LeadsList leadsList = from(LeadsList.class);

        List<BasicElement> rowCells; // campos de una fila
        List<String> nameLeads = new ArrayList<>();
        leadsList.waitForRecords();

        for (int i = 1; i < cantReg + 1; i++) {
            leadsList.waitForRecordsSpan(i);
            rowCells = leadsList.getRowCells(i);//obtener los campos de una fila
            leadsList.getTableRows().get(i - 1).moveTo();

            if (rowCells.get(2).getText().equals("Prospección")) {
                // agregar los links de los registros a una lista
                nameLeads.add(leadsList.getRowHeader(i).getAttribute("href"));
            }
        }

        for (String link : nameLeads) {
            String nombreEmpresa = "";
            String textLineaDeNegocio = "";
            String checked = null;
            String textVisita = "";
            String textEmpresa = "";
            String phone = "";
            getDriver().get(link);
            OneRecordHomeFlexipage2Test oneRecordHomeFlexipage2 = loader.load(OneRecordHomeFlexipage2Test.class);
            Tab2Test tab2Test = oneRecordHomeFlexipage2.getDecorator().getTemplateDesktop2().getComponent2("flexipage_tabset").getTabset2().getTabset().getTab2();


            for (int i = 0; i < 8; i++) {
                tab2Test.getComponent2Test().get(i).moveTo();
                if (tab2Test.getComponent2Test().get(i).getAttribute("data-component-id").equals("flexipage_fieldSection")) {
                    nombreEmpresa = tab2Test.getComponent2("flexipage_fieldSection").getFlexipageFieldFormattedText().get(0).getText();

                }

                if (tab2Test.getComponent2Test().get(i).getAttribute("data-component-id").equals("flexipage_fieldSection2")) {
                    textLineaDeNegocio = tab2Test.getComponent2("flexipage_fieldSection2").getFlexipageFieldFormattedText().get(9).getText();
                    phone = tab2Test.getComponent2("flexipage_fieldSection2").getFlexipageFieldFormattedText().get(3).getTextPhone();

                    if (tab2Test.getComponent2("flexipage_fieldSection2").getFlexipageFieldFormattedText().get(12).containsElement(LocatorBy.byCss("lightning-input"))) {
                        if (tab2Test.getComponent2("flexipage_fieldSection2").getFlexipageFieldLightningInput().get(12).getCheckedState() == "true") {
                            checked = "Checked";
                        }
                    }
                }

                if (tab2Test.getComponent2Test().get(i).getAttribute("data-component-id").equals("flexipage_fieldSection4")) {
                    textEmpresa = tab2Test.getComponent2("flexipage_fieldSection4").getFlexipageFieldFormattedText().get(0).getText();
                }

                if (tab2Test.getComponent2Test().get(i).getAttribute("data-component-id").equals("flexipage_fieldSection5")) {
                    textVisita = tab2Test.getComponent2("flexipage_fieldSection5").getFlexipageFieldFormattedText().get(0).getText();
                }

            }
            //convertir

            ActionsRibbon actionsMenu = loader.load(RecordHomeTemplateDesktopImpl.class).getHighlights().getActions();
            actionsMenu.getDropdownButton().clickButton();
            actionsMenu.getDropdownButton().getCustomMenuItemByTitle("Convertir", ActionRenderer.class).getRibbonMenuItem().clickLinkItem();

            PageConvert pageConvert = loader.load(PageConvert.class);
            pageConvert.waitForCol4();
            pageConvert.waitForMatchPanelDetail();
            List<PageConvert.SectionElement> sectionElementList = pageConvert.getSection();
            for (PageConvert.SectionElement p : sectionElementList) {
                p.moveTo();
            }

            pageConvert.waitTextPropietarioDeRegistro();
            pageConvert.getButtonConvert().click();

            for (int i = pageConvert.getSectionMove().size() - 1; i >= 0; i--) {
                pageConvert.getSectionMove().get(i).scrollToCenter();
            }

            pageConvert.waitForMessageError();
            if (pageConvert.containsElement(LocatorBy.byCss(".errorToast"))) {
                System.out.println("Se muestra un mensaje de error, al querer convertir un lead");
                leadsNoConvert.add(link);
                String error = "false";
                Boolean existeDuplicado = false;
                List<BasicElement> fieldSection = pageConvert.getDuplicados();

                for (BasicElement p : fieldSection) {
                    if (p.containsElement(LocatorBy.byCss(".list.runtime_sales_leadDupePanelCardStencil.forceRecordLayout"))) {
                        existeDuplicado = true;
                    }
                }
                if (nombreEmpresa.equals("") && phone.length() != 12) {
                    System.out.println("mensaje de error: " + pageConvert.getMessageError().getText());
                    Assert.assertEquals(pageConvert.getMessageError().getText(), "Error de validación en Cuenta: El celular debe contener 12 dígitos numéricos");
                } else if (!nombreEmpresa.equals("") && phone.length() != 9) {
                    System.out.println("mensaje de error: " + pageConvert.getMessageError().getText());
                    Assert.assertEquals(pageConvert.getMessageError().getText(), "Error de validación en Contacto: El celular debe contener 9 dígitos numéricos");
                } else if (existeDuplicado) {
                    System.out.println("mensaje de error: " + pageConvert.getMessageError().getText());
                    Assert.assertEquals(pageConvert.getMessageError().getText(), "El registro que está a punto de crear parece un duplicado. Considere seleccionar registros existentes.");
                } else if (textEmpresa.equals("") || textLineaDeNegocio.equals("") || textVisita.equals("") || checked == null) {
                    System.out.println("mensaje de error: " + pageConvert.getMessageError().getText());
                    Assert.assertEquals(pageConvert.getMessageError().getText(), "Error de validación en Candidato: Validar los campos \"Empresa\", \"Línea de negocio\", \"aceptación de uso de datos personales\" y \"Cita pactada para visita a tienda\" estén completos para convertir el Lead");
                } else {
                    System.out.println("Todos los campos obligatorios estan completos, revisar que campo esta arrojando error");
                    Assert.assertEquals(error, "true");
                }
            } else {
                System.out.println("no se muestra ningun mensaje de error, verificar.");
            }


        }
        for (String p : leadsNoConvert) {
            //leads en estado de prospeccion inicialmente
            System.out.println("Lead no convertido: " + p);
        }

    }

    @AfterTest
    public final void tearDown() {
        //quitDriver();
    }
}

