package org.rostovpavel.base.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Stock {

    @Contract(value = " -> new", pure = true)
    @NotNull
    public static String @NotNull [] getNameTickers() {
        return new String[]{"ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EQT", "FSLR", "MSTR", "PCG", "RRC", "SWN", "SPOT", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "NVDA", "SAVE", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "IOVA", "GTHX", "RKLB", "DKNG", "PLUG", "BBBY", "RIDE", "NOK", "RIVN", "TSLA", "BLUE", "SQ", "EAR", "AWH"};
//        "ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EPAM", "EQT", "FSLR", "MSTR", "PCG", "RRC", "REGI", "SEDG", "SWN", "SPOT", "VEON", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "FB", "NVDA", "SAVE", "TSLA", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI"
    }
}
