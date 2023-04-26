package com.example.application.views.find;

import com.example.application.data.entity.Contract;
import com.example.application.data.service.ContractService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Find")
@Route(value = "find", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class FindView extends Div {

    private final ContractService contractService;
    private Grid<Contract> grid;
    private Filters filters;

    public FindView(ContractService contractService) {
        this.contractService = contractService;
        setSizeFull();
        addClassNames("find-view");

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
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

    private Component createGrid() {
        grid = new Grid<>(Contract.class, false);
        grid.addColumn("contractNumber").setAutoWidth(true);
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

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    public static class Filters extends Div implements Specification<Contract> {

        private final TextField contractNumber = new TextField("Numer umowy");
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
                contractorNIP.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Szukaj");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(contractNumber, contractorNIP, actions);
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
