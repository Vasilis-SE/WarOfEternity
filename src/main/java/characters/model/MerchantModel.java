package characters.model;

import Items.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class MerchantModel extends CharacterAbstractModel implements Serializable {

    private List<Item> merchantGoods;

}
