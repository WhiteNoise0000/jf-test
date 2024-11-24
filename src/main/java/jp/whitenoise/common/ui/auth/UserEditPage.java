package jp.whitenoise.common.ui.auth;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;

import org.springframework.dao.OptimisticLockingFailureException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.common.auth.AuthService;
import jp.whitenoise.common.auth.EUserRole;
import jp.whitenoise.common.auth.User;
import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.common.util.UiUtil;
import jp.whitenoise.jftest.ui.MainLayout;

@Route(value = "admin/userEdit", layout = MainLayout.class)
@PageTitle("ユーザ登録・編集 - " + JfTestApplication.APP_NAME)
@PermitAll
public class UserEditPage extends VerticalLayout implements HasUrlParameter<String> {

    /** 認証・認可サービス. */
    private final AuthService service;
    /** ユーザ名. */
    private final TextField txtユーザ名;
    /** 新パスワード. */
    private final PasswordField txtパスワード;
    /** 新パスワード(確認). */
    private final PasswordField txtパスワード確認;
    /** メールアドレス. */
    private final EmailField txtメールアドレス;
    /** ユーザ権限. */
    private final MultiSelectComboBox<EUserRole> cmbユーザ権限;
    /** 有効／無効. */
    private final Checkbox chk有効フラグ;

    /** 管理権限有無. */
    private boolean isAdmin = false;
    /** 編集対象ユーザ. */
    private Optional<User> targetUser = Optional.empty();

    /**
     * コンストラクタ.
     * 
     * @param userDao ユーザ情報DAO
     */
    public UserEditPage(AuthService service) {
        this.service = service;
        this.isAdmin = service.isAdmin();

        add(new H4("ユーザ登録・編集"));
        setSpacing(false);
        setSizeFull();

        // ユーザ名
        txtユーザ名 = new TextField("ユーザ名");
        txtユーザ名.setRequired(true);
        txtユーザ名.setAllowedCharPattern("[0-9a-zA-Z_-]");
        txtユーザ名.setErrorMessage("ユーザ名は英数字16文字以内で入力してください。");
        add(txtユーザ名);

        // 新パスワード
        txtパスワード = new PasswordField("パスワード");
        txtパスワード.setMinLength(8);
        txtパスワード.setErrorMessage("パスワードは8文字以上で入力してください。");
        add(txtパスワード);

        // 新パスワード(確認)
        txtパスワード確認 = new PasswordField("パスワード(確認)");
        txtパスワード確認.setErrorMessage("パスワードが一致しません。");
        txtパスワード確認.setManualValidation(true);
        txtパスワード確認.addValueChangeListener(event -> {
            // パスワード入力の一致をチェック
            boolean isInvalid = !txtパスワード.getValue().equals(txtパスワード確認.getValue());
            txtパスワード確認.setInvalid(isInvalid);
        });
        add(txtパスワード確認);

        // メールアドレス
        txtメールアドレス = new EmailField("メールアドレス(任意)");
        txtメールアドレス.setTooltipText("各種通知をする場合は入力");
        txtメールアドレス.setErrorMessage("メールアドレスが不正です。");
        add(txtメールアドレス);

        // ユーザ権限
        cmbユーザ権限 = new MultiSelectComboBox<EUserRole>("ユーザ権限");
        cmbユーザ権限.setRequired(true);
        cmbユーザ権限.setItems(EUserRole.values());
        cmbユーザ権限.setItemLabelGenerator(EUserRole::getNameJpn);
        add(cmbユーザ権限);

        // 有効フラグ
        chk有効フラグ = new Checkbox("有効", true);
        add(chk有効フラグ);

        // 戻る・保存ボタン
        Button btn保存 = new Button("保存", e -> save());
        btn保存.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button btn戻る = new Button("戻る", click -> UI.getCurrent().getPage().getHistory().back());
        add(new HorizontalLayout(btn戻る, btn保存));
    }

    /**
     * パラメータ初期化処理.
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String username) {

        // 管理者権限かつ新規登録時は何もしない
        if (isAdmin && username == null) {
            txtパスワード.setRequired(true);
            txtパスワード確認.setRequired(true);
            return;
        }
        // ユーザ権限の場合
        if (!isAdmin) {
            // 自分自身以外は編集不可
            String loginUser = service.getAuthUsername().get();
            if (!loginUser.equals(username)) {
                new ErrorNotification("Accses dinied.").open();
                UI.getCurrent().getPage().getHistory().back();
                return;
            }
            // ユーザ権限で編集不可・非表示
            txtユーザ名.setReadOnly(true);
            cmbユーザ権限.setReadOnly(true);
            cmbユーザ権限.setVisible(false);
            chk有効フラグ.setReadOnly(true);
            chk有効フラグ.setVisible(false);
        }

        // 編集対象ユーザの情報を画面表示
        targetUser = service.findByUsername(username);
        if (targetUser.isPresent()) {
            User user = targetUser.get();
            txtユーザ名.setReadOnly(true);
            txtユーザ名.setValue(user.getUsername());
            user.getEmailAddr().ifPresent(v -> txtメールアドレス.setValue(v));
            if (isAdmin) {
                cmbユーザ権限.setValue(user.getRoles().stream().map(EUserRole::valueOf).toList());
                chk有効フラグ.setValue(user.isEnabled());
            }
        }
        // 取得失敗（別ユーザが削除等）
        else {
            // エラーメッセージ表示して戻る
            new ErrorNotification("エラー：対象ユーザが見つかりません").open();
            UI.getCurrent().getPage().getHistory().back();
        }
    }

    /**
     * 保存ボタン押下.
     */
    private void save() {

        txtパスワード.setValue(txtパスワード.getValue());
        txtメールアドレス.setValue(txtメールアドレス.getValue());

        // 入力チェックエラー
        if (!UiUtil.childIsValid(this)) {
            Notification warn = new Notification("入力エラー", 5000, Position.BOTTOM_CENTER);
            warn.addThemeVariants(NotificationVariant.LUMO_WARNING);
            warn.open();
            return;
        }

        try {
            service.save(targetUser, txtユーザ名.getValue(),
                    Optional.ofNullable(txtパスワード.getValue()),
                    Optional.ofNullable(txtメールアドレス.getValue()),
                    cmbユーザ権限.getValue(), chk有効フラグ.getValue());

            // 保存成功
            getUI().ifPresent((ui) -> ui.refreshCurrentRoute(true));
            Notification success = new Notification("保存しました.", 5000, Position.BOTTOM_CENTER);
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            success.open();

        } catch (OptimisticLockingFailureException e) {
            // 排他エラー
            new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
        }
    }
}
