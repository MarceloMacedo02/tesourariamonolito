## Issues Found and Fixes Needed for financeiro_grupos_rubrica_detalhado.jrxml

### Issue 1: Deprecated isStretchWithOverflow Attribute
- **Problem**: The `isStretchWithOverflow` attribute is deprecated in newer versions of JasperReports
- **Solution**: Replace with `textAdjust="StretchHeight"` attribute
- **Files affected**: 
  - subreport_rubrica.jrxml (line with textField containing isStretchWithOverflow="true")

### Issue 2: Misplaced subDataset Element
- **Problem**: The error indicates a `subDataset` element is in an invalid location in the XML structure
- **Solution**: Ensure all `subDataset` elements are placed at the top level of the jasperReport, after parameters/fields but before sections
- **Files affected**: 
  - subreport_rubrica.jrxml (the subDataset is correctly placed at the end, but there might be an issue with how it's referenced)

### Recommended Actions:

1. **For the isStretchWithOverflow issue**:
   - In subreport_rubrica.jrxml, find the textField elements with `isStretchWithOverflow="true"`
   - Replace with `textAdjust="StretchHeight"` attribute
   - Example:
     ```xml
     <!-- Before -->
     <textField isStretchWithOverflow="true">
     
     <!-- After -->
     <textField textAdjust="StretchHeight">
     ```

2. **For the subDataset structure issue**:
   - Ensure the subDataset in subreport_rubrica.jrxml is correctly placed at the end of the file
   - Verify that the datasetRun references the correct subDataset name
   - Check that there are no duplicate or misplaced subDataset elements

3. **Additional Recommendations**:
   - Consider upgrading to a newer version of JasperReports if possible
   - Validate the JRXML files with the JasperReports schema to catch structural issues