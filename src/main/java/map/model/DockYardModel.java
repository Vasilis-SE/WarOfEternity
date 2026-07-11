package map.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockYardModel {

    private Area startingDockLocation;
    private Area destinationDockLocation;
    private double sailingFee;

}
