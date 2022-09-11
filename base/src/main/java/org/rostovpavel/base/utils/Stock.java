package org.rostovpavel.base.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Stock {

    @Contract(value = " -> new", pure = true)
    @NotNull
    public static String @NotNull [] getNameTickers() {
        return new String[]{"ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "F", "AA", "CCL", "COIN", "ACH", "ET", "EQT", "FSLR", "MSTR", "PCG", "RRC", "SWN", "VIPS", "CNK", "CLOV", "MOMO", "PBF", "HOOD", "SPCE", "SAVE", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "IOVA", "GTHX", "RKLB", "DKNG", "PLUG", "BBBY", "RIDE", "NOK", "RIVN", "BLUE", "SQ", "WKHS", "ATRA", "ZYNE", "TGTX", "PRAX", "T", "MARA", "MU", "PINS", "GILD", "HAL", "CVX", "V","OII"};
//        "ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EPAM", "EQT", "FSLR", "MSTR", "PCG", "RRC", "REGI", "SEDG", "SWN", "SPOT", "VEON", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "FB", "NVDA", "SAVE", "TSLA", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI"
    }
}
