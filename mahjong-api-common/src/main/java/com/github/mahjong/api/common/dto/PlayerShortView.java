package com.github.mahjong.api.common.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PlayerShortView {
    @NotNull
    public Long id;
    @NotNull
    public String name;

    @Override
    public String toString() {
        return "PlayerShortView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerShortView that = (PlayerShortView) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
