package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.exception.StockNotFoundException;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.models.move.AO.AwesomeOscillator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class AOService implements IndicatorService {
    public static final int SMA_5 = 5;
    public static final int SMA_34 = 34;


    @Override
    public AwesomeOscillator getData(@NotNull StocksDTO data) {
        List<Stock> stocks = new ArrayList<>(data.getStocks());
        List<BigDecimal> midPoint = stocks.stream().map(stock -> (stock.getHigh().add(stock.getLow())).divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)).collect(Collectors.toList());
        List<BigDecimal> sma_5 = getSMAIndex(midPoint, SMA_5);
        List<BigDecimal> sma_34 = getSMAIndex(midPoint, SMA_34);

        List<BigDecimal> ao = IntStream.range(0, sma_34.size() - 1).mapToObj(index -> sma_5.get(index).subtract(sma_34.get(index))).collect(Collectors.toList());
        List<BigDecimal> color = getColor(ao);
        List<BigDecimal> direction = getDirection(ao);
        List<Integer> swingHL = getSwingHiLow(ao, direction);
        List<BigDecimal> hl = getDateHL(ao, swingHL);

        return AwesomeOscillator.builder()
                .ao(ao.get(0).setScale(4, RoundingMode.HALF_UP))
                .color(setColor(color))
                .direction(setDirection(direction))
                ._key(getZeroCrossLine(direction).getValue())
                .saucerScanner(getSaucerScanner(color, direction).getValue())
                .twinPeakScanner(getTwinPeakScanner(hl, color, direction).getValue())
                .build();
    }


    private Signal getTwinPeakScanner(@NotNull List<BigDecimal> hl, List<BigDecimal> color, List<BigDecimal> direction) {
        if (hl.get(1).compareTo(hl.get(2)) != 0) {
            if ((direction.get(1).compareTo(BigDecimal.valueOf(1)) == 0)
                    && (hl.get(1).compareTo(hl.get(2)) < 0)
                    && (color.get(0).compareTo(BigDecimal.valueOf(0)) == 0)) {
                return Signal.SELLMINUS;
            }else{
                if ((direction.get(1).compareTo(BigDecimal.valueOf(0)) == 0)
                        && (hl.get(1).compareTo(hl.get(2)) > 0)
                        && (color.get(0).compareTo(BigDecimal.valueOf(1)) == 0)) {
                    return Signal.BUYPLUS;
                }
            }
        }
        return Signal.NONE;
    }


    private List<BigDecimal> getDateHL(@NotNull List<BigDecimal> ao, @NotNull List<Integer> swingHL){
        List<BigDecimal> collectAO = ao.stream().limit(swingHL.size()).collect(Collectors.toList());
        List<Integer> collectHL = new ArrayList<>(swingHL);
        Collections.reverse(collectAO);
        Collections.reverse(collectHL);

        AtomicReference<Double> temp = new AtomicReference<>(0.0);
        List<BigDecimal> hL = IntStream.range(0, collectHL.size()).mapToObj(index -> {
            if (collectHL.get(index) == 0) {
                return BigDecimal.valueOf(temp.get());
            } else {
                temp.set(collectAO.get(index).doubleValue());
                return collectAO.get(index);
            }
        }).collect(Collectors.toList());
        Collections.reverse(hL);
        return hL;
    }

    @NotNull
    private List<Integer> getSwingHiLow(@NotNull List<BigDecimal> ao, List<BigDecimal> direction) {
        return IntStream.range(0, ao.size() - 10).mapToObj(index -> {
            if (index == 0) {
                if (direction.get(index).compareTo(BigDecimal.valueOf(0)) == 0) {
                    if ((ao.get(index).compareTo(ao.get(index + 1)) < 0)
                            && (ao.get(index).compareTo(ao.get(index + 2)) < 0)) {
                        return 1;
                    } else {
                        return 0;
                    }

                } else {
                    if ((ao.get(index).compareTo(ao.get(index + 1)) > 0)
                            && (ao.get(index).compareTo(ao.get(index + 2)) > 0)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else {
                if (direction.get(index).compareTo(BigDecimal.valueOf(0)) == 0) {
                    if ((ao.get(index).compareTo(ao.get(index + 1)) < 0)
                            && (ao.get(index).compareTo(ao.get(index + 2)) < 0)
                            && (ao.get(index).compareTo(ao.get(index - 1)) < 0)) {
                        return 1;
                    } else {
                        return 0;
                    }

                } else {
                    if ((ao.get(index).compareTo(ao.get(index + 1)) > 0)
                            && (ao.get(index).compareTo(ao.get(index + 2)) > 0)
                            && (ao.get(index).compareTo(ao.get(index - 1)) > 0)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }).collect(Collectors.toList());
    }

    private Signal getSaucerScanner(List<BigDecimal> color, @NotNull List<BigDecimal> direction) {

        if (direction.get(0).compareTo(BigDecimal.valueOf(1)) == 0) {
            if ((color.get(1).compareTo(BigDecimal.valueOf(0)) == 0)
                    && (color.get(2).compareTo(BigDecimal.valueOf(0)) == 0)) {
                if (color.get(0).compareTo(BigDecimal.valueOf(1)) == 0) {
                    return Signal.BUYPLUS;
                }
            }
        } else {
            if ((color.get(1).compareTo(BigDecimal.valueOf(1)) == 0)
                    && (color.get(2).compareTo(BigDecimal.valueOf(1)) == 0)) {
                if (color.get(0).compareTo(BigDecimal.valueOf(0)) == 0) {
                    return Signal.SELLMINUS;
                }
            }
        }
        return Signal.NONE;
    }

    private @NotNull String setDirection(@NotNull List<BigDecimal> data) {
        return data.get(0).compareTo(BigDecimal.valueOf(1)) == 0 ? "Up" : "Down";
    }

    private @NotNull String setColor(@NotNull List<BigDecimal> data) {
        return data.get(0).compareTo(BigDecimal.valueOf(1)) == 0 ? "Green" : "Red";
    }

    private Signal getZeroCrossLine(@NotNull List<BigDecimal> data) {
        if (data.get(1).compareTo(BigDecimal.valueOf(1)) == 0) {
            if (data.get(0).compareTo(BigDecimal.valueOf(0)) == 0) {
                return Signal.SELL;
            }
        } else {
            if (data.get(0).compareTo(BigDecimal.valueOf(1)) == 0) {
                return Signal.BUY;
            }
        }
        return Signal.NONE;
    }

    @NotNull
    private List<BigDecimal> getDirection(@NotNull List<BigDecimal> ao) {
        return IntStream.range(0, ao.size() - 2).mapToObj(index -> {
            if (ao.get(index).compareTo(BigDecimal.valueOf(0)) > 0) {
                return BigDecimal.ONE;
            } else {
                return BigDecimal.ZERO;
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getColor(@NotNull List<BigDecimal> ao) {
        return IntStream.range(0, ao.size() - 2).mapToObj(index -> {
            BigDecimal current = ao.get(index);
            BigDecimal prev = ao.get(index + 1);
            if (current.compareTo(prev) > 0) {
                return BigDecimal.ONE;
            } else {
                return BigDecimal.ZERO;
            }
        }).collect(Collectors.toList());
    }


    private List<BigDecimal> getSMAIndex(@NotNull List<BigDecimal> data, int len) {
        return IntStream.range(0, data.size() - len).mapToObj(index -> {
            return IntStream.range(index, len + index)
                    .mapToObj(data::get)
                    .collect(Collectors.toList())
                    .stream()
                    .reduce(BigDecimal::add)
                    .orElseThrow(() -> new StockNotFoundException("Error MA"))
                    .divide(BigDecimal.valueOf(len), 5, RoundingMode.HALF_UP);
        }).collect(Collectors.toList());
    }
}
