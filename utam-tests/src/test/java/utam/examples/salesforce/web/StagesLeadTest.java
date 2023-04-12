package utam.examples.salesforce.web;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utam.action.pageobjects.ActionRenderer;
import utam.action.pageobjects.ActionsRibbon;
import utam.core.element.BasicElement;
import utam.global.pageobjects.impl.RecordHomeTemplateDesktopImpl;
import utam.helpers.pageobjects.Login;
import utam.tests.pageobjects.ItemsList;
import utam.tests.pageobjects.LeadsList;
import utam.tests.pageobjects.PageConvert;
import utam.utils.salesforce.TestEnvironment;

import java.util.ArrayList;
import java.util.List;


public class StagesLeadTest extends SalesforceWebTestBase {
    private final TestEnvironment testEnvironment = getTestEnvironment("sandbox");


    @BeforeTest
    public void setup() {
        setupChrome();
        final String baseUrl = testEnvironment.getBaseUrl();
        final String userName = testEnvironment.getUserName();
        log("Navigate to login URL: " + baseUrl);
        getDriver().get(baseUrl);
        Login loginPage = from(Login.class);
        loginPage.login(userName, testEnvironment.getPassword());

    }
    @Test
    public void convertLead() {
        int cantReg =2;
        List<String> leadsNoConvert = new ArrayList<>();
        ItemsList itemsList = loader.load(ItemsList.class);
       itemsList.waitForTitleYamaha();
       itemsList.getButton().click();
       itemsList.getRevealedNavMenuItem("Leads").click();
       itemsList.waitForTitleYamaha();
       itemsList.waitForRecords();

        LeadsList leadsList = from(LeadsList.class);

        List<BasicElement> rowCells; // campos de una fila
        List<String> nameLeads = new ArrayList<>();
        leadsList.waitForRecords();

        for (int i = 1; i < cantReg+1 ; i++) {
            rowCells = leadsList.getRowCells(i);//obtener los campos de una fila
            leadsList.getTableRows().get(i-1).moveTo();

            if (rowCells.get(2).getText().equals("Prospección")) {
                // agregar los links de los registros a una lista
                nameLeads.add(leadsList.getRowHeader(i).getAttribute("href"));
            }
        }

        for (String link : nameLeads) {

            getDriver().get(link);

            ActionsRibbon actionsMenu = loader.load(RecordHomeTemplateDesktopImpl.class).getHighlights().getActions();
            actionsMenu.getDropdownButton().clickButton();
            actionsMenu.getDropdownButton().getCustomMenuItemByTitle("Convert", ActionRenderer.class).getRibbonMenuItem().clickLinkItem();

            PageConvert pageConvert = loader.load(PageConvert.class);
            pageConvert.waitForCol4();
            pageConvert.waitForMatchPanelDetail();

            pageConvert.getButtonConvert().click();

            pageConvert.waitForMessageError();

            if(pageConvert.getMessageError().getText().equals("Validation error on Lead: Validar los campos \"Empresa\", \"Línea de negocio\", \"aceptación de uso de datos personales\" y \"Cita pactada para visita a tienda\" estén completos para convertir el Lead")) {
                leadsNoConvert.add(link);
            }


        }
        for (String p: leadsNoConvert) {
            //leads en estado de prospeccion inicialmente
            System.out.println(p);
        }

    }

    @AfterTest
    public final void tearDown() {
        //quitDriver();
    }
}

