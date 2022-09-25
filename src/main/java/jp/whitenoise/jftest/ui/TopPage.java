package jp.whitenoise.jftest.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
public class TopPage extends VerticalLayout {
    
    public TopPage() {

        VerticalLayout vl = new VerticalLayout();
        vl.add(new H2("漁獲予定量集計"));
        vl.add(new RouterLink("予定登録", AddPage.class));
        add(vl);
    }
}
