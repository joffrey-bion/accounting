# How to add new government parameters

The values used in this tool are provided by the Government every year, and need to be added in order for the tool to
support additional tax years. To add a new year, follow the instructions below.

## Regular wage tax and tax credits

All explanations, formulae, and parameter values can be found in a PDF document called "Calculation rules for the
automated payroll administration". Since 2022, the appendix with only the table for parameter values is available
individually too.

To find the PDF for a given year, follow the instructions below:

1. Go to the [brochures and publications search page][1]
2. Select "Werkgever" ("Employer", because it's for employers to compute the wage to give to employees)
3. Select "Loonbelastingtabellen" ("Wage tax tables")
4. Click on the link for the desired year
5. Pick the PDF called "Bijlage parameterwaarden [...]" ("Appendix parameter values [...]") to get the big table
   summary with all parameter values.
6. Update [NLTaxValues.kt](../src/commonMain/kotlin/org/hildan/accounting/taxes/values/NLTaxValues.kt) by adding one
   year to the `when` statement, and specify all the values according to the parameters from the table. Some parameters
   from the government are actually redundant, so only a subset of them are required by this tool.

## Special rewards tax rates

The tax rates for bonuses or special rewards ("bijzondere beloningen") are in a separate table. To get it:

1. Go to the [table search page][2] on the government's website
2. Select the year
3. Select `Nederland` (The Netherlands)
4. Select `Tabel voor bijzondere beloningen` (table for special rewards)
5. Select `Standaardsituatie` (standard situation)
6. Select `Wit` (White) table. This is because this is the standard one, for wages from the present employment;
   the green ones are for past employment.
7. Select PDF or Excel format as you prefer

> **FIXME:** the instructions below are flawed: we're only taking the rate for a specific range of salary but the tool should
support every possible salary. We should instead get all rates for all salary ranges.

How to read the table:
    
* The tool only uses values for people younger than the state pension age (AOW-leeftijd) which is ~66 years old,
  so only consider the columns under `Jonger dan AOW-leeftijd`
* Look at the line corresponding to the biggest threshold exceeded by the gross annual salary for the year (only
  the taxable part, so -30% if applicable)
* Look at the 2 columns under `met loonheffingskorting`
* Add the discount column (`verrekeningspercentage loonheffingskorting`) to the standard rate column (`standaardtarief`)
* This gives the total percentage, for example 49.5% + 6% = 55.5% in 2021

[1]: https://www.belastingdienst.nl/wps/wcm/connect/bldcontentnl/themaoverstijgend/brochures_en_publicaties/brochures_en_publicaties_werkgever
[2]: https://www.belastingdienst.nl/wps/wcm/connect/nl/personeel-en-loon/content/hulpmiddel-loonbelastingtabellen
