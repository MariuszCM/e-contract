package com.example.application.views.find;

import com.example.application.data.entity.Contract;
import com.example.application.data.service.ContractService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Find")
@Route(value = "find/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class FindView extends Div implements BeforeEnterObserver {

    private final BeanValidationBinder<Contract> binder;
    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "find/%s/edit";
    private final Button cancel = new Button("Anuluj");
    private final Button save = new Button("Zapisz");
    private final Button edit = new Button("Edytuj");
    private final ContractService contractService;
    private TextField contractNumber = new TextField("Number umowy");
    private TextField connectedAgreement = new TextField("Numer powiązanej umowy");
    private ComboBox<String> status = new ComboBox<>("Status");
    private TextField contractType = new TextField("Rodzaj umowy");
    private DatePicker signingDate = new DatePicker("Data podpisania");
    private TextField contractSigningPlace = new TextField("Miejsce zawarcia umowy");
    private DatePicker contractStartDate = new DatePicker("Termin rozpoczęcia obowiązywania umowy");
    private DatePicker contractEndDate = new DatePicker("Termin zakończenia obowiązywania umowy");
    private TextField contractSubject = new TextField("Przedmiot umowy");
    private NumberField totalValue = new NumberField("Całkowita wartość umowy");
    private TextField contractorNIP = new TextField("NIP kontrahenta");
    private TextField contractorName = new TextField("Kontrahent");
    private TextField contractorAddress = new TextField("Siedziba/adres kontrahenta");
    private ComboBox<String> sendToMF = new ComboBox<>("Czy umowa/aneks przesyłana do rejestru MF");
    private Contract contract;
    private Grid<Contract> grid = new Grid<>(Contract.class, false);
    private Filters filters;

    public FindView(ContractService contractService) {
        this.contractService = contractService;
        setSizeFull();
        addClassNames("find-view");
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        createEditorLayout(splitLayout);
        sendToMF.setItems("Tak", "Nie");
        status.setItems("Podpisane", "Odstąpione", "Wypowiedzone", "Rozwiązane za zgodą stron", "Zapłacone", "Wykonane");
        binder = new BeanValidationBinder<>(Contract.class);
        binder.bindInstanceFields(this);

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid(), splitLayout);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<Contract> contractFromBackend = contractService.get(samplePersonId.get());
            if (contractFromBackend.isPresent()) {
                populateForm(contractFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FindView.class);
            }
        }
    }

    private Component createGrid() {
//        grid = new Grid<>(Contract.class, false);
        grid.addColumn("contractNumber").setAutoWidth(true);
        grid.addColumn("connectedAgreement").setAutoWidth(true);
        grid.addColumn("contractorNIP").setAutoWidth(true);
        grid.addColumn("contractorName").setAutoWidth(true);
        grid.addColumn("contractSubject").setAutoWidth(true);
        grid.addColumn("totalValue").setAutoWidth(true);
        grid.addColumn("sendToMF").setAutoWidth(true);

        grid.setItems(query -> contractService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FindView.class);
            }
        });

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        formLayout.add(contractNumber, connectedAgreement, status, contractType, signingDate, contractSigningPlace, contractStartDate, contractEndDate, contractSubject, totalValue, contractorNIP, contractorName, contractorAddress, sendToMF);
        formLayout.setVisible(false);

        cancel.addClickListener(e -> {
            cancel.setVisible(false);
            save.setVisible(false);
            formLayout.setVisible(false);
            edit.setVisible(true);
            clearForm();
            refreshGrid();
        });
        edit.addClickListener(e -> {
            formLayout.setVisible(true);
            cancel.setVisible(true);
            edit.setVisible(false);
            save.setVisible(true);
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.contract == null) {
                    this.contract = new Contract();
                }
                binder.writeBean(this.contract);
                contractService.update(this.contract);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(FindView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
            cancel.setVisible(false);
            save.setVisible(false);
            formLayout.setVisible(false);
            edit.setVisible(true);
        });
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(edit, save, cancel);
        save.setVisible(false);
        cancel.setVisible(false);
        editorLayoutDiv.add(buttonLayout);
    }

    private void populateForm(Contract value) {
        this.contract = value;
        binder.readBean(this.contract);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void clearForm() {
        populateForm(null);
    }

    public static class Filters extends Div implements Specification<Contract> {

        private final TextField contractNumber = new TextField("Numer umowy");
        private final TextField connectedAgreement = new TextField("Numer powiązanej umowy");
        private final TextField contractorNIP = new TextField("NIP Kontrahenta");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);


            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                contractNumber.clear();
                connectedAgreement.clear();
                contractorNIP.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Szukaj");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(contractNumber, connectedAgreement, contractorNIP, actions);
        }

        @Override
        public Predicate toPredicate(Root<Contract> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!contractNumber.isEmpty()) {
                String lowerCaseFilter = contractNumber.getValue().toLowerCase();
                Predicate contractNumberMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contractNumber")), lowerCaseFilter + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contractNumber")), "%" + lowerCaseFilter + "%")
                );
                predicates.add(contractNumberMatch);
            }
            if (!connectedAgreement.isEmpty()) {
                String lowerCaseFilter = connectedAgreement.getValue().toLowerCase();
                Predicate connectedContractNumberMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("connectedAgreement")), lowerCaseFilter + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("connectedAgreement")), "%" + lowerCaseFilter + "%")
                );
                predicates.add(connectedContractNumberMatch);
            }
            if (!contractorNIP.isEmpty()) {
                String lowerCaseFilter = contractorNIP.getValue().toLowerCase();
                Predicate contractorNIPMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contractorNIP")), lowerCaseFilter + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contractorNIP")), "%" + lowerCaseFilter + "%")
                );
                predicates.add(contractorNIPMatch);
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

}
