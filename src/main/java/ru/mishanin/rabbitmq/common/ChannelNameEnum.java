package ru.mishanin.rabbitmq.common;

import java.util.stream.Stream;

public enum ChannelNameEnum {

    BOOK,
    MOVIE,
    EDUCATION,
    GAME,
    NEWS;

    public static ChannelNameEnum getNameByOrdinal(int ordinal){
        return Stream.of(ChannelNameEnum.values())
                .filter(item->item.ordinal()==ordinal)
                .findFirst()
                .orElseThrow(()->new NullPointerException("Does not exist channel with number " + ordinal));
    }
}
