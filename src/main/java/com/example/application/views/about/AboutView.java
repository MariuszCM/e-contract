package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.BrowserFrame;
import jakarta.annotation.security.PermitAll;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class AboutView extends HorizontalLayout {

    private BrowserFrame sample;

    public AboutView() {
        IFrame iframe = new IFrame("https://www.gov.pl/web/finanse/wiadomosci");
        iframe.setWidth("100%");
        iframe.setHeight("100%");

        setSizeFull();
        add(iframe);
    }

}
