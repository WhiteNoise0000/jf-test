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
import com.vaadin.flow.theme.lumo.LumoUtility;

import jp.whitenoise.JfApplication;
import jp.whitenoise.jfapp.service.MailService;
import jp.whitenoise.jfapp.ui.MainLayout;

@Route(value = "mailVerify", layout = MainLayout.class)
@PageTitle("メール配信 - " + JfApplication.APP_NAME)
@AnonymousAllowed
public class MailVerifyPage extends VerticalLayout implements HasUrlParameter<String> {

    /** メール配信サービス. */
    private final MailService serivce;
    /** メッセージ. */
    private final Div message;

    /**
     * コンストラクタ.
     */
    public MailVerifyPage(MailService serivce) {
        this.serivce = serivce;

        add(new H4("メールアドレス本登録"));
        message = new Div();
        Button btn戻る = new Button("戻る");
        btn戻る.addClickListener(event -> {
            UI.getCurrent().navigate(MailSubscribePage.class);
        });
        add(message, btn戻る);
    }

    @Override
    public void setParameter(BeforeEvent event, String id) {
        // メールアドレス本登録
        if (serivce.verify(id)) {
            message.setText("メールアドレスを本登録しました。");
        }
        // 登録なし
        else {
            message.setText("無効なリンクです。再度仮登録を行ってください。");
            message.addClassName(LumoUtility.TextColor.ERROR);
        }
    }
}
