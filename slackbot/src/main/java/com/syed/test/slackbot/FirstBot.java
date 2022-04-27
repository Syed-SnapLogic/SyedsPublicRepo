package com.syed.test.slackbot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.MessageEvent;

public class FirstBot {
    public static void main(String str[]) throws Exception {
        String botToken = "xoxb-hid"; //syed app
        //String botToken = "xoxb-hid"; //mani app
        String appToken = "xapp-1-hid"; //syed app
        //String appToken = "xapp-1-hid"; //maniapp
        App app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
        app.event(MessageEvent.class, (req, ctx) -> {
            System.out.println("message event tracked\n\n");
            MessageEvent event = req.getEvent();
            String msg = event.getText();
            System.out.println("msg:\n" + msg + "\n");
            if (msg.startsWith("New Deal Signed")) {
                System.out.println("yay, new deal signed");
                ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
                        .channel(event.getChannel())
                        .threadTs(event.getTs())
                        .text("Yay deal sealed. Consider sending the following materials to the customer:\n\t" +
                                "<https://www.snaplogic.com/blog/using-snaplogic-at-snaplogic-user-onboarding|User Onboarding>\n\t" +
                                "<https://www.snaplogic.com/resources|Welcome Kit>"));
                if (!message.isOk()) {
                    ctx.logger.error("chat.postMessage failed: {}", message.getError());
                } else {
                    System.out.println("sent successfully!!");
                }
            }
            return ctx.ack();
        });
        System.out.println("launching");
        SocketModeApp socketModeApp = new SocketModeApp(appToken, app);
        socketModeApp.start();
    }
}
