package com.nailBot.botApi;

import com.nailBot.NailTelegramBot;
import com.nailBot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.nailBot.service.ReplyMessagesService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramFacade {
    private ReplyMessagesService messagesService;
    private NailTelegramBot nailBot;
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;

    public TelegramFacade(ReplyMessagesService messagesService, @Lazy NailTelegramBot nailBot, UserDataCache userDataCache, BotStateContext botStateContext) {
        this.messagesService = messagesService;
        this.nailBot = nailBot;
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.GREETING;
                //myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.hello"), "static/images/wizard_logo.jpg");
                break;
            case "Записаться":
                botState = BotState.FILLING_PROFILE;
                break;
            case "Моя анкета":
                botState = BotState.SHOW_RECORD;
                break;
            case "Местоположение":
                botState = BotState.SHOW_WERE_AM_I;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        //botState = BotState.ASK_DESTINY;

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
