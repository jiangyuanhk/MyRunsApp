package com.dartmouth.yuanjiang.myruns_2;

/**
 * Created by bertrandding on 2/14/15.
 */
public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N1db3a2e10(i);
        return p;
    }

    static double N1db3a2e10(Object[] i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 0;
        } else if (((Double) i[64]).doubleValue() <= 2.618203) {
            p = WekaClassifier.N1b64625d3(i);
        } else if (((Double) i[64]).doubleValue() > 2.618203) {
            p = WekaClassifier.N4a78aba31(i);
        }
        return p;
    }

    static double N1b64625d3(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 9.835219) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 9.835219) {
            p = WekaClassifier.N311377b92(i);
        }
        return p;
    }

    static double N311377b92(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 46.362164) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 46.362164) {
            p = WekaClassifier.N2ab152a3b(i);
        }
        return p;
    }

    static double N2ab152a3b(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 2.289696) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 2.289696) {
            p = 2;
        }
        return p;
    }

    static double N4a78aba31(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 260.132513) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 260.132513) {
            p = WekaClassifier.N4844cdb65(i);
        }
        return p;
    }

    static double N4844cdb65(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 1;
        } else if (((Double) i[16]).doubleValue() <= 0.346775) {
            p = 1;
        } else if (((Double) i[16]).doubleValue() > 0.346775) {
            p = 2;
        }
        return p;
    }
}
