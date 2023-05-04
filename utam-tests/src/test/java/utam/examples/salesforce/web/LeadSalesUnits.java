package utam.examples.salesforce.web;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utam.core.selenium.element.LocatorBy;
import utam.helpers.pageobjects.Login;
import utam.lightning.pageobjects.Combobox;
import utam.navex.pageobjects.WorkspaceManager;
import utam.tests.pageobjects.*;
import utam.utils.salesforce.TestEnvironment;

public class LeadSalesUnits extends SalesforceWebTestBase {
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

    @Test(description = "Creacion de un Lead: Verificar que aparesca el mensaje de error cuando el campo sea requerido" +
            "Pasos: Crear un lead de unidades de ventas sin completar ningun campo, para verificar los menajes de validacion")
    public void validationOfRequiredFields() {
        ItemsList itemsList = loader.load(ItemsList.class);
        itemsList.waitForTitleYamaha();
        itemsList.getButton().click();
        itemsList.getRevealedNavMenuItem("Candidatos").click();
        itemsList.waitForTitleYamaha();
        itemsList.waitForRecords();

        WorkspaceManager workspaceManager = loader.load(WorkspaceManager.class);
        workspaceManager.getTabset().getSplitViewWrapper().getConsoleObjectHome().getListView().getHeader().waitForAction("Nuevo");

        workspaceManager
                .getTabset()
                .getSplitViewWrapper()
                .getConsoleObjectHome()
                .getListView()
                .getHeader()
                .getActions()
                .getActionLink("Nuevo")
                .click();


        //seleccion de venta de unidades
        RecordActionW recordActionW = loader.load(RecordActionW.class);
        recordActionW.getSelect(1).click();
        recordActionW.getButtonNext().click();

        RecordActionWrapperTest copiaRecordActionWrapper = loader.load(RecordActionWrapperTest.class);
        copiaRecordActionWrapper.getForceFormFooter().getActionsRibbon().getActionRendererWithTitle("Guardar").clickButton();
        loader.load(ModalMessageError.class).getButtonCloseModal().click();

        Assert.assertEquals(copiaRecordActionWrapper.getDesktopRecordPageDecorator().getTitle().getText(),"Crear Candidato: Venta de Unidades");

        NewLead newLead = copiaRecordActionWrapper.getRecordHomeSingleColNoHeaderTemplateDesktop2Test();
        for (int i = 0; i < 35; i++) {

            //newLead.getFlexipageFieldMove().get(i).moveTo();

            //input con el componente record-layout-base-input ej num de documento, i=2
            if (newLead.getFlexipageField().get(i).containsElement(LocatorBy.byCss("records-record-layout-base-input"))) {
                InputTest input = newLead.getFlexipageField()
                        .get(i)
                        .getRecordlayoutBaseInput()
                        .getInput();
                if (input.isRequired()) {
                    System.out.println(input.getLabelText() + ", Mensaje de error: " + input.getErrorText());
                    Assert.assertEquals(input.getErrorText(), "Cumplimente este campo.");

                }
            }

            //input picklist
            if (newLead.getRecordPicklistTest().get(i).containsElement(LocatorBy.byCss("records-record-picklist"))) {
                Combobox combobox = newLead.getRecordPicklistTest()
                        .get(i)
                        .getRecordPicklist()
                        .getFormPicklist()
                        .getPicklist()
                        .getComboBox();
                if (combobox.isRequired() && combobox.getBase().getTriggerText().equals("--Ninguno--")) {
                    System.out.println(combobox.getLabelText() + ", Mensaje de error: " + combobox.getErrorMessage());
                    Assert.assertEquals(combobox.getErrorMessage(), "Cumplimente este campo.");
                }

            }

            //input sin el componente record-layout-base-input
            if (newLead.getFlexipageFieldInput().get(i).containsElement(LocatorBy.byCss("lightning-input"))) {
                InputTest input = newLead.getFlexipageFieldInput()
                        .get(i)
                        .getLightningInput();
                if (input.isRequired()) {
                    System.out.println(input.getLabelText() + ", Mensaje de error: " + input.getErrorText());
                    Assert.assertEquals(input.getErrorText(), "Cumplimente este campo.");

                }

            }

            //input group name
            if (newLead.getFlexipageFieldInputName().get(i).containsElement(LocatorBy.byCss("records-record-layout-input-name"))) {
                InputNameTest inputName = newLead.getFlexipageFieldInputName().get(i).getRecordLayoutInputName().getInputName();

                if (inputName.getFirstNameInput().isRequired()) {
                    System.out.println(inputName.getFirstNameInput().getLabelText() + ", Mensaje de error: " + inputName.getFirstNameInput().getErrorText());
                    Assert.assertEquals(inputName.getFirstNameInput().getErrorText(), "Cumplimente este campo.");
                }

                if (inputName.getLastNameInput().isRequired()) {
                    System.out.println(inputName.getLastNameInput().getLabelText() + ", Mensaje de error: " + inputName.getLastNameInput().getErrorText());
                    Assert.assertEquals(inputName.getLastNameInput().getErrorText(), "Cumplimente este campo.");

                }

                if (inputName.getMiddleNameInput().isRequired()) {
                    System.out.println(inputName.getMiddleNameInput().getLabelText() + ", Mensaje de error: " + inputName.getMiddleNameInput().getErrorText());
                    Assert.assertEquals(inputName.getMiddleNameInput().getErrorText(), "Cumplimente este campo.");

                }

                Combobox combobox = inputName.getSalutationPicklist().getComboBox();
                if (combobox.isRequired() && combobox.getBase().getTriggerText().equals("--Ninguno--")) {
                    System.out.println(combobox.getLabelText() + ", Mensaje de error: " + combobox.getErrorMessage());
                    Assert.assertEquals(combobox.getErrorMessage(), "Cumplimente este campo.");

                }

            }
            //input group address

            if (newLead.getFlexipageFieldInputAddress().get(i).containsElement(LocatorBy.byCss("records-record-layout-input-address"))) {
                InputAddressTest inputAddress = newLead.getFlexipageFieldInputAddress().get(i).getRecordLayoutInputAddress().getInputAddress();

                if (inputAddress.getCityInput().isRequired()) {
                    System.out.println(inputAddress.getCityInput().getLabelText() + ", Mensaje de error: " + inputAddress.getCityInput().getErrorText());
                    Assert.assertEquals(inputAddress.getCityInput().getErrorText(), "Cumplimente este campo.");

                }

                if (inputAddress.getCountryInput().isRequired()) {
                    System.out.println(inputAddress.getCountryInput().getLabelText() + ", Mensaje de error: " + inputAddress.getCountryInput().getErrorText());
                    Assert.assertEquals(inputAddress.getCountryInput().getErrorText(), "Cumplimente este campo.");

                }

                if (inputAddress.getStateInput().isRequired()) {
                    System.out.println(inputAddress.getStateInput().getLabelText() + ", Mensaje de error: " + inputAddress.getStateInput().getErrorText());
                    Assert.assertEquals(inputAddress.getStateInput().getErrorText(), "Cumplimente este campo.");

                }

                if (inputAddress.getPostalCodeInput().isRequired()) {
                    System.out.println(inputAddress.getPostalCodeInput().getLabelText() + ", Mensaje de error: " + inputAddress.getPostalCodeInput().getErrorText());
                    Assert.assertEquals(inputAddress.getPostalCodeInput().getErrorText(), "Cumplimente este campo.");

                }
            }

            //input lookup
            if (newLead.getFlexipageFieldLayoutLookup().get(i).containsElement(LocatorBy.byCss("records-record-layout-lookup"))) {
                LookupDesktopTest lookupDesktop = newLead.getFlexipageFieldLayoutLookup().get(i).getRecordLayoutLookup().getLookup().getLookupDesktop();

                if (lookupDesktop.getGroupedCombobox().isRequired()) {
                    System.out.println(lookupDesktop.getGroupedCombobox().getLabelText() + ", Mensaje de error: " + lookupDesktop.getGroupedCombobox().getErrorText());
                    Assert.assertEquals(lookupDesktop.getGroupedCombobox().getErrorText(), "Cumplimente este campo.");

                }

            }

            //input checkbox
            if (newLead.getFlexipageFieldCheckbox().get(i).containsElement(LocatorBy.byCss("records-record-layout-checkbox"))) {
                InputTest input = newLead.getFlexipageFieldCheckbox().get(i).getRecordLayoutCheckbox().getInput();

                if (input.isRequired()) {
                    System.out.println(input.getLabelText() + ", Mensaje de error: " + input.getErrorText());
                    Assert.assertEquals(input.getErrorText(), "Cumplimente este campo.");
                }
            }
        }
    }

    @AfterTest
    public final void tearDown() {

        //quitDriver();
    }
}
