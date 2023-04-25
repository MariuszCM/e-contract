package com.example.application.views.create;

import com.example.application.data.entity.Contract;
import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
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
    /**
     * Uwaga! Nie wszystkie pola sie tu znajduja
     */
    private TextField contractNumber = new TextField("Number umowy");
    private DatePicker signingDate = new DatePicker("Data podpisania");
    private TextField contractSubject = new TextField("Temat umowy");
    private NumberField totalValue = new NumberField("Całkowita kwota netto");
    private TextField contractorNIP = new TextField("Numer NIP");
    private TextField contractorName = new TextField("Nazwa przedsiębiorcy");
    private PhoneNumberField contractorNumber = new PhoneNumberField("Number kontaktowy do przedsiębiorcy");

    private Button cancel = new Button("Anuluj");
    private Button save = new Button("Zapisz");

    private Binder<Contract> binder = new Binder<>(Contract.class);

    public CreateView(SamplePersonService personService) {
        addClassName("create-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
        contractorNIP.setAllowedCharPattern("[0-9]*");
        contractorNIP.setMaxLength(10);
        contractorNIP.setMinLength(10);

        binder.bindInstanceFields(this);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            Notification.show("Dane umowy zostały zapisane");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new Contract());
    }

    private Component createTitle() {
        return new H3("Wprowadzanie nowej umowy");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(contractNumber, contractSubject, contractorNIP, contractorName, contractorNumber, signingDate, totalValue);
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

    private static class PhoneNumberField extends CustomField<String> {
        private ComboBox<String> countryCode = new ComboBox<>();
        private TextField number = new TextField();

        public PhoneNumberField(String label) {
            setLabel(label);
            countryCode.setWidth("120px");
            countryCode.setPlaceholder("Prefix");
            countryCode.setAllowedCharPattern("[\\+\\d]");
            countryCode.setItems("+48", "+49", "+33", "+44", "+39");
            countryCode.addCustomValueSetListener(e -> countryCode.setValue(e.getDetail()));
            number.setAllowedCharPattern("\\d");
            HorizontalLayout layout = new HorizontalLayout(countryCode, number);
            layout.setFlexGrow(1.0, number);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (countryCode.getValue() != null && number.getValue() != null) {
                String s = countryCode.getValue() + " " + number.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String phoneNumber) {
            String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                countryCode.clear();
                number.setValue(parts[0]);
            } else if (parts.length == 2) {
                countryCode.setValue(parts[0]);
                number.setValue(parts[1]);
            } else {
                countryCode.clear();
                number.clear();
            }
        }
    }

}
