# Changes Made to Fix JasperReports Issues

## File: subreport_rubrica.jrxml

### Issue 1: Deprecated isStretchWithOverflow Attribute

**Before:**
```xml
<textField isStretchWithOverflow="true">
    <reportElement x="0" y="0" width="425" height="10"/>
    <textFieldExpression><![CDATA[$F{descricao}]]></textFieldExpression>
</textField>
```

**After:**
```xml
<textField textAdjust="StretchHeight">
    <reportElement x="0" y="0" width="425" height="10"/>
    <textFieldExpression><![CDATA[$F{descricao}]]></textFieldExpression>
</textField>
```

**Changes made:**
1. Replaced `isStretchWithOverflow="true"` with `textAdjust="StretchHeight"` in two locations:
   - In the "Detalhes dos Movimentos de Entrada" section
   - In the "Detalhes dos Movimentos de Saída" section

### Issue 2: subDataset Structure

**Verification:**
- Confirmed that the `subDataset` element is correctly placed at the end of the file
- No changes were needed as the structure was already correct

## Files with Issues That Need Restoration

The following files appear to be incomplete or corrupted and need to be restored:
- `financeiro_grupos_rubrica_detalhado.jrxml`
- `grupo_rubrica_subreport.jrxml`

These files should be restored from a complete source to ensure proper report functionality.