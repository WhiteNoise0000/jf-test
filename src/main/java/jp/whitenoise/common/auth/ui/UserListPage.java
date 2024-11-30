package jp.whitenoise.common.auth.ui;

import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.common.auth.AuthService;
import jp.whitenoise.common.auth.CosmosUserDetailManager;
import jp.whitenoise.common.auth.EUserRole;
import jp.whitenoise.common.auth.User;
import jp.whitenoise.jftest.ui.MainLayout;

@Route(value = "admin/userList", layout = MainLayout.class)
@PageTitle("ユーザ一覧 - " + JfTestApplication.APP_NAME)
@RolesAllowed(EUserRole.ROLE_ADMIN)
public class UserListPage extends VerticalLayout {

    private final AuthService service;
    private final CosmosUserDetailManager manager;

    public UserListPage(AuthService service, CosmosUserDetailManager manager) {
        this.service = service;
        this.manager = manager;

        setSizeFull();
        add(new H4("ユーザ一覧"));

        Button btnユーザ登録 = new Button("新規ユーザ登録");
        btnユーザ登録.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnユーザ登録.addClickListener(event -> UI.getCurrent().navigate(UserEditPage.class));
        add(btnユーザ登録);

        Grid<User> grid = new Grid<>();
        grid.setItems(service.createDataProvider());
        grid.addColumn(u -> u.getUsername()).setHeader("ユーザ名");
        grid.addColumn(
                u -> u.getRoles().stream().map(r -> EUserRole.valueOf(r).getNameJpn()).collect(Collectors.joining(",")))
                .setHeader("権限");
        grid.addColumn(u -> u.isEnabled() ? "有効" : "無効").setHeader("アカウント状態");
        grid.addColumn(u -> u.isAccountNonLocked() ? "－" : "ロック中").setHeader("ロックアウト");

        // 編集・削除ボタン表示
        grid.addComponentColumn(u -> {
            HorizontalLayout hl = new HorizontalLayout();
            // 編集ボタン
            hl.add(new Button(VaadinIcon.EDIT.create(), event -> editUser(u.getUsername())));
            // 自分自身ではない場合のみ
            service.getAuthenticatedUser().ifPresent(loginUser -> {
                if (!loginUser.getUsername().equals(u.getUsername())) {
                    // 削除ボタン
                    Icon delIcon = VaadinIcon.CLOSE_BIG.create();
                    delIcon.setColor("red");
                    hl.add(new Button(delIcon, event -> deleteUser(u.getUsername())));
                }
            });
            return hl;
        }).setHeader("編集・削除");
        grid.setSizeFull();
        add(grid);
    }

    /**
     * ユーザ編集ボタン押下.
     * 
     * @param username ユーザ名
     */
    private void editUser(String username) {
        // 編集画面へ遷移
        boolean isSelf = service.getAuthUsername().orElse("").equals(username);
        if (isSelf) {
            // 自分自身の場合は編集専用ページへ遷移
            UI.getCurrent().navigate(UserEditPage.class);
        } else {
            UI.getCurrent().navigate(AdminEditPage.class, username);
        }
    }

    /**
     * ユーザ削除ボタン押下.
     * 
     * @param username ユーザ名
     */
    private void deleteUser(String username) {
        // 確認ダイアログを表示
        new ConfirmDialog("削除確認", "選択したユーザを削除しますか？",
                "はい", yes -> {
                    manager.deleteUser(username);
                    UI.getCurrent().refreshCurrentRoute(false);
                },
                "キャンセル", no -> {
                }).open();
    }
}
