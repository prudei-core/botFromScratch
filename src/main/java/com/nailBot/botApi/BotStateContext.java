package com.nailBot.botApi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
        } else if(isFillingRecordState(currentState)){
            return messageHandlers.get(BotState.FILLING_RECORD);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_NAME:
            case ASK_NUMBER:
            case FILLING_PROFILE:
            case PROFILE_FILLED:
                return true;
            default:
                return false;
        }
    }

    private boolean isFillingRecordState(BotState currentState) {
        switch (currentState) {
            case ASK_DATE:
            case ASK_TIME:
            case ASK_SERVICE:
            case FILLING_RECORD:
            case RECORD_FILLED:
                return true;
            default:
                return false;
        }
    }

}
