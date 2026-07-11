package map.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class AreaConnectionMaker sets the characteristics for a area connection.
 * An area connection is consisted of the area object which refers to the next
 * area and the direction with witch the two areas are connected. It also
 * implements the Serializable interface so that the storing of area connection
 * objects can be made.
 *
 * @author Vasilis Triantaris
 */
@Getter
@Setter
@AllArgsConstructor
public class AreaConnectionMaker implements Serializable{

    //Data memebr of the area connection maker class
    private Area nextArea;
    private String directionsOnCurrentArea;

}