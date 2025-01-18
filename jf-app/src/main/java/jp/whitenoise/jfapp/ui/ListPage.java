package jp.whitenoise.jfapp.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jp.whitenoise.JfApplication;
import jp.whitenoise.jfapp.model.入港予定;
import jp.whitenoise.jfapp.service.ListService;

@Route(value = "list", layout = MainLayout.class)
@PageTitle("登録一覧 - " + JfApplication.APP_NAME)
@AnonymousAllowed
public class ListPage extends VerticalLayout {

    public ListPage(ListService service, AccessAnnotationChecker accessChecker) {
        setSizeFull();
        add(new H4("出荷予定一覧（登録順）"));

        Grid<入港予定> grid = new Grid<>();
        grid.setEmptyStateText("出荷予定が未登録です。");
        grid.setItems(service.createDataProvider());
        grid.addColumn(s -> s.get入港予定日()).setHeader("入港予定日");
        grid.addColumn(s -> s.get入港漁船().get漁船名()).setHeader("漁船名");

        // 権限がある場合は編集・削除ボタン表示
        if (accessChecker.hasAccess(EditPage.class)) {
            grid.addComponentColumn(s -> {
                // 編集ボタン
                Button btn編集 = new Button(VaadinIcon.EDIT.create(), event -> {
                    UI.getCurrent().navigate(EditPage.class, s.getId());
                });
                // 削除ボタン
                Icon delIcon = VaadinIcon.CLOSE_BIG.create();
                delIcon.setColor("red");
                Button btn削除 = new Button(delIcon, event -> {
                    new ConfirmDialog("削除確認", "選択した入港予定を削除しますか？",
                            "はい", yes -> {
                                service.delete入港予定(s.getId());
                                UI.getCurrent().refreshCurrentRoute(false);
                            },
                            "キャンセル", no -> {
                            }).open();
                });
                return new HorizontalLayout(btn編集, btn削除);
            });
        }
        grid.setSizeFull();
        add(grid, new Paragraph("※登録内容は最終出荷日＋7日経過時に自動削除します"));
    }
}
