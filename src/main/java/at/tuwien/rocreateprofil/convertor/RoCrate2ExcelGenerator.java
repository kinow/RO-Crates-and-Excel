package at.tuwien.rocreateprofil.convertor;

import at.tuwien.rocreateprofil.model.entity.dataset.Sheet;
import at.tuwien.rocreateprofil.model.entity.dataset.mapper.SheetMapper;
import at.tuwien.rocreateprofil.model.entity.rocrate.RoCrate;
import at.tuwien.rocreateprofil.model.entity.rocrate.meta.util.RoCrateMetaUtil;
import at.tuwien.rocreateprofil.output.excel.SheetInserter;
import at.tuwien.rocreateprofil.output.excel.WorkbookWriter;
import at.tuwien.rocreateprofil.output.rocrate.RoCrateSchema;
import at.tuwien.rocreateprofil.output.rocrate.Xlsx2rocrateSchema;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class RoCrate2ExcelGenerator implements Convertor {

    public static final String RO_CRATE_ROOT = "RO_CRATE_ROOT";

    private String roCrateRoot;

    private RoCrate roCrate;
    private XSSFWorkbook workbook;
    private String outputFileName;

    public RoCrate2ExcelGenerator(Map<String, Object> parameterMap) {
        initializeStateFromParameterMap(parameterMap);
        roCrate = RoCrate.loadFromFile(this.roCrateRoot);
    }

    private void initializeStateFromParameterMap(Map<String, Object> parameterMap) {
        this.roCrateRoot = (String) parameterMap.get(RO_CRATE_ROOT);
    }

    @Override
    public void convert() {
        JSONArray entityGraph = RoCrateMetaUtil.getRoCrateMetadataGraphArray(roCrate.getRoCrateMetadata());
        JSONObject rootDataEntity = RoCrateMetaUtil.retrieveEntityById(entityGraph, RoCrateSchema.ROOT_DATA_ENTITY_ID);

        JSONArray excelFiles = (JSONArray)rootDataEntity.get(RoCrateSchema.HAS_PART);
        Iterator fileIterator = excelFiles.iterator();

        workbook = new XSSFWorkbook();
        while (fileIterator.hasNext()) {
            JSONObject fileReference = (JSONObject) fileIterator.next();
            String fileDataEntityId = (String) fileReference.get(RoCrateSchema.ID);
            outputFileName = fileDataEntityId;

            JSONObject fileDataEntity = RoCrateMetaUtil.retrieveEntityById(entityGraph, fileDataEntityId);

            JSONArray sheets = (JSONArray) fileDataEntity.get(Xlsx2rocrateSchema.SHEET);
            Iterator sheetIterator = sheets.iterator();

            while (sheetIterator.hasNext()) {
                JSONObject sheetReference = (JSONObject) sheetIterator.next();
                String sheetContextEntityId = sheetReference.get(RoCrateSchema.ID).toString();
                JSONObject sheetContextEntity = RoCrateMetaUtil.retrieveEntityById(entityGraph, sheetContextEntityId);

                // initialize Sheet model
                Sheet mappedSheet = SheetMapper.fromJSON(entityGraph, sheetContextEntity);
                // append sheet with values
                SheetInserter.insertWithGeneratedValues(mappedSheet, workbook);
            }
        }
    }

    @Override
    public void writeOutput() {
        WorkbookWriter.write(workbook, outputFileName);
    }
}
