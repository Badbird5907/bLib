package net.badbird5907.blib.util;

import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.objects.TypeCallback;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@RequiredArgsConstructor
public class QuestionConversation extends StringPrompt {
    private final String prompt;
    private final TypeCallback<Prompt,String> callback;
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return prompt;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        return callback.callback(s);
    }
    public Prompt reprompt(){
        return this;
    }
}
