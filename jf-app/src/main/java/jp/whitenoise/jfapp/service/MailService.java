package jp.whitenoise.jfapp.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.models.ResponseError;
import com.azure.core.util.polling.SyncPoller;
import com.vaadin.flow.router.RouteConfiguration;

import jp.whitenoise.jfapp.dao.メール配信Dao;
import jp.whitenoise.jfapp.model.メール配信;
import jp.whitenoise.jfapp.ui.notify.MailVerifyPage;

/**
 * メール配信サービス.
 */
@Component
public class MailService {

    /** ロガー. */
    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    /** Azure Email Communication Serviceクライアント. */
    private final EmailClient mailClient;
    /** メール配信Dao. */
    private final メール配信Dao dao;
    /** メール送信元アドレス. */
    private final String fromAddress;

    /**
     * コンストラクタ.
     * 
     * @param dao メール配信Dao
     * @param conStr メールサービス接続文字列
     * @param fromAddress メール送信元アドレス
     */
    public MailService(メール配信Dao dao,
            @Value("${mail.conStr}") String conStr,
            @Value("${mail.formAddress}") String fromAddress) {
        this.dao = dao;
        this.mailClient = new EmailClientBuilder().connectionString(conStr).buildClient();
        this.fromAddress = fromAddress;
    }

    /**
     * メールアドレス登録.
     * 
     * @param address アドレス
     */
    public void regist(String address) {

        // 既に登録済みの場合、何もしない
        if (dao.findByアドレス(address) != null) {
            return;
        }

        // メールアドレス仮登録
        メール配信 entity = dao.save(new メール配信(address));

        // 未登録の場合、検証用メールを送信
        sendVerifyMail(address, entity.getId());
    }

    /**
     * メールアドレス検証.
     * 
     * @param address アドレス
     */
    public void verify(String id) {

        // 登録されていない場合、何もしない（例外は出さない）
        Optional<メール配信> ret = dao.findById(id);
        if (ret.isEmpty()) {
            return;
        }

        // メールアドレスを本登録に変更
        メール配信 entity = ret.get();
        entity.set検証済み(true);
        entity.setTtl(-1);
        dao.save(entity);

        // TODO 処理時間の差で登録有無を見分ける攻撃があったはず
        // →処理間の平均化は必要？
    }

    /**
     * 検証メール送信.
     * 
     * @param address アドレス
     * @param id 
     */
    private void sendVerifyMail(String address, String id) {

        EmailMessage send = new EmailMessage();

        // 送信タイトル
        String subject = "メールアドレス認証(漁獲速報配信)";
        send.setSubject(subject);
        // 送信元アドレス
        send.setSenderAddress(fromAddress);
        // TOで送信
        send.setToRecipients(new EmailAddress(address));
        // 本文
        send.setBodyPlainText("""
                ※このメールは、【漁獲速報配信】にご登録いただいたメールアドレス宛に自動的に送信しています。

                この度は、漁獲速報配信にご登録頂きありがとうございます。
                5分以内に、下記リンクよりメールアドレスの認証を完了してください。

                認証URL：%s/%s

                【ご注意】
                アドレスを登録した覚えがない場合は、リンクは押下せず本メールは破棄ください。
                """.formatted(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString(),
                RouteConfiguration.forSessionScope().getUrl(MailVerifyPage.class, id)));

        // メール送信開始
        SyncPoller<EmailSendResult, EmailSendResult> poller = mailClient.beginSend(send);
        EmailSendResult result = poller.waitForCompletion().getValue(); // 送信完了まで待機

        // 送信結果出力
        logger.info("status:" + result.getStatus());
        if (result.getStatus() != EmailSendStatus.SUCCEEDED) {
            ResponseError e = result.getError();
            logger.error(e.getCode() + ":" + e.getMessage());
        }
    }
}
