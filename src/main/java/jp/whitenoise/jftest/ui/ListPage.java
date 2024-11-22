package jp.whitenoise.jftest.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.service.ListService;

@Route(value = "list", layout = MainLayout.class)
@PageTitle("登録一覧 - 漁獲予定量集計")
@AnonymousAllowed
public class ListPage extends VerticalLayout {

	public ListPage(ListService service) {
		setSizeFull();
		add(new H4("出荷予定一覧（登録順）"));
		
		Grid<入港予定> grid = new Grid<>();
		grid.setEmptyStateText("出荷予定が未登録です。");
		grid.setItems(service.createDataProvider());
		grid.addColumn(s -> s.get入港予定日()).setHeader("入港予定日");
		grid.addColumn(s -> s.get入港漁船().get漁船名()).setHeader("漁船名");
		grid.addComponentColumn(s -> new Button(VaadinIcon.EDIT.create(), event -> {
			UI.getCurrent().navigate(AddPage.class, s.getId());
		})).setWidth("20px");
		grid.setSizeFull();
		add(grid);
	}
}
