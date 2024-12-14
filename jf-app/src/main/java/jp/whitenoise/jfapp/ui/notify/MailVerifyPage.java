package jp.whitenoise.jfapp.ui.notify;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jp.whitenoise.JfApplication;
import jp.whitenoise.jfapp.service.MailService;
import jp.whitenoise.jfapp.ui.MainLayout;
import jp.whitenoise.jfapp.ui.TopPage;

@Route(value = "mail/verify", layout = MainLayout.class)
@PageTitle("メール登録 - " + JfApplication.APP_NAME)
@AnonymousAllowed
public class MailVerifyPage extends VerticalLayout implements HasUrlParameter<String> {

    /** メール配信サービス. */
    private final MailService serivce;

    /**
     * コンストラクタ.
     */
    public MailVerifyPage(MailService serivce) {
        this.serivce = serivce;

        add(new H4("メール配信登録"));
        Div message = new Div("メールアドレスを本登録しました。");
        Button btn戻る = new Button("戻る");
        btn戻る.addClickListener(event -> {
            UI.getCurrent().navigate(TopPage.class);
        });
        add(message, btn戻る);
    }

    @Override
    public void setParameter(BeforeEvent event, String id) {
        // メールアドレス本登録
        serivce.verify(id);
    }
}
