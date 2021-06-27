package at.tuwien.rocreateprofil.convertor.excel.parser.content;

import at.tuwien.rocreateprofil.model.entity.dataset.Dataset;
import java.util.Map;
import org.apache.jena.ontology.OntModel;

public interface ExcelContentParser {
    
    public void parse(final OntModel ontModel, final Map<String, Dataset> datasets);
    
}