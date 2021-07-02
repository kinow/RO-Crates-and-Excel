package at.tuwien.rocreateprofil.model.entity.rocrate;

import at.tuwien.rocreateprofil.model.entity.rocrate.meta.ContextBuilder;
import at.tuwien.rocreateprofil.model.entity.rocrate.meta.FileDataEntityBuilder;
import at.tuwien.rocreateprofil.model.entity.rocrate.meta.RootDataEntityBuilder;
import at.tuwien.rocreateprofil.model.entity.rocrate.meta.RootEntityBuilder;
import at.tuwien.rocreateprofil.model.entity.rocrate.meta.util.RoCrateMetaUtil;
import at.tuwien.rocreateprofil.output.rocrate.RoCrateSchema;
import org.apache.commons.lang3.NotImplementedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoCrate {

    private Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private String roCrateLocation;

    private JSONObject roCrateMetadata;
    private List<File> files = new ArrayList<>();

    public RoCrate(String roCrateLocation, URL license) {
        this.roCrateLocation = roCrateLocation;
        initializeRoCrateMetadata(license);
    }

    public void addExcelFile(File file) {
        this.files.add(file);
        addFileDateEntityToGraph(file);
    }

    private void addFileDateEntityToGraph(File file) {
        // create hasFile on Dataset
        JSONObject rootDataEntity = RoCrateMetaUtil.getRootDataEntity(roCrateMetadata);
        RootDataEntityBuilder.addFile(rootDataEntity, file);
        // create File Data entity
        JSONObject fileDataEntity = FileDataEntityBuilder.buildMinimal(file);
        RoCrateMetaUtil.getRoCrateMetadataGraphArray(roCrateMetadata).add(fileDataEntity);
    }

    private void initializeRoCrateMetadata(URL license) {
        if (this.roCrateMetadata != null) {
            logger.info("RO crate already initialized, skipping ro-crate-meta.json skeleton generation");
        }
        roCrateMetadata = new JSONObject();
        // add context
        roCrateMetadata.put(RoCrateSchema.CONTEXT, ContextBuilder.build());
        // add graph array
        roCrateMetadata.put(RoCrateSchema.GRAPH, new JSONArray());
        // append root entity into graph
        RoCrateMetaUtil.getRoCrateMetadataGraphArray(roCrateMetadata).add(RootEntityBuilder.build());
        // append empty root entity description
        RoCrateMetaUtil.getRoCrateMetadataGraphArray(roCrateMetadata).add(RootDataEntityBuilder.buildWithoutFiles(license));
    }

    public JSONObject getRoCrateMetadata() {
        return roCrateMetadata;
    }

    public void write() {
        logger.info(roCrateMetadata.toString());
        throw new NotImplementedException();
    }

    public static RoCrate load(String roCrateLocation) {
        throw new NotImplementedException();
//        return new RoCrate(roCrateLocation);
    }

}