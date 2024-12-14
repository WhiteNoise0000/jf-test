package jp.whitenoise.jfnotify;

import java.util.Arrays;
import java.util.UUID;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.BroadcastRequest;
import com.linecorp.bot.messaging.model.TextMessageV2;

/**
 * LINE通知.
 */
public class NotifyLINE {

    /** Messageing APIクライアント. */
    private final MessagingApiClient apiClient;

    /**
     * コンストラクタ.
     * 
     * @param token チャネルアクセストークン
     */
    public NotifyLINE(String token) {
        apiClient = MessagingApiClient.builder(token).build();
    }

    /**
     * ブロードキャストメッセージ送信.
     * 
     * @param message メッセージ
     */
    public void broadcast(String messages) {
        UUID uuid = UUID.randomUUID();
        BroadcastRequest.Builder builder = new BroadcastRequest.Builder(
                Arrays.asList(new TextMessageV2.Builder(messages).build()));
        apiClient.broadcast(uuid, builder.build());
    }
}
