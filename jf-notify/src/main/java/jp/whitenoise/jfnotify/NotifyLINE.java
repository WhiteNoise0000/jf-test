package jp.whitenoise.jfnotify;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.linecorp.bot.client.base.Result;
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
        try {
            Result<Object> result = apiClient.broadcast(uuid, builder.build()).get();
            System.out.println("LINE result: OK/" + result);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("LINE error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
