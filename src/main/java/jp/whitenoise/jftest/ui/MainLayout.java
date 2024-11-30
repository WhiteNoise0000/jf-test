package jp.whitenoise.jftest.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText.WhiteSpace;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import jp.whitenoise.common.auth.AuthService;
import jp.whitenoise.common.auth.ui.UserEditPage;
import jp.whitenoise.common.auth.ui.UserListPage;
import jp.whitenoise.jftest.ui.admin.SettingsPage;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * メインレイアウト.
 */
public class MainLayout extends AppLayout {

    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthService service, AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;

        H3 title = new H3("漁獲・出荷予定速報");
        title.setWhiteSpace(WhiteSpace.NOWRAP);
        addToNavbar(new DrawerToggle(), title);

        // ログイン中の場合
        if (service.getAuthenticatedUser().isPresent()) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setPadding(false);
            hl.setSizeFull();
            hl.setJustifyContentMode(JustifyContentMode.END);
            Button btnLogOff = new Button("ログオフ", VaadinIcon.SIGN_OUT.create(), click -> service.logout());
            btnLogOff.setTooltipText("ログオフ");
            btnLogOff.addThemeVariants(ButtonVariant.LUMO_SMALL);
            hl.add(btnLogOff);
            hl.add(new Span()); // 右パディング代替
            // スマホの場合は下部に表示
            addToNavbar(true, hl);
        }
        // サイドナビゲーション
        addToDrawer(createSideNav());
    }

    private SideNav createSideNav() {
        SideNav menu = new SideNav();
        add(menu, new NavItem("集計", TopPage.class, VaadinIcon.HOME));
        add(menu, new NavItem("一覧", ListPage.class, VaadinIcon.LIST));
        add(menu, new NavItem("登録", EditPage.class, VaadinIcon.EDIT));
        add(menu, "管理", VaadinIcon.TOOLBOX,
                new NavItem("ユーザ一覧", UserListPage.class, VaadinIcon.USERS),
                new NavItem("ユーザ情報", UserEditPage.class, VaadinIcon.USER),
                new NavItem("システム設定", SettingsPage.class, VaadinIcon.TOOLS));
        return menu;
    }

    private void add(SideNav parent, NavItem... items) {
        for (NavItem item : items) {
            if (accessChecker.hasAccess(item.view)) {
                parent.addItem(item.create());
            }
        }
    }

    private void add(SideNav parent, String categoryName, VaadinIcon icon, NavItem... items) {
        SideNavItem cat = new SideNavItem(categoryName);
        cat.setPrefixComponent(icon != null ? icon.create() : null);
        cat.setExpanded(true);
        boolean isAdded = false;
        for (NavItem item : items) {
            if (accessChecker.hasAccess(item.view)) {
                cat.addItem(item.create());
                isAdded = true;
            }
        }
        // すべてアクセス権がある場合のみ追加
        if (isAdded) {
            parent.addItem(cat);
        }
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class NavItem {
        @NonNull
        private final String name;
        @NonNull
        private final Class<? extends Component> view;
        private VaadinIcon icon;

        private SideNavItem create() {
            SideNavItem item = new SideNavItem(name);
            item.setPath(view);
            item.setPrefixComponent(icon != null ? icon.create() : null);
            return item;
        }
    }
}
