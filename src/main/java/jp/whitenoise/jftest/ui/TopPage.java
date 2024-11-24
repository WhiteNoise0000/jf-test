package jp.whitenoise.jftest.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.jftest.model.入港予定明細;
import jp.whitenoise.jftest.service.TopService;

@Route(value = "", layout = MainLayout.class)
@PageTitle("出荷予定集計 - " + JfTestApplication.APP_NAME)
@AnonymousAllowed
public class TopPage extends VerticalLayout {

    public TopPage(TopService service) {
        setSizeFull();
        add(new H4("出荷予定集計（出荷予定日および魚種別）"));

        Grid<入港予定明細> grid = new Grid<>();
        grid.setEmptyStateText("出荷予定が未登録です。");
        grid.setItems(service.summary出荷予定());
        grid.addColumn(入港予定明細::get出荷予定日).setHeader("出荷予定日");
        grid.addColumn(入港予定明細::get魚種).setHeader("魚種");
        grid.addColumn(入港予定明細::get数量).setHeader("数量");
        grid.setSizeFull();
        add(grid);
    }
}
