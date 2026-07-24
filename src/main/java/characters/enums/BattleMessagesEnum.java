package characters.enums;

import lombok.Getter;

@Getter
public enum BattleMessagesEnum {
    CANNOT_ACT_WHILE_IN_BATTLE("You can't do this action while in battle!"),
    NO_ENEMY_TO_ATTACK("There is no enemy to attack!"),
    ATTACK_DAMAGE_DEALT("You damaged the enemy for %s attack damage!"),
    ENEMY_DEFEATED("The enemy is dead!"),
    DAMAGE_TAKEN("%s damaged you for %s damage!"),
    PLAYER_DIED("You died!");

    private final String message;

    BattleMessagesEnum(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
