package jp.whitenoise.jfnotify;

import java.time.LocalDate;
import java.util.List;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.SyncPoller;

/**
 * メール通知.
 */
public class NotifyEmail {

    /** Azure Email Communication Serviceクライアント. */
    private final EmailClient mailClient;
    /** 送信元アドレス. */
    private final String fromAddress;

    /**
     * コンストラクタ.
     * 
     * @param conStr 接続文字列
     * @param fromAddress 送信元アドレス
     */
    public NotifyEmail(String conStr, String fromAddress) {
        this.mailClient = new EmailClientBuilder().connectionString(conStr).buildClient();
        this.fromAddress = fromAddress;
    }

    /**
     * メール送信.
     * 
     * @param message メッセージ
     */
    public void broadcast(String messages, List<String> address) {
        EmailMessage send = new EmailMessage();

        // 送信タイトル
        String subject = "出荷予定(%s)".formatted(LocalDate.now().plusDays(1));
        send.setSubject(subject);
        // 送信元アドレス
        send.setSenderAddress(fromAddress);
        // BCCで送信
        send.setBccRecipients(address.stream().map(t -> new EmailAddress(t)).toList());

        // メール送信開始
        SyncPoller<EmailSendResult, EmailSendResult> poller = mailClient.beginSend(send);
        EmailSendResult result = poller.waitForCompletion().getValue(); // 送信完了まで待機

        // 送信結果出力
        System.out.println("status:" + result.getStatus());
        if (result.getStatus() != EmailSendStatus.SUCCEEDED) {
            System.out.println(result.getError());
        }
    }
}
