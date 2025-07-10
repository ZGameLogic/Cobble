package com.zgamelogic.data.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebsocketMessage {
    private WebsocketMessageType type;
    private WebsocketMessageSubtype subtype;
    private String replyId;
    private Object data;

    public WebsocketMessage(WebsocketMessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public WebsocketMessage(WebsocketMessageType type, String replyId, Object data) {
        this.type = type;
        this.replyId = replyId;
        this.data = data;
    }

    public WebsocketMessage(WebsocketMessageType type, WebsocketMessageSubtype subtype, String replyId, Object data) {
        this.type = type;
        this.subtype = subtype;
        this.replyId = replyId;
        this.data = data;
    }
}
