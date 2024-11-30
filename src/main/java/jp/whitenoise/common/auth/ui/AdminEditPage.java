package jp.whitenoise.common.auth.ui;

import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;

import org.springframework.dao.OptimisticLockingFailureException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import jp.whitenoise.common.ui.SuccessNotification;
import jp.whitenoise.common.ui.WarnNotification;
import jp.whitenoise.common.util.UiUtil;
import jp.whitenoise.jftest.ui.MainLayout;

@Route(value = "users/adminEdit", layout = MainLayout.class)
@PageTitle("ユーザ登録・編集 - " + JfTestApplication.APP_NAME)
@RolesAllowed(EUserRole.ROLE_ADMIN)
public class AdminEditPage extends UserEditPage implements HasUrlParameter<String> {

    /** ユーザ権限. */
    private final MultiSelectComboBox<EUserRole> cmbユーザ権限;
    /** 有効／無効. */
    private final Checkbox chk有効フラグ;

    /**
     * コンストラクタ.
     * 
     * @param userDao ユーザ情報DAO
     */
    public AdminEditPage(AuthService service) {
        super(service);

        // ユーザ名編集可能
        txtユーザ名.setRequired(true);
        txtユーザ名.setMaxLength(16);
        txtユーザ名.setManualValidation(true);
        txtユーザ名.addValueChangeListener(event -> {
            String newValue = txtユーザ名.getValue();
            String errMsg = "";
            boolean isInValid = false;
            if (16 < newValue.length() && !newValue.matches("[0-9a-zA-Z_-]+")) {
                errMsg = "ユーザ名は英数字16文字以内で入力してください。";
                isInValid = true;
            } else if (super.targetUser.isEmpty() && service.findByUsername(newValue).isPresent()) {
                errMsg = "指定されたユーザ名は既に利用されています。";
                isInValid = true;
            }
            txtユーザ名.setInvalid(isInValid);
            txtユーザ名.setErrorMessage(errMsg);
        });

        // ユーザ権限
        cmbユーザ権限 = new MultiSelectComboBox<EUserRole>("ユーザ権限");
        cmbユーザ権限.setRequired(true);
        cmbユーザ権限.setItems(EUserRole.values());
        cmbユーザ権限.setItemLabelGenerator(EUserRole::getNameJpn);
        super.vl管理属性レイアウト.add(cmbユーザ権限);

        // 有効フラグ
        chk有効フラグ = new Checkbox("有効", true);
        super.vl管理属性レイアウト.add(chk有効フラグ);
    }

    /**
     * パラメータ初期化処理.
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String username) {

        // 新規登録の場合
        boolean isNew = username == null;
        txtユーザ名.setReadOnly(!isNew);
        txtパスワード.setRequired(isNew);
        txtパスワード確認.setRequired(isNew);
        if (isNew) {
            return;
        }

        // 自己編集の場合、権限と有効フラグは編集不可
        String loginUser = super.service.getAuthUsername().get();
        boolean isSelfEdit = loginUser.equals(username);
        cmbユーザ権限.setReadOnly(isSelfEdit);
        chk有効フラグ.setReadOnly(isSelfEdit);

        // 編集対象ユーザ情報を画面表示
        targetUser = service.findByUsername(username);
        if (targetUser.isPresent()) {
            User user = targetUser.get();
            txtユーザ名.setReadOnly(true);
            txtユーザ名.setValue(user.getUsername());
            user.getEmailAddr().ifPresent(v -> txtメールアドレス.setValue(v));
            cmbユーザ権限.setValue(user.getRoles().stream().map(EUserRole::valueOf).toList());
            chk有効フラグ.setValue(user.isEnabled());
        }
        // 取得失敗（別ユーザが削除等）
        else {
            // エラーメッセージ表示して戻る
            UI.getCurrent().getPage().getHistory().back();
            new ErrorNotification("エラー：対象ユーザが見つかりません").open();
        }
    }

    /**
     * 保存ボタン押下.
     */
    @Override
    protected void save() {

        // 入力チェックエラー
        if (!UiUtil.childIsValid(this)) {
            new WarnNotification("入力内容が不正です。").open();
            return;
        }

        try {
            service.save(targetUser, txtユーザ名.getValue(),
                    Optional.ofNullable(txtパスワード.getValue()),
                    Optional.ofNullable(txtメールアドレス.getValue()),
                    cmbユーザ権限.getValue(), chk有効フラグ.getValue());

            // 保存成功
            getUI().ifPresent((ui) -> ui.refreshCurrentRoute(true));
            new SuccessNotification("保存しました。").open();

        } catch (OptimisticLockingFailureException e) {
            // 排他エラー
            new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
        }
    }
}
