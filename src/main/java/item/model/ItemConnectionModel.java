package item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import map.model.AreaModel;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ItemConnectionModel implements Serializable {

    private AreaModel connectionWithAreaReference;
    private ItemModel itemConnectedToAreaReference;
    private String itemUsage;

}
