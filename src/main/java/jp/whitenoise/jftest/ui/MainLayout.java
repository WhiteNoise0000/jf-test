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

import jp.whitenoise.common.security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * メインレイアウト.
 */
public class MainLayout extends AppLayout {

    private final AccessAnnotationChecker accessChecker;

    public MainLayout(SecurityService secService, AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;

        H3 title = new H3("漁獲・出荷予定速報");
        title.setWhiteSpace(WhiteSpace.NOWRAP);
        addToNavbar(new DrawerToggle(), title);

        // ログイン中の場合
        if (secService.getAuthenticatedUser() != null) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setPadding(false);
            hl.setSizeFull();
            hl.setJustifyContentMode(JustifyContentMode.END);
            Button btnLogOff = new Button("ログオフ", VaadinIcon.SIGN_OUT.create(), click -> secService.logout());
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
        return menu;
    }

    private void add(SideNav parent, NavItem... items) {
        for (NavItem item : items) {
            if (accessChecker.hasAccess(item.view)) {
                parent.addItem(item.create());
            }
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
            return new SideNavItem(name, view, icon != null ? icon.create() : null);
        }
    }
}
