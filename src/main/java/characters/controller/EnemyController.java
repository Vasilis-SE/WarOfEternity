package characters.controller;

import characters.service.EnemyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import map.model.AreaModel;
import org.json.simple.JSONArray;

import java.util.List;

/**
 * Class EnemyController is the controlling class for actions based on the
 * enemy roster data, delegating the business logic to EnemyService.
 *
 * @author Vasilis Triantaris
 */
@RequiredArgsConstructor
public class EnemyController {

    private final List<AreaModel> areaModels;
    private final EnemyService enemyService;

    @Getter
    private JSONArray jsonEnemiesArray;

    public void loadEnemiesForGame() {
        this.jsonEnemiesArray = enemyService.loadEnemies(areaModels);
    }
}