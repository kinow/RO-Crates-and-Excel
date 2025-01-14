package at.tuwien.rocreateprofil.model.entity.profile;

import at.tuwien.rocreateprofil.model.entity.dataset.Cell;
import at.tuwien.rocreateprofil.model.entity.dataset.ColumnType;
import at.tuwien.rocreateprofil.model.entity.dataset.mapper.columnprofile.CategoricalColumnProfileMapper;
import at.tuwien.rocreateprofil.model.entity.dataset.mapper.columnprofile.ColumnProfileMapper;
import at.tuwien.rocreateprofil.output.rocrate.Xlsx2rocrateSchema;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CategoricalProfileColumn implements ColumnProfile {

    private final HashMap<String, Integer> categories = new HashMap<>();
    private final HashMap<String, Double> categoryProportions = new HashMap<>();
    private Long mode = null;
    private String modeKey = null;
    private Double modeInPercent = null;
    private Integer c = 0;

    @Override
    public void build(List<Cell> cells) {

        for (Cell cell : cells) {
            final String value = String.valueOf(cell.getValue().getValue());

            // Get value
            Integer count = categories.get(value);
            if (count != null) {
                categories.put(value, ++count);
            } else {
                categories.put(value, 1);
            }

            c++;
        }

        // Get mode
        mode = 0L;
        modeKey = "";

        for (String key : categories.keySet()) {
            final Long value = Long.valueOf(categories.get(key));
            // New mode?
            if (value > mode) {
                mode = value;
                modeKey = key;
            }
        }

        modeInPercent = (mode.doubleValue() / cells.size()) * 100;

    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.CATEGORICAL;
    }

    @Override
    public void writeToFile(JSONObject object) {
        // Write properties of column
        object.put(Xlsx2rocrateSchema.FORMAT_TYPE, "categorical");
        if (mode != null) {
            object.put(Xlsx2rocrateSchema.MODE, mode);
        }
        if (modeKey != null) {
            object.put(Xlsx2rocrateSchema.MODE_KEY, modeKey);
        }
        if (modeInPercent != null) {
            object.put(Xlsx2rocrateSchema.MODE_IN_PERCENT, modeInPercent);
        }
        // Write proportion of categories
        final JSONArray cat = new JSONArray();
        final JSONArray catProp = new JSONArray();
        for (String string : categories.keySet()) {
            cat.add(string);
            final Double prop = Double.valueOf(categories.get(string)) / c;
            catProp.add(prop);
        }
        object.put(Xlsx2rocrateSchema.CATEGORY, cat);
        object.put(Xlsx2rocrateSchema.CATEGORY_PROPORTIONS, catProp);
    }

    @Override
    public ColumnProfileMapper getProfileMapper() {
        return new CategoricalColumnProfileMapper();
    }

    public Long getMode() {
        return mode;
    }

    public void setMode(Long mode) {
        this.mode = mode;
    }

    public String getModeKey() {
        return modeKey;
    }

    public void setModeKey(String modeKey) {
        this.modeKey = modeKey;
    }

    public Double getModeInPercent() {
        return modeInPercent;
    }

    public void setModeInPercent(Double modeInPercent) {
        this.modeInPercent = modeInPercent;
    }

    public HashMap<String, Double> getCategoryProportions() {
        return categoryProportions;
    }

    @Override
    public String generateValidValue() {
        return retrieveCategoryByRandomPercentage(Math.random());
    }

    private String retrieveCategoryByRandomPercentage(double randomlyGeneratedPercentage) {
        double percentTotal = 0.0;
        for (String key: categoryProportions.keySet()) {
            percentTotal += categoryProportions.get(key);
            if (percentTotal >= randomlyGeneratedPercentage) {
                return key;
            }
        }
        throw new IllegalStateException("Should not happen!");
    }
}
