package com.syed.test.slackbot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.MessageEvent;

public class FirstBot {
    public static void main(String str[]) throws Exception {
        String botToken = "xoxb-hidden";
        String appToken = "xapp-1-hidden";
        App app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
        app.event(MessageEvent.class, (req, ctx) -> {
            MessageEvent event = req.getEvent();
            ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
                    .channel(event.getChannel())
                    .threadTs(event.getTs())
                    .text("<@" + event.getUser() + "> Thank you! We greatly appreciate your efforts :two_hearts:"));
            if (!message.isOk()) {
                ctx.logger.error("chat.postMessage failed: {}", message.getError());
            }
            return ctx.ack();
        });
        System.out.println("launching");
        SocketModeApp socketModeApp = new SocketModeApp(appToken, app);
        socketModeApp.start();
    }
}
