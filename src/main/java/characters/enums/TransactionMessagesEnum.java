package characters.enums;

import lombok.Getter;

@Getter
public enum TransactionMessagesEnum {
    NO_SUCH_TRANSACTION_OR_PERSON("There is no such transaction / person to contact!");

    private final String message;

    TransactionMessagesEnum(String message) {
        this.message = message;
    }
}
