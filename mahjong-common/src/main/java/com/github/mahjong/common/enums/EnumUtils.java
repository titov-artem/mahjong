package com.github.mahjong.common.enums;

public class EnumUtils {

    /**
     * Replace enum value of class S on enum value with same name of class D
     *
     * @param val   source value
     * @param clazz destination enum class
     * @param <S>   source class
     * @param <D>   destination class
     * @return value of class D
     * @throws IllegalArgumentException if class D has no such value
     */
    public static <S extends Enum<S>, D extends Enum<D>> D transferClass(S val, Class<D> clazz) {
        return D.valueOf(clazz, val.name());
    }

}
