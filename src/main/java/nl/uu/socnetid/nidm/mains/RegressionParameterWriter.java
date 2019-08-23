package nl.uu.socnetid.nidm.mains;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.LogParameters;
import nl.uu.socnetid.nidm.data.LogProperties;

/**
 * @author Hendrik Nunner
 */
public class RegressionParameterWriter {

    /**
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {

        StringBuilder sbDeclaration = new StringBuilder();
        StringBuilder sbModel = new StringBuilder();
        List<String> params = new LinkedList<String>();
        int tabWidth1 = 35;
        int tabWidth2 = 44;

        sbDeclaration.append("# MAIN EFFECTS\n# CIDM parameters\n");
        sbModel.append("# CIDM parameters\n");
        for (LogParameters logParameter : LogParameters.getRegressionParameters()) {

            String paramFull = logParameter.toString();
            String paramName = paramFull.replace("param.cidm.", "").replace(".av", "");
            params.add(paramName);
            sbModel.append(paramName).append(" +\n");

            sbDeclaration.append(paramName);
            for (int i = tabWidth1-paramName.length(); i > 0; i--) {
                sbDeclaration.append(" ");
            }
            sbDeclaration.append("<- meanCenter(ssData$").append(paramFull);
            if (paramFull.equals("param.cidm.N") ||
                    paramFull.equals("param.cidm.sigma.av")) {
                sbDeclaration.append(" / 50");
            }
            sbDeclaration.append(")\n");
        }

        sbDeclaration.append("# network properties\n");
        sbModel.append("# network properties\n");
        for (LogProperties logProperty : LogProperties.getRegressionProperties()) {

            String propertyFull = logProperty.toString();
            String propertyName = propertyFull.replace("prop.", "").replace("net.", "").replace(".pre.epidemic", "");
            params.add(propertyName);
            sbModel.append(propertyName).append(" +\n");

            sbDeclaration.append(propertyName);
            for (int i = tabWidth1-propertyName.length(); i > 0; i--) {
                sbDeclaration.append(" ");
            }
            sbDeclaration.append("<- meanCenter(ssData$").append(propertyFull).append(")\n");
        }

        sbDeclaration.append("\n# INTERACTION EFFECTS\n");
        sbModel.append("# interaction effects\n");
        while (!params.isEmpty()) {
            String param = params.get(0);
            params.remove(0);
            if (!params.isEmpty()) {
                sbDeclaration.append("# combinations of ").append(param).append("\n");
                List<String> params2 = new LinkedList<String>(params);
                Iterator<String> it = params2.iterator();
                while (it.hasNext()) {
                    String param2 = it.next();
                    String intName = param + ".X." + param2;
                    sbDeclaration.append(intName);
                    sbModel.append(intName).append(" +\n");
                    for (int i = tabWidth1-intName.length(); i > 0; i--) {
                        sbDeclaration.append(" ");
                    }
                    String intExpr = "<- (" + param + " - mean(" + param + ")";
                    sbDeclaration.append(intExpr);
                    for (int i = tabWidth2-intExpr.length(); i > 0; i--) {
                        sbDeclaration.append(" ");
                    }
                    sbDeclaration.append("*  (").append(param2).append(" - mean(").append(param2).append(")))\n");
                }
            }
        }

        System.out.println(sbDeclaration.toString());

        System.out.println(sbModel.toString());

    }

}
