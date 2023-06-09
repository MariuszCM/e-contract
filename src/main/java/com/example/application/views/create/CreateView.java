package com.example.application.views.create;

import com.example.application.data.entity.Contract;
import com.example.application.data.service.ContractService;
import com.example.application.data.service.NipApiService;
import com.example.application.data.service.SamplePersonService;
import com.example.application.regonApi.model.CompanyIntegration;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Nowy dokument")
@Route(value = "create", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class CreateView extends Div {
    private TextField contractNumber = new TextField("Number umowy/aneksu");
    private TextField connectedAgreement = new TextField("Numer powiązanej umowy");
    private Checkbox annex = new Checkbox("Aneks");
    private TextField contractType = new TextField("Rodzaj umowy");
    private DatePicker signingDate = new DatePicker("Data podpisania");
    private TextField contractSigningPlace = new TextField("Miejsce zawarcia umowy");
    private DatePicker contractStartDate = new DatePicker("Termin rozpoczęcia obowiązywania umowy");
    private DatePicker contractEndDate = new DatePicker("Termin zakończenia obowiązywania umowy");
    private TextField contractSubject = new TextField("Przedmiot umowy");
    private NumberField totalValue = new NumberField("Całkowita wartość umowy");
    private TextField contractorNIP = new TextField();
    private TextField contractorName = new TextField("Kontrahent");
    private TextField contractorAddress = new TextField("Siedziba/adres kontrahenta");
    private ComboBox<String> sendToMF = new ComboBox<>("Czy umowa/aneks przesyłana do rejestru MF");
    private ComboBox<String> status = new ComboBox<>("Status");
    private HorizontalLayout horizontalLayoutNip = new HorizontalLayout();
    private final NipApiService regonApiPromptService;
    private Contract contract;
    private ContractService contractService;

    private Button cancel = new Button("Anuluj");
    private Button save = new Button("Zapisz");
    private Button search = new Button("Szukaj");

    private Binder<Contract> binder = new Binder<>(Contract.class);

    public CreateView(ContractService contractService, NipApiService regonApiPromptService) {
        this.regonApiPromptService = regonApiPromptService;
        this.contractService = contractService;
        this.contract = new Contract();
        addClassName("create-view");

        add(createTitle());
        contractorNIP.setPlaceholder("NIP kontrahenta");
        horizontalLayoutNip.add(contractorNIP, search);

        add(createFormLayout());
        add(createButtonLayout());
        contractorNIP.setAllowedCharPattern("[0-9]*");
        contractorNIP.setMaxLength(10);
        contractorNIP.setMinLength(10);
        sendToMF.setItems("Tak", "Nie");
        status.setItems("Podpisane");
        connectedAgreement.setVisible(false);

        binder.bindInstanceFields(this);
        clearForm();
        addListener();

    }

    private void clearForm() {
        binder.setBean(new Contract());
    }

    private Component createTitle() {
        return new H3("Wprowadzanie nowej umowy/aneksu");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(contractNumber, annex, connectedAgreement, status, contractType, signingDate, contractSigningPlace, contractStartDate,
                contractEndDate, contractSubject, totalValue, horizontalLayoutNip, contractorName, contractorAddress, sendToMF);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void addListener() {
        cancel.addClickListener(e -> clearForm());
        annex.addClickListener(e -> connectedAgreement.setVisible(annex.getValue()));
        save.addClickListener(e -> {
            boolean result = binder.writeBeanIfValid(contract);
            if (result) {
                contractService.save(contract);
                contract = new Contract();
                Notification.show("Dane umowy zostały zapisane");
                clearForm();
            }
        });
        search.addClickListener(e -> {
            if (contractorNIP.getValue() != "") {
                CompanyIntegration companyFromApi = regonApiPromptService.getFullCompanyReport(contractorNIP.getValue());
                contractorName.setValue(companyFromApi.getCompanyName());
                if (companyFromApi.getStreet() != null && companyFromApi.getCity() != null) {
                    contractorAddress.setValue(companyFromApi.getStreet() + " " + companyFromApi.getCity());
                }
            }
        });
    }

}
