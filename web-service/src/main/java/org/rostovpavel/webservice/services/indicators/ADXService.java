package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.models.power.ADX.AverageDirectionalMovementIndex;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class ADXService implements IndicatorService {
    public static final int LENGHT_DAY = 14;
    private final ATRService atrService;

    @Override
    public AverageDirectionalMovementIndex getData(StocksDTO data) {
        List<BigDecimal> pubAT = atrService.getPubAT();

        List<BigDecimal> dmPlus = getDmPlus(data);
        List<BigDecimal> dmMinus = getDmMinus(data);
        List<BigDecimal> dxArr = new ArrayList<>();
        List<BigDecimal> dl14PArr = new ArrayList<>();
        List<BigDecimal> dl14MArr = new ArrayList<>();

        IntStream.range(0, LENGHT_DAY).forEach(i -> {

            BigDecimal aT14 = IntStream.range(i , LENGHT_DAY + i).mapToObj(pubAT::get).collect(Collectors.toList())
                   .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dmPlus14 = IntStream.range(i , LENGHT_DAY + i).mapToObj(dmPlus::get).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dmMinus14 = IntStream.range(i , LENGHT_DAY + i).mapToObj(dmMinus::get).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dl14P = dmPlus14.divide(aT14,5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
            BigDecimal dl14M = dmMinus14.divide(aT14,5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
            BigDecimal dl14Diff = dl14P.subtract(dl14M).abs();
            BigDecimal dl14Sum = dl14P.add(dl14M).abs();
            BigDecimal dx = dl14Diff.divide(dl14Sum,5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
            dxArr.add(dx);
            dl14PArr.add(dl14P);
            dl14MArr.add(dl14M);
        });

        BigDecimal adx = dxArr.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(dxArr.size()), 4, RoundingMode.HALF_UP);

        return AverageDirectionalMovementIndex.builder()
                .adx(adx)
                .dlP(dl14PArr.get(0))
                .dlM(dl14MArr.get(0))
                ._key(compareADXToBuySell(adx).getValue())
                .build();
    }

    private Signal compareADXToBuySell(@NotNull BigDecimal adx) {
        if((adx.compareTo(BigDecimal.valueOf(30)) > 0) && (adx.compareTo(BigDecimal.valueOf(40)) < 0)){
            return Signal.VAlSMALL;
        }
        if((adx.compareTo(BigDecimal.valueOf(40)) > 0) && (adx.compareTo(BigDecimal.valueOf(60)) < 0)){
            return Signal.VAlLOW;
        }
        if((adx.compareTo(BigDecimal.valueOf(60)) > 0) && (adx.compareTo(BigDecimal.valueOf(80)) < 0)){
            return Signal.VAlMEDIUM;
        }
        if(adx.compareTo(BigDecimal.valueOf(80)) > 0){
            return Signal.VAlHIGH;
        }
        return Signal.NONE;
    }

    @NotNull
    private List<BigDecimal> getDmPlus(@NotNull StocksDTO data) {
        return IntStream.range(0, data.getStocks().size() - 2).mapToObj(index -> {
            Stock cStock = data.getStocks().get(index);
            Stock pStock = data.getStocks().get(index + 1);
            BigDecimal subtractHigh = cStock.getHigh().subtract(pStock.getHigh()).setScale(5, RoundingMode.HALF_UP);
            BigDecimal subtractLow = pStock.getLow().subtract(cStock.getLow()).setScale(5, RoundingMode.HALF_UP);

            if ((subtractHigh.compareTo(subtractLow) > 0) && subtractHigh.compareTo(BigDecimal.valueOf(0)) > 0) {
                return subtractHigh;
            } else {
                return BigDecimal.ZERO;
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getDmMinus(@NotNull StocksDTO data) {
        return IntStream.range(0, data.getStocks().size() - 2).mapToObj(index -> {
            Stock cStock = data.getStocks().get(index);
            Stock pStock = data.getStocks().get(index + 1);
            BigDecimal subtractHigh = cStock.getHigh().subtract(pStock.getHigh()).setScale(5, RoundingMode.HALF_UP);
            BigDecimal subtractLow = pStock.getLow().subtract(cStock.getLow()).setScale(5, RoundingMode.HALF_UP);

            if ((subtractLow.compareTo(subtractHigh) > 0) && subtractLow.compareTo(BigDecimal.valueOf(0)) > 0) {
                return subtractLow;
            } else {
                return BigDecimal.ZERO;
            }
        }).collect(Collectors.toList());
    }
}
