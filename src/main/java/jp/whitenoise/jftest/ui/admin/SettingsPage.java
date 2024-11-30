package jp.whitenoise.jftest.ui.admin;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.common.auth.EUserRole;
import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.common.ui.PrimaryButton;
import jp.whitenoise.common.ui.SuccessNotification;
import jp.whitenoise.jftest.service.SettingsService;
import jp.whitenoise.jftest.ui.MainLayout;

@Route(value = "admin/settings", layout = MainLayout.class)
@PageTitle("システム設定 - " + JfTestApplication.APP_NAME)
@RolesAllowed(EUserRole.ROLE_ADMIN)
public class SettingsPage extends VerticalLayout {

    private final SettingsService service;
    private final TextField txtサイト名;
    private final Grid<String> grid魚種;
    private final GridListDataView<String> list魚種;

    public SettingsPage(SettingsService service) {
        this.service = service;
        add(new H4("システム設定"));
        setSizeFull();

        // 全般
        add(new H5("全般"));
        // サイト名
        txtサイト名 = new TextField("サイト名");
        add(txtサイト名);
        // サイト名保存ボタン
        add(new PrimaryButton("サイト名保存", event -> saveサイト名()));

        // 魚種
        add(new H5("魚種"));
        grid魚種 = new Grid<String>();
        list魚種 = grid魚種.setItems(new LinkedHashSet<String>());
        grid魚種.setEmptyStateText("魚種が未登録です。");
        grid魚種.addColumn(s -> s).setHeader("魚種名");
        grid魚種.addComponentColumn(s -> {
            // 削除ボタン
            Icon delIcon = VaadinIcon.CLOSE_BIG.create();
            delIcon.setColor("red");
            return new Button(delIcon, event -> {
                list魚種.removeItem(s);
            });

        });
        add(grid魚種);

        TextField txt新規魚種名 = new TextField();
        txt新規魚種名.setMaxLength(16);
        Button btn魚種追加 = new Button("追加", event -> {
            if (txt新規魚種名.isEmpty()) {
                return;
            }
            list魚種.addItem(txt新規魚種名.getValue());
            txt新規魚種名.clear();
        });
        add(new HorizontalLayout(txt新規魚種名, btn魚種追加));

        // 保存ボタン
        add(new PrimaryButton("魚種保存", event -> save魚種()));

        // 初期表示
        init();
    }

    /**
     * 画面初期表示.
     */
    private void init() {
        txtサイト名.setValue(service.selectサイト名());
        list魚種.addItems(service.select魚種());
    }

    /**
     * サイト名保存
     */
    private void saveサイト名() {
        if (txtサイト名.isEmpty()) {
            return;
        }
        service.saveサイト名(txtサイト名.getValue());
        new SuccessNotification("保存しました。").open();
    }

    /**
     * 魚種保存.
     */
    private void save魚種() {
        service.save魚種(list魚種.getItems().collect(Collectors.toSet()));
        new SuccessNotification("保存しました。").open();
    }

    /**
     * 排他エラー発生.
     */
    @ExceptionHandler(exception = OptimisticLockingFailureException.class)
    private void optimisticLockingFailure(OptimisticLockingFailureException e) {
        new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
    }
}
