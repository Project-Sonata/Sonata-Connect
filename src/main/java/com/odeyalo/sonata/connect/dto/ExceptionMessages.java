package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ExceptionMessages {
    @JsonUnwrapped
    List<ExceptionMessage> messages = new ArrayList<>();

    public static ExceptionMessages empty() {
        return new ExceptionMessages();
    }

    public int getSize() {
        return messages.size();
    }

    public void addMessage(ExceptionMessage message) {
        this.messages.add(message);
    }
}
