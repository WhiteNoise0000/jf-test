package jp.whitenoise.jftest.ui.admin;

import jakarta.annotation.security.RolesAllowed;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.common.auth.EUserRole;
import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.common.ui.PrimaryButton;
import jp.whitenoise.common.ui.SuccessNotification;
import jp.whitenoise.jftest.model.漁船;
import jp.whitenoise.jftest.service.BoatListService;
import jp.whitenoise.jftest.ui.MainLayout;

@Route(value = "admin/boats", layout = MainLayout.class)
@PageTitle("漁船情報 - " + JfTestApplication.APP_NAME)
@RolesAllowed(EUserRole.ROLE_ADMIN)
public class BoatListPage extends VerticalLayout {

    private final BoatListService service;
    private final Grid<漁船> grid漁船;
    private final GridDataView<漁船> list漁船;

    public BoatListPage(BoatListService service) {
        this.service = service;
        setSizeFull();

        // 漁船一覧
        add(new H4("漁船情報"));
        grid漁船 = new Grid<漁船>();
        grid漁船.setEmptyStateText("漁船が未登録です。");
        list漁船 = grid漁船.setItems(service.createDataProvider());
        grid漁船.setItems(service.createDataProvider());
        add(grid漁船);

        // インライン編集
        Editor<漁船> editor = grid漁船.getEditor();
        editor.setBuffered(true);
        Binder<漁船> binder = new Binder<>();
        editor.setBinder(binder);
        editor.addSaveListener(event -> save(event.getItem()));

        // カラム定義
        Column<漁船> col漁船名 = grid漁船.addColumn(漁船::get漁船名).setHeader("漁船名");
        Column<漁船> col備考 = grid漁船.addColumn(漁船::get備考).setHeader("備考");
        Column<漁船> col編集削除 = grid漁船.addComponentColumn(entity -> {
            Button btn編集 = new Button(VaadinIcon.EDIT.create(), event -> {
                if (editor.isOpen()) {
                    editor.cancel();
                }
                editor.editItem(entity);
            });
            Button btn削除 = new Button(VaadinIcon.CLOSE.create(), event -> delete(entity));
            btn削除.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(btn編集, btn削除);
        }).setHeader("編集・削除");

        // 編集モード
        TextField txt漁船名 = new TextField();
        txt漁船名.setRequired(true);
        txt漁船名.setMaxLength(30);
        txt漁船名.setWidthFull();
        binder.bind(txt漁船名, 漁船::get漁船名, 漁船::set漁船名);
        col漁船名.setEditorComponent(txt漁船名);
        TextField txt備考 = new TextField();
        txt備考.setRequired(true);
        txt備考.setMaxLength(30);
        txt備考.setWidthFull();
        txt備考.setClearButtonVisible(true);
        binder.bind(txt備考, 漁船::get備考, 漁船::set備考);
        col備考.setEditorComponent(txt備考);

        Button btn保存 = new Button(VaadinIcon.CHECK.create(), e -> {
            if (editor.save()) {
                new SuccessNotification("保存しました。").open();
            }
        });
        btn保存.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS);
        btn保存.setEnabled(false);
        Button btnキャンセル = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        btnキャンセル.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        col編集削除.setEditorComponent(new HorizontalLayout(btn保存, btnキャンセル));

        // バリデーション結果に応じて保存ボタン有効
        binder.addStatusChangeListener(event -> btn保存.setEnabled(!event.hasValidationErrors()));

        // 新規登録
        add(new H4("新規登録"));
        Binder<漁船> newBinder = new Binder<漁船>();
        newBinder.setBean(new 漁船());
        TextField txt新規漁船名 = new TextField("漁船名");
        txt新規漁船名.setRequired(true);
        txt新規漁船名.setMaxLength(30);
        newBinder.bind(txt新規漁船名, 漁船::get漁船名, 漁船::set漁船名);
        TextField txt新規備考 = new TextField("備考");
        txt新規備考.setMaxLength(30);
        newBinder.bind(txt新規備考, 漁船::get備考, 漁船::set備考);
        PrimaryButton btn新規保存 = new PrimaryButton("追加", e -> {
            if (newBinder.isValid()) {
                save(newBinder.getBean());
                new SuccessNotification("保存しました。").open();
                newBinder.setBean(new 漁船());
            }
        });
        btn新規保存.setEnabled(false);
        newBinder.addStatusChangeListener(event -> btn新規保存.setEnabled(!event.hasValidationErrors()));
        HorizontalLayout hl = new HorizontalLayout(txt新規漁船名, txt新規備考, btn新規保存);
        hl.setPadding(false);
        hl.setAlignItems(Alignment.BASELINE);
        add(hl);
    }

    /**
     * 漁船保存.
     * 
     * @param item 漁船
     */
    private void save(漁船 item) {
        service.save漁船(item);
        list漁船.refreshAll();
    }

    /**
     * 漁船削除.
     * 
     * @param item 漁船
     */
    private void delete(漁船 item) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("削除確認");
        dialog.setText("「" + item.get漁船名() + "」を削除しますか？");
        dialog.setConfirmButton("はい", yes -> {
            service.delete漁船(item.getId());
            list漁船.refreshAll();
        });
        dialog.setCancelButton("キャンセル", no -> dialog.close());
        dialog.open();
    }

    /**
     * 排他エラー発生.
     */
    @ExceptionHandler(exception = OptimisticLockingFailureException.class)
    private void optimisticLockingFailure(OptimisticLockingFailureException e) {
        new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
    }
}
