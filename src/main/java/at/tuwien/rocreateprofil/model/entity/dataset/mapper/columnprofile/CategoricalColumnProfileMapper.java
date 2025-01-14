package at.tuwien.rocreateprofil.model.entity.dataset.mapper.columnprofile;

import at.tuwien.rocreateprofil.model.entity.dataset.Column;
import at.tuwien.rocreateprofil.model.entity.profile.CategoricalProfileColumn;
import at.tuwien.rocreateprofil.output.rocrate.Xlsx2rocrateSchema;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CategoricalColumnProfileMapper implements ColumnProfileMapper {

    @Override
    public CategoricalProfileColumn map(JSONObject columnEntity, Column column) {
        Long mode = (Long) columnEntity.get(Xlsx2rocrateSchema.MODE);
        String modeKey = (String) columnEntity.get(Xlsx2rocrateSchema.MODE_KEY);
        Double modeInPercent = (Double) columnEntity.get(Xlsx2rocrateSchema.MODE_IN_PERCENT);

        CategoricalProfileColumn profile = new CategoricalProfileColumn();
        
        // Get categories and their proportions
        JSONArray cat = (JSONArray) columnEntity.get(Xlsx2rocrateSchema.CATEGORY);
        JSONArray prop = (JSONArray) columnEntity.get(Xlsx2rocrateSchema.CATEGORY_PROPORTIONS);
        HashMap<String, Double> categoryProportions = profile.getCategoryProportions();
        for (int i = 0; i < cat.size(); i++) {
            categoryProportions.put((String) cat.get(i), 
                    (Double) prop.get(i));
        }

        profile.setMode(mode);
        profile.setModeKey(modeKey);
        profile.setModeInPercent(modeInPercent);

        return profile;
    }
}
